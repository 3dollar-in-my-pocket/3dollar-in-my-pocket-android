package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.threedollar.domain.home.data.store.UserStoreDetailItem
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.domain.home.data.store.UserStoreMoreResponse
import com.zion830.threedollars.databinding.ItemStoreDetailMenuMoreBinding
import com.zion830.threedollars.databinding.ItemUserStoreMenuBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

class UserStoreMenuAdapter(private val clickListener: () -> Unit) :
    ListAdapter<UserStoreDetailItem, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is UserStoreMenuModel -> {
            VIEW_TYPE_MENU
        }
        else -> {
            VIEW_TYPE_FOOTER
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserStoreMenuViewHolder -> {
                val item = getItem(position) as UserStoreMenuModel
                val isFirstInCategory = isFirstItemInCategory(position, item)
                holder.bind(item, isFirstInCategory)
            }
            is UserStoreMenuMoreViewHolder -> {
                holder.bind(getItem(position) as UserStoreMoreResponse)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_MENU -> {
            UserStoreMenuViewHolder(ItemUserStoreMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
        else -> {
            UserStoreMenuMoreViewHolder(
                binding = ItemStoreDetailMenuMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickListener = clickListener
            )
        }
    }

    private fun isFirstItemInCategory(position: Int, currentItem: UserStoreMenuModel): Boolean {
        if (position == 0) return true

        val previousItem = getItem(position - 1)
        return previousItem !is UserStoreMenuModel ||
               previousItem.category.name != currentItem.category.name
    }

    companion object {
        private const val VIEW_TYPE_MENU = 1
        private const val VIEW_TYPE_FOOTER = 2
    }


    inner class UserStoreMenuViewHolder(private val binding: ItemUserStoreMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserStoreMenuModel, isFirstInCategory: Boolean) {
            if (isFirstInCategory) {
                binding.categoryImageView.apply {
                    Glide.with(this)
                        .load(item.category.imageUrl)
                        .into(this)
                    isVisible = true
                }
                binding.categoryTextView.apply {
                    text = item.category.name
                    isVisible = true
                }
            } else {
                binding.categoryImageView.isVisible = false
                binding.categoryTextView.isVisible = false
            }
            binding.menuNameTextView.text = if (item.name.isNullOrEmpty()) "-" else item.name
            binding.menuPriceTextView.text = when {
                item.price.isNullOrEmpty() -> "-"
                item.count == null || item.count == 0 -> itemView.context.getString(CommonR.string.food_truck_price, item.price)
                else -> itemView.context.getString(CommonR.string.menu_count_price_format, item.count, item.price)
            }
        }
    }
}

class UserStoreMenuMoreViewHolder(
    private val binding: ItemStoreDetailMenuMoreBinding,
    private val clickListener: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserStoreMoreResponse) {
        binding.menuLayout.onSingleClick {
            clickListener()
        }
        binding.menuNameTextView.text = item.moreTitle
    }
}