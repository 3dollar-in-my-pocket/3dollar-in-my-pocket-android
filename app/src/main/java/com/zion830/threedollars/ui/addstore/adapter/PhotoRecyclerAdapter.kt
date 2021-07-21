package com.zion830.threedollars.ui.addstore.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class PhotoRecyclerAdapter(
    private val clickListener: OnItemClickListener<StoreImage>
) : ListAdapter<StoreImage, PhotoRecyclerAdapter.PhotoViewHolder>(BaseDiffUtilCallback()) {

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent, currentList.size)
    }

    class PhotoViewHolder(
        parent: ViewGroup,
        private val size: Int = 0
    ) : BaseViewHolder<ItemPhotoBinding, StoreImage>(R.layout.item_photo, parent) {

        override fun bind(item: StoreImage, listener: OnItemClickListener<StoreImage>?) {
            super.bind(item, listener)
            if (item.index < MAX - 1) {
                binding.layoutMore.isVisible = false
            } else if (item.index == MAX - 1) {
                binding.tvMoreCount.text = "+${size - MAX}"
            } else {
                binding.layoutItem.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val MAX = 4
    }
}