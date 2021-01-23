package com.zion830.threedollars.ui.store_detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByDistanceBinding
import com.zion830.threedollars.repository.model.response.StoreList
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByDistanceRecyclerAdapter(
    private val listener: OnItemClickListener<StoreList>
) : ListAdapter<StoreList, SearchByDistanceViewHolder>(object : DiffUtil.ItemCallback<StoreList?>() {
    override fun areItemsTheSame(oldItem: StoreList, newItem: StoreList): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: StoreList, newItem: StoreList): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByDistanceViewHolder {
        return SearchByDistanceViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchByDistanceViewHolder, position: Int) {
        holder.setBackgroundByPosition(position, position == itemCount - 1)
        holder.bind(getItem(position), listener)
    }
}

class SearchByDistanceViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByDistanceBinding, StoreList>(R.layout.item_store_by_distance, parent) {

    override fun bind(item: StoreList, listener: OnItemClickListener<StoreList>?) {
        super.bind(item, listener)
        binding.tvStoreName.text = item.storeName
    }

    fun setBackgroundByPosition(position: Int, isLastIndex: Boolean) {
//        if (position % 2 == 1 && !isLastIndex) {
//            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_gray2))
//        } else if (position % 2 == 1 && isLastIndex) {
//            binding.layoutItem.setBackgroundResource(R.drawable.rect_gray_corner_bottom)
//        }
    }
}