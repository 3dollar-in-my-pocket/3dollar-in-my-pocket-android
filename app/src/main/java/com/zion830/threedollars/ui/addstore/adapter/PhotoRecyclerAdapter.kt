package com.zion830.threedollars.ui.addstore.adapter

import android.net.Uri
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.ui.addstore.StoreImage
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class PhotoRecyclerAdapter(
    clickListener: OnItemClickListener<StoreImage>
) : BaseRecyclerView<ItemPhotoBinding, StoreImage>(R.layout.item_photo) {

    init {
        submitList(listOf(StoreImage(0, null)))
        this.clickListener = clickListener
    }

    fun addPhoto(uri: Uri?) {
        val newList = currentList.dropLast(1) + listOf(StoreImage(currentList.size - 1, uri)) + StoreImage(currentList.size, null)
        submitList(newList)
    }

    fun removePhoto(position: Int) {
        submitList(currentList.filter { it.index != position }.mapIndexed { index, storeImage -> StoreImage(index, storeImage.uri) })
    }
}
