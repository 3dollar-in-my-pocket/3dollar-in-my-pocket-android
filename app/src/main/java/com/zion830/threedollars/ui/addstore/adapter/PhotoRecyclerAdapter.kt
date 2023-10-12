package com.zion830.threedollars.ui.addstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseDiffUtilCallback


class PhotoRecyclerAdapter(
    private val photoClickListener: OnItemClickListener<StoreImage>,
) : ListAdapter<StoreImage, PhotoRecyclerAdapter.PhotoViewHolder>(BaseDiffUtilCallback()) {

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(position, getItem(position), photoClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder =
        PhotoViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemCount)

    override fun submitList(list: MutableList<StoreImage>?) {
        super.submitList(null)
        super.submitList(list)
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding, private val size: Int = 0) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, item: StoreImage, photoClickListener: OnItemClickListener<StoreImage>?) {
            binding.photoImageView.loadImage(item.url)
            when {
                position < MAX_COUNT - 1 || (size - MAX_COUNT) == 0 -> {
                    binding.root.setOnClickListener { photoClickListener?.onClick(item) }
                    binding.layoutMore.isVisible = false
                }

                (position == MAX_COUNT - 1) -> {
                    binding.tvMoreCount.text = "+${size - MAX_COUNT}"
                    binding.root.setOnClickListener { // TODO: 더보기 페이지로 이동
                    }
                }

                else -> {
                    itemView.visibility = View.GONE
                    itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
    }

    companion object {
        private const val MAX_COUNT = 4
    }
}