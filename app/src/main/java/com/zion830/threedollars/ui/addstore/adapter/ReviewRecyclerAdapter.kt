package com.zion830.threedollars.ui.addstore.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemReviewBinding
import com.zion830.threedollars.repository.model.response.Review
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class ReviewRecyclerAdapter(
    reviewClickEvent: OnItemClickListener<Review>
) : BaseRecyclerView<ItemReviewBinding, Review>(R.layout.item_review) {

    init {
        clickListener = reviewClickEvent
    }
}