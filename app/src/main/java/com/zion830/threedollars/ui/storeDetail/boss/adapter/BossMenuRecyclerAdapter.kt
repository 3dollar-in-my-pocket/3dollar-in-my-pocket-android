package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.home.data.store.BossStoreDetailItem
import com.threedollar.domain.home.data.store.MenuModel
import com.threedollar.common.ext.loadCircleImage
import com.threedollar.common.ext.toFormattedNumber
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.databinding.ItemFoodTruckMenuBinding
import com.zion830.threedollars.databinding.ItemFoodTruckMenuEmptyBinding
import com.zion830.threedollars.databinding.ItemStoreDetailMenuMoreBinding
import com.zion830.threedollars.datasource.model.v2.response.BossStoreMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR


class BossMenuRecyclerAdapter(
    private val onMoreClick: () -> Unit,
    private val onMenuImageClick: (MenuModel, Int) -> Unit,
) :
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
            BossMenuViewHolder(
                binding = ItemFoodTruckMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onMenuImageClick = onMenuImageClick,
            )
        }

        VIEW_TYPE_FOOTER -> {
            BossMenuMoreViewHolder(
                binding = ItemStoreDetailMenuMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickListener = onMoreClick
            )
        }

        else -> {
            BossMenuEmptyViewHolder(ItemFoodTruckMenuEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is BossMenuViewHolder -> {
                holder.bind(getItem(position) as MenuModel, position)
            }

            is BossMenuEmptyViewHolder -> {
                holder.bind(getItem(position) as FoodTruckMenuEmptyResponse)
            }

            is BossMenuMoreViewHolder -> {
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

class BossMenuEmptyViewHolder(private val binding: ItemFoodTruckMenuEmptyBinding) : ViewHolder(binding.root) {
    fun bind(item: FoodTruckMenuEmptyResponse) {
        binding.menuNameTextView.text = GlobalApplication.getContext().getString(item.emptyTitle)
        binding.menuImageView.loadCircleImage(item.emptyImage)
    }
}

    class BossMenuMoreViewHolder(
        private val binding: ItemStoreDetailMenuMoreBinding,
        private val clickListener: () -> Unit,
    ) : ViewHolder(binding.root) {
        fun bind(item: BossStoreMenuMoreResponse) {
            binding.menuLayout.onSingleClick {
                clickListener()
            }
            binding.menuNameTextView.text = item.moreTitle
        }
    }

    class BossMenuViewHolder(
        private val binding: ItemFoodTruckMenuBinding,
        private val onMenuImageClick: (MenuModel, Int) -> Unit,
    ) :
        ViewHolder(binding.root) {

        fun bind(item: MenuModel, position: Int) {
            binding.menuNameTextView.text = item.name
            binding.priceTextView.text = GlobalApplication.getContext().getString(CommonR.string.food_truck_price, item.price.toFormattedNumber())
            binding.menuImageView.loadCircleImage(item.imageUrl)
            if (item.imageUrl.isNullOrBlank()) {
                binding.menuImageView.setOnClickListener(null)
            } else {
                binding.menuImageView.onSingleClick {
                    onMenuImageClick(item, position)
                }
            }
        }
    }
