package com.zion830.threedollars.ui.mypage.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFaqDetailBinding
import com.zion830.threedollars.datasource.model.v4.faq.FAQ
import com.zion830.threedollars.datasource.model.v4.faq.FAQCategory
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FaqRecyclerAdapter(
    private val deleteAccount: () -> Unit
) : BaseRecyclerView<ItemFaqDetailBinding, FAQ>(R.layout.item_faq_detail) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFaqDetailBinding, FAQ>, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        holder.bind(getItem(position), object : OnItemClickListener<FAQ> {
            override fun onClick(item: FAQ) {
                deleteAccount()
            }
        })
    }
}

data class Faq(
    val tag: FAQCategory = FAQCategory("", "", 0),
    val content: List<FAQCategory> = listOf()
)