package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByRatingBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.StoreList
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
        holder.bind(items[position], listener)
    }

    fun submitList(newItems: List<StoreList>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SearchByRatingViewHolder(parent: ViewGroup) : BaseViewHolder<ItemStoreByRatingBinding, StoreList>(R.layout.item_store_by_rating, parent) {

    override fun bind(item: StoreList, listener: OnItemClickListener<StoreList>?) {
        super.bind(item, listener)
        val categoryInfo = "#${binding.tvCategory.context.getString(MenuType.of(item.categories.firstOrNull()).displayNameId)}"
        binding.tvCategory.text = categoryInfo
    }
}