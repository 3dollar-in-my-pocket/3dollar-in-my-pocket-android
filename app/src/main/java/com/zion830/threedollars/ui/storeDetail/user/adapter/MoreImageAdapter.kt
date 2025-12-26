package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.store.ImageContentModel
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.databinding.ItemMorePhotoBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class MoreImageAdapter(private val photoClickListener: OnItemClickListener<Int>) :
    PagingDataAdapter<ImageContentModel, MoreImageViewHolder>(BaseDiffUtilCallback()) {
    override fun onBindViewHolder(holder: MoreImageViewHolder, position: Int) {
        holder.bind(getItem(position), photoClickListener, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreImageViewHolder =
        MoreImageViewHolder(ItemMorePhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
}

class MoreImageViewHolder(private val binding: ItemMorePhotoBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ImageContentModel?, photoClickListener: OnItemClickListener<Int>, position: Int) {
        item?.let {
            binding.photoImageView.loadImage(item.url)
            binding.photoImageView.onSingleClick {
                photoClickListener.onClick(position)
            }
        }
    }
}