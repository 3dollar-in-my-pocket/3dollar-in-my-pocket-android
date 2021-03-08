package com.zion830.threedollars.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import android.widget.FrameLayout

interface OnMapTouchListener {
    fun onTouch()
}

class TouchableWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val onTouchListener: OnMapTouchListener? = null
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchListener?.onTouch()
            MotionEvent.ACTION_UP -> onTouchListener?.onTouch()
        }
        return super.dispatchTouchEvent(event)
    }
}