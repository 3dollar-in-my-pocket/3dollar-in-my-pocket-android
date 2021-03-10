package com.zion830.threedollars.ui.mypage.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFaqDetailBinding
import com.zion830.threedollars.repository.model.response.FaqByTag
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FaqByTagRecyclerAdapter(
    private val listener: OnItemClickListener<FaqByTag>?
) : BaseRecyclerView<ItemFaqDetailBinding, FaqByTag>(R.layout.item_faq_detail) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFaqDetailBinding, FaqByTag>, position: Int) {
        holder.bind(getItem(position), listener)
    }
}