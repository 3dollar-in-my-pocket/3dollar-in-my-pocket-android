package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckMenuBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuEmptyBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuMoreBinding
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailItem
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailModel
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import com.threedollar.common.listener.OnItemClickListener


class FoodTruckMenuRecyclerAdapter(
    private val clickListener: () -> Unit
) :
    ListAdapter<BossStoreDetailItem?, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    fun getItemPosition(item: BossStoreDetailItem) =
        currentList.indexOfFirst {
            if (it is BossStoreDetailModel.Menu && item is BossStoreDetailModel.Menu) {
                it.imageUrl == item.imageUrl
            } else {
                false
            }
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is BossStoreDetailModel.Menu -> {
            VIEW_TYPE_MENU
        }
        is FoodTruckMenuMoreResponse -> {
            VIEW_TYPE_FOOTER
        }
        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_MENU -> {
            FoodTruckMenuViewHolder(parent)
        }
        VIEW_TYPE_FOOTER -> {
            FoodTruckMenuMoreViewHolder(parent)
        }
        else -> {
            FoodTruckMenuEmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FoodTruckMenuViewHolder -> {
                holder.bind(getItem(position) as BossStoreDetailModel.Menu, null)
            }
            is FoodTruckMenuEmptyViewHolder -> {
                holder.bind(getItem(position) as FoodTruckMenuEmptyResponse, null)
            }
            is FoodTruckMenuMoreViewHolder -> {
                holder.bind(getItem(position) as FoodTruckMenuMoreResponse, null)
                holder.itemView.setOnClickListener { clickListener() }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_MENU = 1
        private const val VIEW_TYPE_FOOTER = 2
    }
}

class FoodTruckMenuEmptyViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemFoodTruckMenuEmptyBinding, FoodTruckMenuEmptyResponse>(
        R.layout.item_food_truck_menu_empty,
        parent
    ) {
    @SuppressLint("Range")
    override fun bind(
        item: FoodTruckMenuEmptyResponse,
        listener: OnItemClickListener<FoodTruckMenuEmptyResponse>?
    ) {
        super.bind(item, listener)
    }
}

class FoodTruckMenuMoreViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemFoodTruckMenuMoreBinding, FoodTruckMenuMoreResponse>(
        R.layout.item_food_truck_menu_more,
        parent
    ) {

    @SuppressLint("Range")
    override fun bind(
        item: FoodTruckMenuMoreResponse,
        listener: OnItemClickListener<FoodTruckMenuMoreResponse>?
    ) {
        super.bind(item, listener)
    }
}

class FoodTruckMenuViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemFoodTruckMenuBinding, BossStoreDetailModel.Menu>(
        R.layout.item_food_truck_menu,
        parent
    ) {

    @SuppressLint("Range")
    override fun bind(
        item: BossStoreDetailModel.Menu,
        listener: OnItemClickListener<BossStoreDetailModel.Menu>?
    ) {
        super.bind(item, listener)
    }
}