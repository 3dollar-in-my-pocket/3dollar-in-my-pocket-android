package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemPhotoAddBinding
import com.zion830.threedollars.databinding.ItemPhotoAddFullBinding
import com.zion830.threedollars.databinding.ItemPhotoSelectedBinding
import zion830.com.common.base.loadRoundUrlImg

sealed class PhotoItem {
    data class AddPhoto(
        val isEnabled: Boolean,
        val currentCount: Int,
        val maxCount: Int
    ) : PhotoItem()

    data class SelectedPhoto(val uri: Uri, val position: Int) : PhotoItem()
}

class PhotoAdapter(
    private val onAddPhotoClick: () -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : ListAdapter<PhotoItem, RecyclerView.ViewHolder>(PhotoDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_ADD = 0
        private const val VIEW_TYPE_PHOTO = 1
        private const val VIEW_TYPE_FULL_ADD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is PhotoItem.AddPhoto -> {
                if (item.currentCount == 0) VIEW_TYPE_FULL_ADD
                else VIEW_TYPE_ADD
            }

            is PhotoItem.SelectedPhoto -> VIEW_TYPE_PHOTO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD -> {
                val binding = ItemPhotoAddBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                AddPhotoViewHolder(binding)
            }

            VIEW_TYPE_PHOTO -> {
                val binding = ItemPhotoSelectedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SelectedPhotoViewHolder(binding)
            }

            VIEW_TYPE_FULL_ADD -> {
                val binding = ItemPhotoAddFullBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                FullAddPhotoViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddPhotoViewHolder -> {
                val item = getItem(position) as PhotoItem.AddPhoto
                holder.bind(item)
            }

            is SelectedPhotoViewHolder -> {
                val item = getItem(position) as PhotoItem.SelectedPhoto
                holder.bind(item.uri, item.position)
            }

            is FullAddPhotoViewHolder -> {
                holder.bind()
            }
        }
    }

    inner class AddPhotoViewHolder(
        private val binding: ItemPhotoAddBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhotoItem.AddPhoto) {
            val context = binding.root.context
            val fullText = context.getString(
                R.string.boss_review_image_count,
                item.currentCount,
                item.maxCount
            )

            val spannableText = SpannableString(fullText)
            val currentCountText = item.currentCount.toString()
            val startIndex = fullText.indexOf(currentCountText)
            
            if (startIndex != -1) {
                val endIndex = startIndex + currentCountText.length
                val pinkColor = ContextCompat.getColor(context, R.color.pink)
                spannableText.setSpan(
                    ForegroundColorSpan(pinkColor),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            
            binding.tvAddPhoto.text = spannableText

            // 활성화/비활성화 상태 설정
            binding.root.isEnabled = item.isEnabled
            binding.root.alpha = if (item.isEnabled) 1.0f else 0.5f

            // 클릭 리스너 설정
            binding.root.setOnClickListener {
                if (item.isEnabled) {
                    onAddPhotoClick()
                }
            }
        }
    }

    inner class SelectedPhotoViewHolder(
        private val binding: ItemPhotoSelectedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            binding.ivPhoto.loadRoundUrlImg(uri.toString())
            binding.btnDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    inner class FullAddPhotoViewHolder(private val binding: ItemPhotoAddFullBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener {
                onAddPhotoClick()
            }
        }
    }

    private class PhotoDiffCallback : DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return when {
                oldItem is PhotoItem.AddPhoto && newItem is PhotoItem.AddPhoto -> true
                oldItem is PhotoItem.SelectedPhoto && newItem is PhotoItem.SelectedPhoto -> {
                    oldItem.uri == newItem.uri
                }

                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}