package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.user.MyReviewResponseData
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyReviewBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class MyReviewRecyclerAdapter(
    private val listener: OnItemClickListener<MyReviewResponseData>,
) : PagingDataAdapter<MyReviewResponseData, BaseViewHolder<ItemMyReviewBinding, MyReviewResponseData>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyReviewBinding, MyReviewResponseData>(R.layout.item_my_review, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyReviewBinding, MyReviewResponseData>, position: Int) {
        val item = getItem(position) ?: MyReviewResponseData()
        holder.bind(item, if (item.store.isDeleted == false) listener else null)
        val previousItem = if (position > 0) getItem(position - 1) else null

        holder.binding.apply {
            tvCreatedAt.text = StringUtils.getTimeString(item.review.createdAt, "yyyy.MM.dd")

            val titleVisibility = !(position > 0 && previousItem?.store?.storeId == item.store.storeId)
            tvStoreName.isVisible = titleVisibility
            ivCategory.isVisible = titleVisibility


        }
    }
}