package com.zion830.threedollars.ui.addstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener


class PhotoRecyclerAdapter(
    private val clickListener: OnItemClickListener<StoreImage>,
) : ListAdapter<StoreImage, PhotoRecyclerAdapter.PhotoViewHolder>(BaseDiffUtilCallback()) {

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(position, getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent, itemCount)
    }

    override fun submitList(list: MutableList<StoreImage>?) {
        super.submitList(null)
        super.submitList(list)
    }

    class PhotoViewHolder(
        parent: ViewGroup,
        private val size: Int = 0
    ) : BaseViewHolder<ItemPhotoBinding, StoreImage>(R.layout.item_photo, parent) {

        fun bind(position: Int, item: StoreImage, listener: OnItemClickListener<StoreImage>?) {
            super.bind(item, listener)
            when {
                position < MAX_COUNT - 1 || (size - MAX_COUNT) == 0 -> {
                    binding.layoutMore.isVisible = false
                }
                (position == MAX_COUNT - 1) -> {
                    binding.tvMoreCount.text = "+${size - MAX_COUNT}"
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