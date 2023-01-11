package com.zion830.threedollars.ui.favorite

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFavoritePageBinding
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FavoriteMyFolderRecyclerAdapter(
    private val listener: OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>,
    private val deleteListener: OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>
) : PagingDataAdapter<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel, BaseViewHolder<ItemFavoritePageBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>>(
    BaseDiffUtilCallback()
) {
    private var isDelete = false

    fun setDelete(boolean: Boolean) {
        isDelete = boolean
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object :
            BaseViewHolder<ItemFavoritePageBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>(R.layout.item_favorite_page, parent) {}

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemFavoritePageBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>,
        position: Int
    ) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item, listener)
            holder.binding.deleteImageView.isVisible = isDelete
            holder.binding.storeCategoriesTextView.text =
                item.categories.joinToString(" ") { "#${it.name}" }
            holder.binding.deleteImageView.setOnClickListener {
                deleteListener.onClick(item)
            }
            holder.binding.itemLinearLayout.setOnClickListener {
                if (!item.isDeleted && isDelete.not()) {
                    listener.onClick(item)
                }
            }
        }
    }
}