package zion830.com.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import zion830.com.common.BR
import zion830.com.common.listener.OnItemClickListener

abstract class BaseViewHolder<B : ViewDataBinding, T>(
    @LayoutRes layoutResId: Int,
    parent: ViewGroup?
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent?.context).inflate(layoutResId, parent, false)
) {
    val binding: B = DataBindingUtil.bind(itemView)!!

    open fun bind(item: T, listener: OnItemClickListener<T>?) {
        try {
            binding.run {
                setVariable(BR.item, item)
                setVariable(BR.listener, listener)
                executePendingBindings()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}