package zion830.com.common.base

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import zion830.com.common.ext.disableDoubleClick
import zion830.com.common.listener.OnItemClickListener

abstract class BaseRecyclerView<B : ViewDataBinding, T : Any>(
    @LayoutRes private val layoutResId: Int
) : ListAdapter<T, BaseViewHolder<B, T>>(object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}) {

    var clickListener = object : OnItemClickListener<T> {
        override fun onClick(item: T) {
            Log.d("list item clicked", item.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = object : BaseViewHolder<B, T>(layoutResId, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<B, T>, position: Int) {
        holder.bind(getItem(position), clickListener.disableDoubleClick())
    }
}