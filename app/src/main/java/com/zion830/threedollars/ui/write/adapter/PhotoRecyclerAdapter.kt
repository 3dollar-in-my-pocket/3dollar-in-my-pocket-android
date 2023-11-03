package com.zion830.threedollars.ui.write.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.home.domain.data.store.UserStoreDetailEmptyItem
import com.home.domain.data.store.UserStoreDetailItem
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.databinding.ItemPhotoBinding
import com.zion830.threedollars.databinding.ItemUserStoreEmptyPhotoReviewBinding
import com.home.domain.data.store.StoreImage
import zion830.com.common.base.BaseDiffUtilCallback


class PhotoRecyclerAdapter(
    private val photoClickListener: OnItemClickListener<StoreImage>, private val moreClickListener: () -> Unit,
) : ListAdapter<UserStoreDetailItem, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is StoreImage -> {
            VIEW_TYPE_PHOTO
        }
        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_PHOTO -> {
            PhotoViewHolder(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemCount)
        }

        else -> {
            UserStorePhotoEmptyViewHolder(
                binding = ItemUserStoreEmptyPhotoReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PhotoViewHolder -> {
                holder.bind(position, getItem(position) as StoreImage, photoClickListener, moreClickListener)
            }

            is UserStorePhotoEmptyViewHolder -> {
                holder.bind(getItem(position) as UserStoreDetailEmptyItem)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_PHOTO = 1
        private const val VIEW_TYPE_EMPTY = 2
        private const val MAX_COUNT = 4
    }

    class PhotoViewHolder(private val binding: ItemPhotoBinding, private val size: Int = 0) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            position: Int,
            item: StoreImage,
            photoClickListener: OnItemClickListener<StoreImage>?,
            moreClickListener: () -> Unit?,
        ) {
            binding.photoImageView.loadImage(item.url)
            when {
                position < MAX_COUNT - 1 || (size - MAX_COUNT) == 0 -> {
                    binding.root.setOnClickListener { photoClickListener?.onClick(item) }
                    binding.layoutMore.isVisible = false
                }

                (position == MAX_COUNT - 1) -> {
                    binding.tvMoreCount.text = "+${size - MAX_COUNT}"
                    binding.root.setOnClickListener { moreClickListener() }
                }

                else -> {
                    itemView.visibility = View.GONE
                    itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                }
            }
        }
    }

    class UserStorePhotoEmptyViewHolder(
        private val binding: ItemUserStoreEmptyPhotoReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserStoreDetailEmptyItem) {
            binding.emptyTextView.text = item.text
        }
    }
}