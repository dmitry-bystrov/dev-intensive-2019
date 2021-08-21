package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import android.graphics.drawable.Drawable

class TextDrawable(bgColor: Int = Color.GRAY, private val text: String, size: Int) : Drawable() {
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        textPaint.color = Color.WHITE
        textPaint.textSize = size.toFloat() / 2f
        textPaint.textAlign = Paint.Align.CENTER
        backgroundPaint.color = bgColor
        backgroundPaint.style = Paint.Style.FILL
        bounds = Rect(0, 0, size, size)
    }

    override fun getIntrinsicWidth(): Int {
        return bounds.width()
    }

    override fun getIntrinsicHeight(): Int {
        return bounds.height()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, backgroundPaint)
        canvas.drawText(
            text,
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat() - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        backgroundPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        backgroundPaint.colorFilter = colorFilter
    }
}