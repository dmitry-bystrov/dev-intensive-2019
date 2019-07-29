package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.roundToInt


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2f
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private val borderBounds = RectF()
    private val bitmapDrawBounds = RectF()
    private var bitmapSource: Bitmap? = null
    private var bitmapShader: BitmapShader? = null
    private val shaderMatrix = Matrix()
    private val bitmapPaint: Paint
    private val borderPaint: Paint
    private val supportedScaleType = ScaleType.CENTER_CROP
    private var paintIsReady = false

    init {
        super.setScaleType(supportedScaleType)
        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        paintIsReady = true

        attrs?.let {
            val arr = context.obtainStyledAttributes(it, R.styleable.CircleImageView)
            val defBorderWidth = DEFAULT_BORDER_WIDTH * resources.displayMetrics.density
            borderColor = arr.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = arr.getDimension(R.styleable.CircleImageView_cv_borderWidth, defBorderWidth)
            arr.recycle()
        }

        borderPaint.style = Paint.Style.STROKE
        updateBorderColor()
        updateBorderWidth()
        setupBitmap()
    }

    private fun updateBorderColor() {
        borderPaint.color = borderColor
    }

    private fun updateBorderWidth() {
        borderBounds.set(bitmapDrawBounds)
        borderBounds.inset(borderWidth / 2f, borderWidth / 2f)
        borderPaint.strokeWidth = borderWidth
    }

    override fun getScaleType(): ScaleType {
        return supportedScaleType
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != supportedScaleType) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        setupBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setupBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        setupBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setupBitmap()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        drawable?.let {
            val bitmap = Bitmap.createBitmap(
                it.intrinsicWidth,
                it.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)

            return bitmap
        }

        return null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setupSize()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setupSize()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setupSize()
    }

    private fun setupSize() {
        updateCircleDrawBounds(bitmapDrawBounds)
        borderBounds.set(bitmapDrawBounds)
        borderBounds.inset(borderWidth / 2f, borderWidth / 2f)
        updateBitmapSize()
        invalidate()
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()

        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun setupBitmap() {
        bitmapSource = getBitmapFromDrawable(drawable)

        if (!paintIsReady) {
            return
        }

        bitmapSource?.let {
            bitmapShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            bitmapPaint.shader = bitmapShader
        }

        updateBitmapSize()
        invalidate()
    }

    private fun updateBitmapSize() {
        bitmapSource?.let {
            val dx: Float
            val dy: Float
            val scale: Float

            if (it.width < it.height) {
                scale = bitmapDrawBounds.width() / it.width.toFloat()
                dx = bitmapDrawBounds.left
                dy = bitmapDrawBounds.top - it.height * scale / 2f + bitmapDrawBounds.width() / 2f
            } else {
                scale = bitmapDrawBounds.height() / it.height.toFloat()
                dx = bitmapDrawBounds.left - it.width * scale / 2f + bitmapDrawBounds.width() / 2f
                dy = bitmapDrawBounds.top
            }

            shaderMatrix.setScale(scale, scale)
            shaderMatrix.postTranslate(dx, dy)
            bitmapShader?.setLocalMatrix(shaderMatrix)
        }
    }

    @Dimension
    fun getBorderWidth(): Int = (borderWidth / resources.displayMetrics.density).roundToInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = dp * resources.displayMetrics.density
        updateBorderWidth()
        invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        updateBorderColor()
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = resources.getColor(colorId, null)
        updateBorderColor()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        drawBitmap(canvas)
        drawStroke(canvas)
    }

    private fun drawStroke(canvas: Canvas?) {
        if (borderPaint.strokeWidth > 0f) {
            canvas?.drawOval(borderBounds, borderPaint)
        }
    }

    private fun drawBitmap(canvas: Canvas?) {
        canvas?.drawOval(bitmapDrawBounds, bitmapPaint)
    }
}
