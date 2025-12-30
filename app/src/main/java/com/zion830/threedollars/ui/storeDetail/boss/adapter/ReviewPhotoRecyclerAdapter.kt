package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.common.ext.loadImage
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.ui.storeDetail.boss.listener.OnReviewImageClickListener
import zion830.com.common.base.BaseDiffUtilCallback


class ReviewPhotoRecyclerAdapter(
    private val photoClickListener: OnReviewImageClickListener
) : ListAdapter<ImageModel, ReviewPhotoRecyclerAdapter.PhotoViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), position, currentList, photoClickListener)
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ImageModel, 
            position: Int, 
            allImages: List<ImageModel>, 
            listener: OnReviewImageClickListener
        ) {
            binding.photoImageView.loadImage(item.imageUrl)
            binding.layoutMore.isVisible = false
            binding.root.setOnClickListener { 
                listener.onImageClick(item, allImages, position)
            }
        }
    }
}
