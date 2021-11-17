package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByRatingBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.repository.model.v2.response.store.StoreList
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByRatingRecyclerAdapter(
    private val listener: OnItemClickListener<StoreInfo>
) : RecyclerView.Adapter<SearchByRatingViewHolder>() {
    private val items = arrayListOf<StoreInfo>()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchByRatingViewHolder {
        return SearchByRatingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SearchByRatingViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    fun submitList(newItems: List<StoreInfo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SearchByRatingViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByRatingBinding, StoreInfo>(R.layout.item_store_by_rating, parent) {

    override fun bind(item: StoreInfo, listener: OnItemClickListener<StoreInfo>?) {
        super.bind(item, listener)

        val categoryInfo = SharedPrefUtils.getCategories()
        val categories = item.categories.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }
        binding.tvCategory.text = categories
    }
}