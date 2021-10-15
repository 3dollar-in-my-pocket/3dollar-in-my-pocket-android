package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyRestaurantPreviewBinding
import com.zion830.threedollars.databinding.ItemMyRestaurantShowAllBinding
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyStorePreviewRecyclerAdapter(
    private val storeInfoClickEvent: OnItemClickListener<StoreInfo>,
    private val showAllClickEvent: OnItemClickListener<StoreInfo>
) : ListAdapter<StoreInfo, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<StoreInfo?>() {
    override fun areItemsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_SHOW_ALL -> MyStoreInfoShowAllViewHolder(parent)
        else -> MyStoreInfoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyStoreInfoViewHolder -> {
                holder.bind(getItem(position), storeInfoClickEvent)
            }
            is MyStoreInfoShowAllViewHolder -> {
                holder.bind(getItem(position), showAllClickEvent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount - 1) VIEW_TYPE_StoreInfo else VIEW_TYPE_SHOW_ALL
    }

    override fun submitList(list: List<StoreInfo>?) {
        if (!list.isNullOrEmpty()) {
            super.submitList(list.plus(StoreInfo()))
        } else {
            super.submitList(list)
        }
    }

    companion object {
        private const val VIEW_TYPE_StoreInfo = 1
        private const val VIEW_TYPE_SHOW_ALL = 2
    }
}

class MyStoreInfoViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemMyRestaurantPreviewBinding, StoreInfo>(R.layout.item_my_restaurant_preview, parent) {}

class MyStoreInfoShowAllViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemMyRestaurantShowAllBinding, StoreInfo>(R.layout.item_my_restaurant_show_all, parent) {}