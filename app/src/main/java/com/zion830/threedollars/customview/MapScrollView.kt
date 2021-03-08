package com.zion830.threedollars.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView


class MapScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ScrollView(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //return super.onTouchEvent(ev)
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> super.onTouchEvent(ev)
            MotionEvent.ACTION_MOVE -> false
            MotionEvent.ACTION_CANCEL -> super.onTouchEvent(ev)
            MotionEvent.ACTION_UP -> false
            else -> super.onTouchEvent(ev)
        }
    }
}