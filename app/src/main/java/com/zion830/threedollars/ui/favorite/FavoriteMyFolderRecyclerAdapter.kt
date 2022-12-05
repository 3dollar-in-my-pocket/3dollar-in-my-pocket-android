package com.zion830.threedollars.ui.favorite

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFavoritePageBinding
import com.zion830.threedollars.datasource.model.v2.response.favorite.MyFavoriteFolderResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FavoriteMyFolderRecyclerAdapter(
    private val listener: OnItemClickListener<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>
) : PagingDataAdapter<MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel, BaseViewHolder<ItemFavoritePageBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>>(
    BaseDiffUtilCallback()
) {

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
        }
    }
}