package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyRestaurantBinding
import com.zion830.threedollars.repository.model.response.Store
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyStoreRecyclerAdapter(
    private val listener: OnItemClickListener<StoreInfo>
) : PagedListAdapter<StoreInfo, BaseViewHolder<ItemMyRestaurantBinding, StoreInfo>>(object : DiffUtil.ItemCallback<StoreInfo?>() {
    override fun areItemsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyRestaurantBinding, StoreInfo>(R.layout.item_my_restaurant, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyRestaurantBinding, StoreInfo>, position: Int) {
        holder.bind(getItem(position) ?: StoreInfo(), listener)
    }
}