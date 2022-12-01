package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMypageEmptyBinding
import com.zion830.threedollars.databinding.ItemMypageFavoriteBinding
import com.zion830.threedollars.databinding.ItemRecentVisitHistoryBinding
import com.zion830.threedollars.datasource.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import com.zion830.threedollars.datasource.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class RecentVisitHistoryRecyclerAdapter(
    private val onClickListener: (AdAndStoreItem) -> Unit,
) : ListAdapter<AdAndStoreItem, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is VisitHistoryContent -> {
            VIEW_TYPE_RECENT_VISIT_HISTORY
        }
        is MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel -> {
            VIEW_TYPE_MY_FAVORITE
        }
        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecentVisitHistoryViewHolder -> {
                holder.bind(getItem(position) as VisitHistoryContent, listener = null)
            }
            is MyFavoriteViewHolder -> {
                holder.bind(getItem(position) as MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel, listener = null)
            }
            is MyPageEmptyViewHolder -> {
                holder.bind(getItem(position) as HomeStoreEmptyResponse, listener = null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_RECENT_VISIT_HISTORY -> {
            RecentVisitHistoryViewHolder(parent)
        }
        VIEW_TYPE_MY_FAVORITE -> {
            MyFavoriteViewHolder(parent)
        }
        else -> {
            MyPageEmptyViewHolder(parent)
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_RECENT_VISIT_HISTORY = 1
        private const val VIEW_TYPE_MY_FAVORITE = 2
    }

    inner class RecentVisitHistoryViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemRecentVisitHistoryBinding, VisitHistoryContent>(R.layout.item_recent_visit_history, parent) {

        override fun bind(item: VisitHistoryContent, listener: OnItemClickListener<VisitHistoryContent>?) {
            super.bind(item, listener)
            binding.run {
                tvCreatedAt.text = StringUtils.getTimeString(item.createdAt)
                ivCategory.bindMenuIcons(item.store.categories)
                val categoryInfo = SharedPrefUtils.getCategories()
                val categories = item.store.categories.joinToString(" ") {
                    "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}"
                }
                tvCategories.text = categories
                layoutItem.setOnClickListener {
                    if (!item.store.isDeleted) {
                        onClickListener(item)
                    }
                }
            }
        }
    }

    inner class MyFavoriteViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemMypageFavoriteBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>(R.layout.item_mypage_favorite, parent) {

        override fun bind(
            item: MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel,
            listener: OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>?
        ) {
            super.bind(item, listener)
            binding.item = item
            binding.run {
                val categories = item.categories.joinToString(" ") {
                    "#${it.name}"
                }
                storeCategoriesTextView.text = categories
                itemLinearLayout.setOnClickListener {
                    onClickListener(item)
                }
            }
        }
    }

    inner class MyPageEmptyViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemMypageEmptyBinding, HomeStoreEmptyResponse>(R.layout.item_mypage_empty, parent) {
        override fun bind(item: HomeStoreEmptyResponse, listener: OnItemClickListener<HomeStoreEmptyResponse>?) {
            super.bind(item, listener)
            binding.item = item
        }
    }
}