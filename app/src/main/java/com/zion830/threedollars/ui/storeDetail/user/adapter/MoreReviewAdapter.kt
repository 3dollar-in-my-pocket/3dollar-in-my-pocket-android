package com.zion830.threedollars.ui.storeDetail.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewStatusType
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.ItemMoreReviewBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

class MoreReviewAdapter(private val reviewEditOrDeleteClickEvent: OnItemClickListener<ReviewContentModel>) :
    PagingDataAdapter<ReviewContentModel, MoreReviewViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MoreReviewViewHolder(ItemMoreReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MoreReviewViewHolder, position: Int) {
        holder.bind(getItem(position) as ReviewContentModel, reviewEditOrDeleteClickEvent, position)
    }
}

class MoreReviewViewHolder(private val binding: ItemMoreReviewBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ReviewContentModel, reviewEditOrDeleteClickEvent: OnItemClickListener<ReviewContentModel>, position: Int) {
        binding.blindTextView.isVisible = item.review.status != ReviewStatusType.POSTED
        binding.reviewConstraintLayout.isVisible = item.review.status == ReviewStatusType.POSTED

        if (item.review.status == ReviewStatusType.POSTED) {
            binding.reviewTextView.text = item.review.contents
            binding.nameTextView.text = item.reviewWriter.name
            binding.medalTextView.text = item.reviewWriter.medal.name
            binding.medalImageView.loadImage(item.reviewWriter.medal.iconUrl)
            binding.createdAtTextView.text = StringUtils.getTimeString(item.review.createdAt, "yy.MM.dd E")
            binding.reviewRatingBar.rating = item.review.rating.toFloat()

            if (position % 2 == 0) {
                binding.reviewConstraintLayout.setBackgroundResource(DesignSystemR.drawable.rect_radius_12_gray_0)
                binding.medalLayout.setBackgroundResource(DesignSystemR.drawable.rect_radius_4_pink_100)
                binding.reviewRatingBar.setBackgroundResource(DesignSystemR.drawable.rect_radius_4_pink_100)
            } else {
                binding.reviewConstraintLayout.setBackgroundResource(DesignSystemR.drawable.rect_radius_12_pink_100)
                binding.medalLayout.setBackgroundResource(DesignSystemR.drawable.rect_radius_4_white)
                binding.reviewRatingBar.setBackgroundResource(DesignSystemR.drawable.rect_radius_4_white)
            }

            binding.reportAndEditTextView.onSingleClick { reviewEditOrDeleteClickEvent.onClick(item) }
            binding.reportAndEditTextView.text =
                if (item.review.isOwner) GlobalApplication.getContext().getString(CommonR.string.review_edit) else GlobalApplication.getContext()
                    .getString(CommonR.string.review_report)
        }
    }
}