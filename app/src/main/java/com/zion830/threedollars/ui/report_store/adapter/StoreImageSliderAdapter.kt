package com.zion830.threedollars.ui.report_store.adapter

import android.view.ViewGroup
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemImageSliderBinding
import com.zion830.threedollars.repository.model.v2.response.store.Image
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener


class StoreImageSliderAdapter : BaseRecyclerView<ItemImageSliderBinding, Image>(R.layout.item_image_slider) {

    fun getItems(): List<Image> = currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemImageSliderBinding, Image> {
        return SliderAdapterHolder(parent)
    }
}

class SliderAdapterHolder(parent: ViewGroup) : BaseViewHolder<ItemImageSliderBinding, Image>(R.layout.item_image_slider, parent) {

    override fun bind(item: Image, listener: OnItemClickListener<Image>?) {
        super.bind(item, listener)
        binding.ivContent.loadUrlImg(item.url)
    }
}