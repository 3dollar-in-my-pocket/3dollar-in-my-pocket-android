package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreByRatingBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByRatingRecyclerAdapter(
    private val listener: OnItemClickListener<StoreInfo>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = arrayListOf<AdAndStoreItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Popups -> {
                VIEW_TYPE_AD
            }
            is StoreInfo -> {
                VIEW_TYPE_STORE
            }
            else -> {
                throw IllegalStateException("Not Found ViewHolder Type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_AD -> {
            SearchByAdViewHolder(parent)
        }
        VIEW_TYPE_STORE -> {
            SearchByRatingViewHolder(parent)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchByRatingViewHolder -> {
                holder.bind(items[position] as StoreInfo, listener)
            }
            is SearchByAdViewHolder -> {
                holder.bind(items[position] as Popups, null)
            }
        }
    }

    fun submitList(newItems: List<StoreInfo>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun submitAdList(newItems: List<AdAndStoreItem>) {
        val list = items.filterIsInstance<StoreInfo>()
        items.clear()
        items.addAll(list)
        items.add(1, newItems[0])
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_AD = 1
        private const val VIEW_TYPE_STORE = 2
    }
}

class SearchByRatingViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemStoreByRatingBinding, StoreInfo>(R.layout.item_store_by_rating, parent) {

    override fun bind(item: StoreInfo, listener: OnItemClickListener<StoreInfo>?) {
        super.bind(item, listener)

        val categoryInfo = SharedPrefUtils.getCategories()
        val categories =
            item.categories.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }
        binding.tvCategory.text = categories
    }
}