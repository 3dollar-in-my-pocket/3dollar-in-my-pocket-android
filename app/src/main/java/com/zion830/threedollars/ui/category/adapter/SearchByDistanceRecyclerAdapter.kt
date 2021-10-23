package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByDistanceBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.StoreList
import com.zion830.threedollars.utils.SharedPrefUtils
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

        val categoryInfo = SharedPrefUtils.getCategories()
        val categories = item.categories.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }
        binding.tvCategory.text = categories
    }
}