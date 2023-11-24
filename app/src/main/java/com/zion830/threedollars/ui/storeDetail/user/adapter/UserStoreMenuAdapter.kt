package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.home.domain.data.store.UserStoreDetailItem
import com.home.domain.data.store.UserStoreMenuModel
import com.home.domain.data.store.UserStoreMoreResponse
import com.zion830.threedollars.databinding.ItemStoreDetailMenuMoreBinding
import com.zion830.threedollars.databinding.ItemUserStoreMenuBinding
import zion830.com.common.base.BaseDiffUtilCallback

class UserStoreMenuAdapter(private val clickListener: () -> Unit) :
    ListAdapter<UserStoreDetailItem, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    private var categoryNameList = mutableListOf<String>()

    fun clearCategoryNameList() {
        categoryNameList.clear()
    }

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
                holder.bind(getItem(position) as UserStoreMenuModel)
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

    companion object {
        private const val VIEW_TYPE_MENU = 1
        private const val VIEW_TYPE_FOOTER = 2
    }


    inner class UserStoreMenuViewHolder(private val binding: ItemUserStoreMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserStoreMenuModel) {
            if (!categoryNameList.contains(item.category.name)) {
                categoryNameList.add(item.category.name)
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
            binding.menuPriceTextView.text = if (item.price.isNullOrEmpty()) "-" else item.price
        }
    }
}

class UserStoreMenuMoreViewHolder(
    private val binding: ItemStoreDetailMenuMoreBinding,
    private val clickListener: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserStoreMoreResponse) {
        binding.menuLayout.setOnClickListener {
            clickListener()
        }
        binding.menuNameTextView.text = item.moreTitle
    }
}