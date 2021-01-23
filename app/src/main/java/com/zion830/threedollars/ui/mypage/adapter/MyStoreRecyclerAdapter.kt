package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyRestaurantBinding
import com.zion830.threedollars.repository.model.response.Store
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyStoreRecyclerAdapter(
    private val listener: OnItemClickListener<Store>
) : PagedListAdapter<Store, BaseViewHolder<ItemMyRestaurantBinding, Store>>(object : DiffUtil.ItemCallback<Store?>() {
    override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyRestaurantBinding, Store>(R.layout.item_my_restaurant, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyRestaurantBinding, Store>, position: Int) {
        holder.bind(getItem(position) ?: Store(), listener)
    }
}