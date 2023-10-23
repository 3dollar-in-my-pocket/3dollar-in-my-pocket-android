package com.zion830.threedollars.ui.addstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.*
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemReviewBinding
import com.zion830.threedollars.databinding.ItemStoreDetailMenuMoreBinding
import com.zion830.threedollars.databinding.ItemStoreDetailReviewMoreBinding
import com.zion830.threedollars.databinding.ItemUserStoreEmptyPhotoReviewBinding
import com.zion830.threedollars.databinding.ItemUserStoreMenuBinding
import com.zion830.threedollars.ui.store_detail.adapter.UserStoreMenuAdapter
import com.zion830.threedollars.ui.store_detail.adapter.UserStoreMenuMoreViewHolder
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback

class ReviewRecyclerAdapter(
    private val reviewEditOrDeleteClickEvent: OnItemClickListener<ReviewContentModel>,
    private val reviewClickListener: () -> Unit,
) : ListAdapter<UserStoreDetailItem, ViewHolder>(BaseDiffUtilCallback()) {


    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ReviewContentModel -> {
            VIEW_TYPE_REVIEW
        }
        is UserStoreDetailEmptyItem -> {
            VIEW_TYPE_EMPTY
        }
        else -> {
            VIEW_TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_REVIEW -> {
            ReviewViewHolder(ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        VIEW_TYPE_EMPTY -> {
            UserStoreReviewEmptyViewHolder(
                binding = ItemUserStoreEmptyPhotoReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        else -> {
            UserStoreReviewMoreViewHolder(
                binding = ItemStoreDetailReviewMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickListener = reviewClickListener
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ReviewViewHolder -> {
                holder.bind(getItem(position) as ReviewContentModel, reviewEditOrDeleteClickEvent, reviewClickListener, position)
            }

            is UserStoreReviewEmptyViewHolder -> {
                holder.bind(getItem(position) as UserStoreDetailEmptyItem)
            }

            is UserStoreReviewMoreViewHolder -> {
                holder.bind(getItem(position) as UserStoreMoreResponse)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_REVIEW = 1
        private const val VIEW_TYPE_FOOTER = 2
        private const val VIEW_TYPE_EMPTY = 3
    }
}

class ReviewViewHolder(private val binding: ItemReviewBinding) : ViewHolder(binding.root) {

    fun bind(
        item: ReviewContentModel,
        reviewEditOrDeleteClickEvent: OnItemClickListener<ReviewContentModel>,
        reviewClickListener: () -> Unit,
        position: Int,
    ) {
        binding.root.setOnClickListener { reviewClickListener() }
        binding.reviewTextView.text = item.review.contents
        binding.nameTextView.text = item.reviewWriter.name
        binding.medalTextView.text = item.reviewWriter.medal.name
        binding.medalImageView.loadImage(item.reviewWriter.medal.iconUrl)
        binding.createdAtTextView.text = StringUtils.getTimeString(item.review.createdAt, "yy.MM.dd E")
        binding.reviewRatingBar.rating = item.review.rating.toFloat()

        if (position % 2 == 0) {
            binding.rootConstraintLayout.setBackgroundResource(R.drawable.rect_radius_12_gray_0)
            binding.medalLayout.setBackgroundResource(R.drawable.rect_radius_4_pink_100)
            binding.reviewRatingBar.setBackgroundResource(R.drawable.rect_radius_4_pink_100)
        } else {
            binding.rootConstraintLayout.setBackgroundResource(R.drawable.rect_radius_12_pink_100)
            binding.medalLayout.setBackgroundResource(R.drawable.rect_radius_4_white)
            binding.reviewRatingBar.setBackgroundResource(R.drawable.rect_radius_4_white)
        }

        binding.reportAndEditTextView.setOnClickListener { reviewEditOrDeleteClickEvent.onClick(item) }
        binding.reportAndEditTextView.text =
            if (item.review.isOwner) GlobalApplication.getContext().getString(R.string.review_edit) else GlobalApplication.getContext()
                .getString(R.string.review_report)
    }
}

class UserStoreReviewMoreViewHolder(
    private val binding: ItemStoreDetailReviewMoreBinding,
    private val clickListener: () -> Unit,
) : ViewHolder(binding.root) {
    fun bind(item: UserStoreMoreResponse) {
        binding.menuLayout.setOnClickListener {
            clickListener()
        }
        binding.menuNameTextView.text = item.moreTitle
    }
}

class UserStoreReviewEmptyViewHolder(
    private val binding: ItemUserStoreEmptyPhotoReviewBinding
) : ViewHolder(binding.root) {
    fun bind(item: UserStoreDetailEmptyItem) {
        binding.emptyTextView.text = item.text
    }
}