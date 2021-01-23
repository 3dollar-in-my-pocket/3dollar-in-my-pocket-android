package com.zion830.threedollars.ui.store_detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByRatingBinding
import com.zion830.threedollars.repository.model.response.StoreList
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByRatingRecyclerAdapter(
    private val listener: OnItemClickListener<StoreList>
) : ListAdapter<StoreList, SearchByRatingViewHolder>(object : DiffUtil.ItemCallback<StoreList?>() {
    override fun areItemsTheSame(oldItem: StoreList, newItem: StoreList): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: StoreList, newItem: StoreList): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByRatingViewHolder {
        return SearchByRatingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchByRatingViewHolder, position: Int) {
        holder.setBackgroundByPosition(position, position == itemCount - 1)
        holder.bind(getItem(position), listener)
    }
}

class SearchByRatingViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByRatingBinding, StoreList>(R.layout.item_store_by_rating, parent) {

    fun setBackgroundByPosition(position: Int, isLastIndex: Boolean) {
//        if (position % 2 == 1 && !isLastIndex) {
//            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_gray2))
//        } else if (position % 2 == 1 && isLastIndex) {
//            binding.layoutItem.setBackgroundResource(R.drawable.rect_gray_corner_bottom)
//        }
    }
}