package com.zion830.threedollars.ui.report_store.adapter

import android.view.ViewGroup
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemImageSliderBinding
import com.zion830.threedollars.repository.model.response.Image
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg


class StoreImageSliderAdapter : BaseRecyclerView<ItemImageSliderBinding, Image>(R.layout.item_image_slider) {
    val items = arrayListOf<Image>()

    fun submitItems(newItems: List<Image>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemImageSliderBinding, Image> {
        return SliderAdapterHolder(parent)
    }
}

class SliderAdapterHolder(parent: ViewGroup) : BaseViewHolder<ItemImageSliderBinding, Image>(R.layout.item_image_slider, parent) {

    fun bind(image: Image) {
        binding.ivContent.loadUrlImg(image.url)
    }
}