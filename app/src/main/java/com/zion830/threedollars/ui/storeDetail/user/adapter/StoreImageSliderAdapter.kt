package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.store.ImageContentModel
import com.threedollar.common.ext.loadImage
import com.zion830.threedollars.databinding.ItemImageSliderBinding
import zion830.com.common.base.BaseDiffUtilCallback


class StoreImageSliderAdapter : PagingDataAdapter<ImageContentModel, SliderAdapterHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SliderAdapterHolder(ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SliderAdapterHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SliderAdapterHolder(private val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageContentModel?) {
        binding.ivContent.loadImage(item?.url)
    }
}