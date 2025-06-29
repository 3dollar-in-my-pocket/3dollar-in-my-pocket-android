package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.ImageModel
import com.threedollar.common.ext.loadImage
import com.zion830.threedollars.databinding.ItemReviewImageSliderBinding
import zion830.com.common.base.BaseDiffUtilCallback

class ReviewImageSliderAdapter : ListAdapter<ImageModel, ReviewImageSliderAdapter.ReviewImageViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewImageViewHolder {
        return ReviewImageViewHolder(
            ItemReviewImageSliderBinding.inflate(
                LayoutInflater.from(parent.context), 
                parent, 
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewImageViewHolder(
        private val binding: ItemReviewImageSliderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ImageModel?) {
            item?.let {
                binding.ivContent.loadImage(it.imageUrl)
            }
        }
    }
}