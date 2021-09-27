package com.zion830.threedollars.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreLocationBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.AllStoreResponseItem
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class NearStoreRecyclerAdapter(
    private val clickListener: OnItemClickListener<AllStoreResponseItem>
) : ListAdapter<AllStoreResponseItem, NearStoreViewHolder>(object : DiffUtil.ItemCallback<AllStoreResponseItem?>() {
    override fun areItemsTheSame(oldItem: AllStoreResponseItem, newItem: AllStoreResponseItem): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AllStoreResponseItem, newItem: AllStoreResponseItem): Boolean = oldItem == newItem
}) {
    var focusedIndex = 0

    fun getItemLocation(position: Int) = LatLng(getItem(position).latitude, getItem(position).longitude)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearStoreViewHolder {
        return NearStoreViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NearStoreViewHolder, position: Int) {
        holder.bindPosition(focusedIndex == position)
        holder.bind(getItem(position), listener = clickListener)
    }
}

class NearStoreViewHolder(parent: ViewGroup?) : BaseViewHolder<ItemStoreLocationBinding, AllStoreResponseItem>(R.layout.item_store_location, parent) {

    fun bindPosition(isSelected: Boolean) {
        binding.isSelectedItem = isSelected
    }

    override fun bind(item: AllStoreResponseItem, listener: OnItemClickListener<AllStoreResponseItem>?) {
        super.bind(item, listener)
        val categoryInfo = "#${binding.tvCategory.context.getString(MenuType.of(item.category).displayNameId)}"
        binding.tvCategory.text = categoryInfo
    }
}