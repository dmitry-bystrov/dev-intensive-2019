@file:Suppress("unused")

package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager


fun Activity.hideKeyboard() {
    val view: View? = findViewById(android.R.id.content)
    view?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.isKeyboardOpen(): Boolean {
    val (windowSize, visibleFrame) = getDimensions(this)
    return windowSize.y > visibleFrame.bottom
}

fun Activity.isKeyboardClosed(): Boolean {
    val (windowSize, visibleFrame) = getDimensions(this)
    return windowSize.y == visibleFrame.bottom
}

private fun getDimensions(activity: Activity): Pair<Point, Rect> {
    val size = Point()
    val rect = Rect()
    activity.windowManager.defaultDisplay.getSize(size)
    activity.window.decorView.getWindowVisibleDisplayFrame(rect)

    return size to rect
}