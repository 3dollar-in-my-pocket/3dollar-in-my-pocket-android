package com.zion830.threedollars.ui.report_store.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.ImageContentModel
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemImageSliderBinding
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.datasource.model.v2.response.store.Image
import com.zion830.threedollars.ui.addstore.adapter.PhotoRecyclerAdapter
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg


class StoreImageSliderAdapter : ListAdapter<ImageContentModel, SliderAdapterHolder>(BaseDiffUtilCallback()) {

    fun getItems(): List<ImageContentModel> = currentList

    override fun submitList(list: List<ImageContentModel>?) {
        super.submitList(null)
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SliderAdapterHolder(ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SliderAdapterHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SliderAdapterHolder(private val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ImageContentModel) {
        binding.ivContent.loadImage(item.url)
    }
}