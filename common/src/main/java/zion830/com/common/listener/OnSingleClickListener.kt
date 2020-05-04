package zion830.com.common.listener

import zion830.com.common.ext.MIN_CLICK_DELAY_MS

abstract class OnSingleClickListener<T>(
    private val clickDelayMilliSeconds: Long = MIN_CLICK_DELAY_MS
) : OnItemClickListener<T> {
    private var lastClickTime = 0L

    override fun onClick(item: T) {
        val now = System.currentTimeMillis()
        val clickGap = now - lastClickTime
        if (clickGap > clickDelayMilliSeconds) {
            onSingleClick(item)
            lastClickTime = now
        }
    }

    abstract fun onSingleClick(item: T)
}