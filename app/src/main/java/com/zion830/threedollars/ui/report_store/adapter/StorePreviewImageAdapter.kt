package com.zion830.threedollars.ui.report_store.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoPreviewBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener


class StorePreviewImageAdapter(
    clickListener: OnItemClickListener<StoreImage>
) : BaseRecyclerView<ItemPhotoPreviewBinding, StoreImage>(R.layout.item_photo_preview) {

    init {
        this.clickListener = clickListener
    }
}