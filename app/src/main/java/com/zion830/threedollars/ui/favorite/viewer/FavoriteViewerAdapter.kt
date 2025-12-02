package com.zion830.threedollars.ui.favorite.viewer

import com.zion830.threedollars.BR
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFavoriteViewerBinding
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder

class FavoriteViewerAdapter(private val viewModel: FavoriteViewerViewModel) : BaseRecyclerView<ItemFavoriteViewerBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>(R.layout.item_favorite_viewer) {
    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemFavoriteViewerBinding, MyFavoriteFolderResponse.MyFavoriteFolderFavoriteModel>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.viewModel,viewModel)
    }
}