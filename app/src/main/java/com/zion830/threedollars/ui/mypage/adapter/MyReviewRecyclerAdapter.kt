package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyReviewBinding
import com.zion830.threedollars.repository.model.response.Review
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyReviewRecyclerAdapter(
    private val reviewClickEvent: OnItemClickListener<Review>,
) : PagedListAdapter<Review, BaseViewHolder<ItemMyReviewBinding, Review>>(object : DiffUtil.ItemCallback<Review?>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyReviewBinding, Review>(R.layout.item_my_review, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyReviewBinding, Review>, position: Int) {
        holder.bind(getItem(position) ?: Review(), reviewClickEvent)
    }
}