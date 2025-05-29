package com.zion830.threedollars.ui.storeDetail.boss.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.ImageModel
import com.home.domain.data.store.ReviewContentModel
import com.my.presentation.page.data.convertUpdateAt
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckReviewBinding
import com.zion830.threedollars.databinding.ItemFoodTruckReviewMoreBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

private const val TYPE_PHOTO = 0
private const val TYPE_MORE = 1

class FoodTruckReviewAdapter(
    private val onReviewImageClickListener: OnItemClickListener<ImageModel>,
    private val onReviewReportClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewLikeClickListener: OnItemClickListener<ReviewContentModel>,
    private val onMoreClickListener: (() -> Unit)?
) : ListAdapter<ReviewContentModel, ViewHolder>(BaseDiffUtilCallback()) {
    var count = 0

    fun setTotalCount(totalCount: Int) {
        count = totalCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < 3) TYPE_PHOTO else TYPE_MORE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == TYPE_PHOTO) {
            FoodTruckReviewViewHolder(
                ItemFoodTruckReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onReviewImageClickListener,
                onReviewReportClickListener,
                onReviewLikeClickListener
            )
        } else {
            MoreViewHolder(ItemFoodTruckReviewMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false), onMoreClickListener)
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is FoodTruckReviewViewHolder) {
            holder.bind(getItem(position), position)
        } else if (holder is MoreViewHolder) {
            holder.bind(count)
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
    private val onReviewImageClickListener: OnItemClickListener<ImageModel>,
    private val onReviewReportClickListener: OnItemClickListener<ReviewContentModel>,
    private val onReviewLikeClickListener: OnItemClickListener<ReviewContentModel>,
) : ViewHolder(binding.root) {

    fun bind(item: ReviewContentModel, position: Int) {
        binding.apply {
            index = position
            val photoAdapter = ReviewPhotoRecyclerAdapter(onReviewImageClickListener)
            twNickNameTitle.text = item.reviewWriter.name
            twReviewDate.text = "${item.review.updatedAt.convertUpdateAt()} · "
            twReviewReport.onSingleClick { onReviewReportClickListener.onClick(item) }
            imgMedal.loadUrlImg(item.reviewWriter.medal.iconUrl)
            twMedalName.text = item.reviewWriter.medal.name
            reviewRatingBar.rating = item.review.rating
            recyclerReviewImage.adapter = photoAdapter
            photoAdapter.submitList(item.review.images)
            twReviewContent.text = item.review.contents
            twReviewLike.text = binding.root.context.getString(R.string.str_like, item.stickers.size)
            twReviewLike.isSelected = item.stickers.none { !it.reactedByMe }
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