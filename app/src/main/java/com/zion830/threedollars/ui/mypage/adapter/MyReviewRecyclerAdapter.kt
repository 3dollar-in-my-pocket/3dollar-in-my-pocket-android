package com.zion830.threedollars.ui.mypage.adapter

import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyReviewBinding
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyReviewRecyclerAdapter(
    private val listener: OnItemClickListener<ReviewDetail>,
    private val editReview: (ReviewDetail) -> Unit,
    private val deleteReview: (ReviewDetail) -> Unit,
) : PagingDataAdapter<ReviewDetail, BaseViewHolder<ItemMyReviewBinding, ReviewDetail>>(object : DiffUtil.ItemCallback<ReviewDetail>() {
    override fun areItemsTheSame(oldItem: ReviewDetail, newItem: ReviewDetail): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: ReviewDetail, newItem: ReviewDetail): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyReviewBinding, ReviewDetail>(R.layout.item_my_review, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyReviewBinding, ReviewDetail>, position: Int) {
        holder.bind(getItem(position) ?: ReviewDetail(), listener)
        val item = getItem(position) ?: ReviewDetail()
        val previousItem = if (position > 0) getItem(position - 1) else null

        holder.binding.apply {
            tvCreatedAt.text = StringUtils.getTimeString(item.createdAt, "yy.MM.dd E")

            val titleVisibility = !(position > 0 && previousItem?.store?.storeId == item.store.storeId)
            tvStoreName.isVisible = titleVisibility
            ivCategory.isVisible = titleVisibility

            val popupMenu = PopupMenu(ibSideMenu.context, ibSideMenu, Gravity.BOTTOM, 0, R.style.PopupMenu)
            popupMenu.menuInflater.inflate(R.menu.review_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit_review -> {
                        editReview(item)
                        false
                    }
                    else -> {
                        deleteReview(item)
                        false
                    }
                }
            }
            ibSideMenu.setOnClickListener {
                popupMenu.show()
            }
        }
    }
}