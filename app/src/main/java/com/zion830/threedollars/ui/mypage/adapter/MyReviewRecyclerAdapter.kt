package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyReviewBinding
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyReviewRecyclerAdapter(
    private val reviewClickEvent: OnItemClickListener<ReviewDetail>,
) : PagedListAdapter<ReviewDetail, BaseViewHolder<ItemMyReviewBinding, ReviewDetail>>(object : DiffUtil.ItemCallback<ReviewDetail?>() {
    override fun areItemsTheSame(oldItem: ReviewDetail, newItem: ReviewDetail): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: ReviewDetail, newItem: ReviewDetail): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyReviewBinding, ReviewDetail>(R.layout.item_my_review, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyReviewBinding, ReviewDetail>, position: Int) {
        holder.bind(getItem(position) ?: ReviewDetail(), reviewClickEvent)
    }
}