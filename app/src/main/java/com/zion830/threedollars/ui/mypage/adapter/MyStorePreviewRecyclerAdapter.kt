package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyRestaurantShowAllBinding
import com.zion830.threedollars.repository.model.response.Store
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyStorePreviewRecyclerAdapter(
    private val showAllClickEvent: OnItemClickListener<Store>
) : ListAdapter<Store, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Store?>() {
    override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_SHOW_ALL -> MyStoreShowAllViewHolder(parent)
        else -> MyStoreViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyStoreViewHolder -> holder.bind(getItem(position), null)
            is MyStoreShowAllViewHolder -> holder.bind(getItem(position), showAllClickEvent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount - 1) VIEW_TYPE_STORE else VIEW_TYPE_SHOW_ALL
    }

    override fun submitList(list: MutableList<Store>?) {
        if (!list.isNullOrEmpty()) {
            super.submitList(list.plus(Store()))
        } else {
            super.submitList(list)
        }
    }

    companion object {
        private const val VIEW_TYPE_STORE = 1
        private const val VIEW_TYPE_SHOW_ALL = 2
    }
}

class MyStoreViewHolder(parent: ViewGroup) : BaseViewHolder<ItemMyRestaurantShowAllBinding, Store>(R.layout.item_my_restaurant_preview, parent) {}

class MyStoreShowAllViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemMyRestaurantShowAllBinding, Store>(R.layout.item_my_restaurant_show_all, parent) {}