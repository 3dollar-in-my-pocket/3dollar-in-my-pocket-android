package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemTruckStoreByReviewBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.BossNearStoreResponse
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class TruckSearchByReviewRecyclerAdapter(
    private val listener: OnItemClickListener<BossNearStoreResponse.BossNearStoreModel>,
    private val adListener: OnItemClickListener<Popups>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = arrayListOf<AdAndStoreItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Popups -> {
                VIEW_TYPE_AD
            }
            is BossNearStoreResponse.BossNearStoreModel -> {
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
            TruckSearchByReviewViewHolder(parent)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TruckSearchByReviewViewHolder -> {
                holder.bind(items[position] as BossNearStoreResponse.BossNearStoreModel, listener)
            }
            is SearchByAdViewHolder -> {
                holder.bind(items[position] as Popups, adListener)
            }
        }
    }

    fun submitList(newItems: List<BossNearStoreResponse.BossNearStoreModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun submitAdList(newItems: List<AdAndStoreItem>) {
        if (items.isEmpty()) {
            return
        }
        val list = items.filterIsInstance<BossNearStoreResponse.BossNearStoreModel>()
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

class TruckSearchByReviewViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemTruckStoreByReviewBinding, BossNearStoreResponse.BossNearStoreModel>(
        R.layout.item_truck_store_by_review,
        parent
    ) {

    override fun bind(
        item: BossNearStoreResponse.BossNearStoreModel,
        listener: OnItemClickListener<BossNearStoreResponse.BossNearStoreModel>?
    ) {
        super.bind(item, listener)
        val categories = item.categories.joinToString(" ") { "#${it.name}" }
        binding.tvCategory.text = categories
    }
}