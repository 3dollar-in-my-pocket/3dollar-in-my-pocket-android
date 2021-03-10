package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFaqsBinding
import com.zion830.threedollars.repository.model.response.FaqByTag
import com.zion830.threedollars.repository.model.response.FaqTag
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class FaqRecyclerAdapter(
    private val deleteAccount: () -> Unit
) : ListAdapter<Faq, FaqViewHolder>(object : DiffUtil.ItemCallback<Faq?>() {
    override fun areItemsTheSame(oldItem: Faq, newItem: Faq): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Faq, newItem: Faq): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder = FaqViewHolder(parent)

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(getItem(position), object : OnItemClickListener<Faq> {
            override fun onClick(item: Faq) {
                deleteAccount()
            }
        })
    }
}

class FaqViewHolder(
    parent: ViewGroup
) : BaseViewHolder<ItemFaqsBinding, Faq>(R.layout.item_faqs, parent) {

    override fun bind(item: Faq, listener: OnItemClickListener<Faq>?) {
        super.bind(item, listener)
        binding.tvTitle.text = item.tag.name
        val adapter = FaqByTagRecyclerAdapter(object : OnItemClickListener<FaqByTag> {
            override fun onClick(item: FaqByTag) {
                listener?.onClick(Faq())
            }
        })
        binding.rvFaqs.adapter = adapter
        adapter.submitList(item.content)
    }
}

data class Faq(
    val tag: FaqTag = FaqTag(0, 0, ""),
    val content: List<FaqByTag> = listOf()
)