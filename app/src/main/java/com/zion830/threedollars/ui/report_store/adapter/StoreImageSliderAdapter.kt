package com.zion830.threedollars.ui.store_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.smarteist.autoimageslider.SliderViewAdapter
import com.zion830.threedollars.databinding.ItemImageSliderBinding
import com.zion830.threedollars.repository.model.response.Image
import zion830.com.common.base.loadUrlImg


class StoreImageSliderAdapter : SliderViewAdapter<SliderAdapterHolder>() {
    private val items = arrayListOf<Image>()

    fun submitItems(newItems: List<Image>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterHolder {
        val view = ItemImageSliderBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return SliderAdapterHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterHolder?, position: Int) {
        viewHolder?.bind(items[position])
    }

    override fun getCount(): Int = items.size
}

class SliderAdapterHolder(private val binding: ItemImageSliderBinding) : SliderViewAdapter.ViewHolder(binding.root) {

    fun bind(image: Image) {
        binding.ivContent.loadUrlImg(image.url)
    }
}