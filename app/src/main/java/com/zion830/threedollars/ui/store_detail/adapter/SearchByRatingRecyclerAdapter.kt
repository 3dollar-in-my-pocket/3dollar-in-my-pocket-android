package com.zion830.threedollars.ui.store_detail.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByRatingBinding
import com.zion830.threedollars.repository.model.response.StoreList
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByRatingRecyclerAdapter(
    private val listener: OnItemClickListener<StoreList>
) : RecyclerView.Adapter<SearchByRatingViewHolder>() {
    private val items = arrayListOf<StoreList>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByRatingViewHolder {
        return SearchByRatingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchByRatingViewHolder, position: Int) {
        Log.d("??", items[position].toString() + " " + position)
        holder.setBackgroundByPosition(position, position == itemCount - 1)
        holder.bind(items[position], listener)
    }

    fun submitList(newItems: List<StoreList>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SearchByRatingViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByRatingBinding, StoreList>(R.layout.item_store_by_rating, parent) {

    fun setBackgroundByPosition(position: Int, isLastIndex: Boolean) {
        if (position % 2 == 1 && !isLastIndex) {
            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_gray3))
        } else if (position % 2 == 1 && isLastIndex) {
            binding.layoutItem.setBackgroundResource(R.drawable.rect_gray_corner_bottom)
        } else if (position % 2 == 0 && !isLastIndex) {
            binding.layoutItem.setBackgroundColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.color_white))
        }
    }
}