package zion830.com.common.ext

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSingleClickListener

const val MIN_CLICK_DELAY_MS = 500L

fun <T> OnItemClickListener<T>.disableDoubleClick(
    clickDelayMilliSeconds: Long = MIN_CLICK_DELAY_MS
): OnItemClickListener<T> = object : OnSingleClickListener<T>(clickDelayMilliSeconds) {
    override fun onSingleClick(item: T) {
        this@disableDoubleClick.onClick(item)
    }
}

fun OnItemClickListener<View>.toViewListener(
    clickDelayMilliSeconds: Long = MIN_CLICK_DELAY_MS
): View.OnClickListener = View.OnClickListener {
    this.onClick(it)
}

fun View.OnClickListener.toItemListener(
    clickDelayMilliSeconds: Long = MIN_CLICK_DELAY_MS
): OnItemClickListener<View> = object : OnItemClickListener<View> {
    override fun onClick(item: View) {
        this@toItemListener.onClick(item)
    }
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}