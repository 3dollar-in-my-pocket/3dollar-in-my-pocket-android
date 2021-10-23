package com.zion830.threedollars.ui.report_store.adapter

import androidx.core.view.isVisible
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoPreviewBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener


class StorePreviewImageAdapter(
    clickListener: OnItemClickListener<StoreImage>
) : BaseRecyclerView<ItemPhotoPreviewBinding, StoreImage>(R.layout.item_photo_preview) {
    private var focusedIndex = 0

    init {
        this.clickListener = clickListener
    }

    fun updateFocusedIndex(index: Int) {
        val temp = focusedIndex
        focusedIndex = index
        notifyItemChanged(temp)
        notifyItemChanged(index)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemPhotoPreviewBinding, StoreImage>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.viewFocus.isVisible = position == focusedIndex
    }
}