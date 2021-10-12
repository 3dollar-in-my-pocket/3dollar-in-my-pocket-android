package com.zion830.threedollars.ui.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreLocationBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.mypage.adapter.bindMenuIcons
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class NearStoreRecyclerAdapter(
    private val clickListener: OnItemClickListener<StoreInfo>
) : ListAdapter<StoreInfo, NearStoreViewHolder>(object : DiffUtil.ItemCallback<StoreInfo?>() {
    override fun areItemsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: StoreInfo, newItem: StoreInfo): Boolean = oldItem == newItem
}) {
    var focusedIndex = 0

    fun getItemLocation(position: Int) = LatLng(getItem(position).latitude, getItem(position).longitude)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearStoreViewHolder {
        return NearStoreViewHolder(parent)
    }

    override fun onBindViewHolder(holder: NearStoreViewHolder, position: Int) {
        holder.bind(getItem(position), listener = clickListener)
        holder.bindPosition(focusedIndex == position)
    }
}

class NearStoreViewHolder(parent: ViewGroup?) : BaseViewHolder<ItemStoreLocationBinding, StoreInfo>(R.layout.item_store_location, parent) {

    fun bindPosition(isSelected: Boolean) {
        binding.isSelectedItem = isSelected
    }

    override fun bind(item: StoreInfo, listener: OnItemClickListener<StoreInfo>?) {
        super.bind(item, listener)
        binding.item = item

        val catgories = item.categories.joinToString { "#${binding.tvCategory.context.getString(MenuType.of(it).displayNameId)} " }
        binding.tvDistance.text = if (item.distance < 1000) "${item.distance}m" else "1km+"
        binding.tvStoreName.text = item.storeName
        binding.ivMenuIcon.bindMenuIcons(item.categories)
        binding.tvRating.text = "${item.rating}점"
        binding.tvCategory.text = catgories
    }
}