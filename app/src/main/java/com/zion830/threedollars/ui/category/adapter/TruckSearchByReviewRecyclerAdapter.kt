package com.zion830.threedollars.ui.category.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemHomeEmptyBinding
import com.zion830.threedollars.databinding.ItemTruckSearchEmptyBinding
import com.zion830.threedollars.databinding.ItemTruckStoreByReviewBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.utils.StringUtils.textPartTypeface
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.convertDpToPx
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
            is HomeStoreEmptyResponse -> {
                VIEW_TYPE_EMPTY
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
        VIEW_TYPE_EMPTY -> {
            TruckSearchEmptyViewHolder(parent)
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
            is TruckSearchEmptyViewHolder -> {
                holder.bind(items[position] as HomeStoreEmptyResponse, null)
            }
        }
    }

    fun submitList(newItems: List<BossNearStoreResponse.BossNearStoreModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun submitEmptyList(newItems: List<AdAndStoreItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun submitAdList(newItems: List<AdAndStoreItem>) {
        if (items.isEmpty()) {
            return
        }
        var list: List<AdAndStoreItem> =
            items.filterIsInstance<BossNearStoreResponse.BossNearStoreModel>()
        if (list.isEmpty()) {
            list = items.filterIsInstance<HomeStoreEmptyResponse>()
        }
        items.clear()
        items.addAll(list)
        items.add(1, newItems[0])
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_AD = 1
        private const val VIEW_TYPE_STORE = 2
        private const val VIEW_TYPE_EMPTY = 3
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

class TruckSearchEmptyViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemTruckSearchEmptyBinding, HomeStoreEmptyResponse>(
        R.layout.item_truck_search_empty,
        parent
    ) {

    @SuppressLint("Range")
    override fun bind(
        item: HomeStoreEmptyResponse,
        listener: OnItemClickListener<HomeStoreEmptyResponse>?
    ) {
        super.bind(item, listener)
        binding.emptyBodyTextView.textPartTypeface("가슴속 3천원 사장님", Typeface.BOLD)
    }
}
