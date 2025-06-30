package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.ReviewContentModel
import com.my.presentation.page.data.convertUpdateAt
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.ui.storeDetail.boss.listener.OnReviewImageClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewBinding
import com.zion830.threedollars.databinding.ItemFoodTruckReviewMoreBinding
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

private const val TYPE_REVIEW = 0
private const val TYPE_MORE = 1

class FoodTruckReviewAdapter(
    private val onReviewImageClickListener: OnReviewImageClickListener,
    private val onReviewReportClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewEditClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewLikeClickListener: OnItemClickListener<ReviewContentModel>,
    private val onMoreClickListener: (() -> Unit)?,
    private var isMore: Boolean = false
) : PagingDataAdapter<ReviewContentModel, ViewHolder>(DIFF_CALLBACK) {
    private var count = 0

    fun setTotalCount(totalCount: Int) {
        count = totalCount
    }

    fun updateMoreButtonVisibility(hasMore: Boolean) {
        if (this.isMore != hasMore) {
            this.isMore = hasMore
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isMore && position == itemCount - 1) TYPE_MORE else TYPE_REVIEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == TYPE_REVIEW) {
            FoodTruckReviewViewHolder(
                ItemFoodTruckReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onReviewImageClickListener,
                onReviewReportClickListener,
                onReviewEditClickListener,
                onReviewLikeClickListener
            )
        } else {
            MoreViewHolder(ItemFoodTruckReviewMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false), onMoreClickListener)
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        if (holder is FoodTruckReviewViewHolder) {
            holder.bind(item, position)
        } else if (holder is MoreViewHolder) {
            holder.bind(count)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReviewContentModel>() {
            override fun areItemsTheSame(oldItem: ReviewContentModel, newItem: ReviewContentModel): Boolean =
                oldItem.review.reviewId == newItem.review.reviewId

            override fun areContentsTheSame(oldItem: ReviewContentModel, newItem: ReviewContentModel): Boolean =
                oldItem == newItem
        }
    }
}

class MoreViewHolder(private val binding: ItemFoodTruckReviewMoreBinding, private val listener: (() -> Unit)?) :
    ViewHolder(binding.root) {
    fun bind(count: Int) {
        binding.twReviewMore.text = binding.root.context.getString(R.string.str_review_more, count)
        listener?.let {
            binding.root.setOnClickListener { it() }
        }
    }
}

class FoodTruckReviewViewHolder(
    private val binding: ItemFoodTruckReviewBinding,
    private val onReviewImageClickListener: OnReviewImageClickListener,
    private val onReviewReportClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewEditClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewLikeClickListener: OnItemClickListener<ReviewContentModel>,
) : ViewHolder(binding.root) {

    fun bind(item: ReviewContentModel, position: Int) {
        binding.apply {
            index = position
            val photoAdapter = ReviewPhotoRecyclerAdapter(onReviewImageClickListener)
            twNickNameTitle.text = item.reviewWriter.name
            twReviewDate.text = "${item.review.updatedAt.convertUpdateAt()} · "
            if (item.review.isOwner) {
                twReviewReport.text = binding.root.context.getString(R.string.review_edit)
                twReviewReport.onSingleClick { onReviewEditClickListener.onClick(item) }
            } else {
                twReviewReport.text = binding.root.context.getString(R.string.review_report)
                twReviewReport.onSingleClick { onReviewReportClickListener.onClick(item) }
            }
            imgMedal.loadUrlImg(item.reviewWriter.medal.iconUrl)
            twMedalName.text = item.reviewWriter.medal.name
            reviewRatingBar.rating = item.review.rating
            recyclerReviewImage.adapter = photoAdapter
            photoAdapter.submitList(item.review.images)
            twReviewContent.text = item.review.contents
            twReviewLike.text = binding.root.context.getString(R.string.str_like, item.stickers.find { it.stickerId == "LIKE" }?.count ?: 0)
            twReviewLike.isSelected = item.stickers.any { it.stickerId == "LIKE" && it.reactedByMe }
            twReviewLike.onSingleClick { onReviewLikeClickListener.onClick(item) }
            groupReply.isVisible = item.comments.isNotEmpty()
            item.comments.firstOrNull()?.let {
                twReplyBoss.text = it.writer.name
                twReplyDate.text = it.updatedAt
                twReplyContent.text = it.content
            }
        }
    }
}
