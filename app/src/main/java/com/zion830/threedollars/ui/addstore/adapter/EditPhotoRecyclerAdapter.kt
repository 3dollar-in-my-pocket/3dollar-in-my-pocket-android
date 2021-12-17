package com.zion830.threedollars.ui.addstore.adapter

import android.net.Uri
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoForUrlBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class EditPhotoRecyclerAdapter(
    clickListener: OnItemClickListener<StoreImage>
) : BaseRecyclerView<ItemPhotoForUrlBinding, StoreImage>(R.layout.item_photo_for_url) {

    init {
        this.clickListener = clickListener
    }

    fun addPhoto(uri: Uri?) {
        val newList = currentList.dropLast(1) + listOf(StoreImage(currentList.size - 1, uri, null)) + StoreImage(currentList.size, null)
        submitList(newList.toMutableList())
    }

    fun removePhoto(position: Int) {
        submitList(currentList.filter { it.index != position }
            .mapIndexed { index, storeImage -> StoreImage(index, storeImage.uri, storeImage.url) }
            .toMutableList())
    }
}
