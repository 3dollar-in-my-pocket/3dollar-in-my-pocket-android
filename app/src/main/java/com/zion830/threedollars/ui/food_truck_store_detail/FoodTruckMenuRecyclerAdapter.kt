package com.zion830.threedollars.ui.food_truck_store_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.home.domain.data.store.BossStoreDetailItem
import com.home.domain.data.store.MenuModel
import com.threedollar.common.ext.loadCircleImage
import com.threedollar.common.ext.toFormattedNumber
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckMenuBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuEmptyBinding
import com.zion830.threedollars.databinding.ItemStoreDetailMenuMoreBinding
import com.zion830.threedollars.datasource.model.v2.response.BossStoreMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import zion830.com.common.base.BaseDiffUtilCallback


class FoodTruckMenuRecyclerAdapter(private val clickListener: () -> Unit) :
    ListAdapter<BossStoreDetailItem?, ViewHolder>(BaseDiffUtilCallback()) {

    fun getItemPosition(item: BossStoreDetailItem) =
        currentList.indexOfFirst {
            if (it is MenuModel && item is MenuModel) {
                it.imageUrl == item.imageUrl
            } else {
                false
            }
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is MenuModel -> {
            VIEW_TYPE_MENU
        }

        is BossStoreMenuMoreResponse -> {
            VIEW_TYPE_FOOTER
        }

        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_MENU -> {
            FoodTruckMenuViewHolder(ItemFoodTruckMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        VIEW_TYPE_FOOTER -> {
            FoodTruckMenuMoreViewHolder(
                binding = ItemStoreDetailMenuMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickListener = clickListener
            )
        }

        else -> {
            FoodTruckMenuEmptyViewHolder(ItemFoodTruckMenuEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is FoodTruckMenuViewHolder -> {
                holder.bind(getItem(position) as MenuModel)
            }

            is FoodTruckMenuEmptyViewHolder -> {
                holder.bind(getItem(position) as FoodTruckMenuEmptyResponse)
            }

            is FoodTruckMenuMoreViewHolder -> {
                holder.bind(getItem(position) as BossStoreMenuMoreResponse)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_MENU = 1
        private const val VIEW_TYPE_FOOTER = 2
    }
}

class FoodTruckMenuEmptyViewHolder(private val binding: ItemFoodTruckMenuEmptyBinding) : ViewHolder(binding.root) {
    fun bind(item: FoodTruckMenuEmptyResponse) {
        binding.menuNameTextView.text = GlobalApplication.getContext().getString(item.emptyTitle)
        binding.menuImageView.loadCircleImage(item.emptyImage)
    }
}

    class FoodTruckMenuMoreViewHolder(
        private val binding: ItemStoreDetailMenuMoreBinding,
        private val clickListener: () -> Unit,
    ) : ViewHolder(binding.root) {
        fun bind(item: BossStoreMenuMoreResponse) {
            binding.menuLayout.setOnClickListener {
                clickListener()
            }
            binding.menuNameTextView.text = item.moreTitle
        }
    }

    class FoodTruckMenuViewHolder(private val binding: ItemFoodTruckMenuBinding) :
        ViewHolder(binding.root) {

        fun bind(item: MenuModel) {
            binding.menuNameTextView.text = item.name
            binding.priceTextView.text = GlobalApplication.getContext().getString(R.string.food_truck_price, item.price.toFormattedNumber())
            binding.menuImageView.loadCircleImage(item.imageUrl)
        }
    }