package com.zion830.threedollars.ui.store_detail.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByDistanceBinding
import com.zion830.threedollars.repository.model.response.StoreList
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByDistanceRecyclerAdapter(
    private val listener: OnItemClickListener<StoreList>
) : RecyclerView.Adapter<SearchByDistanceViewHolder>() {
    private val items = arrayListOf<StoreList>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByDistanceViewHolder {
        return SearchByDistanceViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchByDistanceViewHolder, position: Int) {
        holder.setBackgroundByPosition(position, position == itemCount - 1)
        holder.bind(items[position], listener)
    }

    fun submitList(newItems: List<StoreList>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SearchByDistanceViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByDistanceBinding, StoreList>(R.layout.item_store_by_distance, parent) {

    override fun bind(item: StoreList, listener: OnItemClickListener<StoreList>?) {
        super.bind(item, listener)
        binding.tvStoreName.text = item.storeName
    }

    fun setBackgroundByPosition(position: Int, isLastIndex: Boolean) {
        if (position % 2 == 1 && !isLastIndex) {
            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_gray3))
        } else if (position % 2 == 1 && isLastIndex) {
            binding.layoutItem.setBackgroundResource(R.drawable.rect_gray_corner_bottom)
        } else if (position % 2 == 0 && !isLastIndex) {
            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_white))
        } else {
            binding.layoutItem.setBackgroundResource(R.drawable.rect_white_bottom_corner)
        }
    }
}