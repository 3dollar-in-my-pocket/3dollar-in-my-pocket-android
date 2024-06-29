package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMypageEmptyBinding
import com.zion830.threedollars.databinding.ItemMypageFavoriteBinding
import com.zion830.threedollars.databinding.ItemRecentVisitHistoryBinding
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.visit_history.VisitHistoryContent
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class MyPageRecyclerAdapter(
    private val onClickListener: OnItemClickListener<AdAndStoreItem>,
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
                holder.bind(getItem(position) as StoreEmptyResponse, listener = null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_RECENT_VISIT_HISTORY -> {
            RecentVisitHistoryViewHolder(parent, onClickListener)
        }
        VIEW_TYPE_MY_FAVORITE -> {
            MyFavoriteViewHolder(parent, onClickListener)
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
}

class RecentVisitHistoryViewHolder(parent: ViewGroup, private val onClickListener: OnItemClickListener<AdAndStoreItem>) :
    BaseViewHolder<ItemRecentVisitHistoryBinding, VisitHistoryContent>(R.layout.item_recent_visit_history, parent) {

    override fun bind(item: VisitHistoryContent, listener: OnItemClickListener<VisitHistoryContent>?) {
        super.bind(item, listener)
        binding.run {
            tvCreatedAt.text = StringUtils.getTimeString(item.createdAt)
            ivCategory.bindMenuIcons(item.store.categories)
            val categoryInfo = LegacySharedPrefUtils.getCategories()
            val categories = item.store.categories.joinToString(" ") {
                "#${categoryInfo.find { categoryInfo -> categoryInfo.categoryId == it.categoryId }?.name}"
            }
            tvCategories.text = categories
            layoutItem.setOnClickListener {
                if (!item.store.isDeleted) {
                    onClickListener.onClick(item)
                }
            }
        }
    }
}

class MyFavoriteViewHolder(parent: ViewGroup, private val onClickListener: OnItemClickListener<AdAndStoreItem>) :
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
                if (!item.isDeleted) {
                    onClickListener.onClick(item)
                }
            }
        }
    }
}

class MyPageEmptyViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemMypageEmptyBinding, StoreEmptyResponse>(R.layout.item_mypage_empty, parent) {
    override fun bind(item: StoreEmptyResponse, listener: OnItemClickListener<StoreEmptyResponse>?) {
        super.bind(item, listener)
        binding.item = item
    }
}