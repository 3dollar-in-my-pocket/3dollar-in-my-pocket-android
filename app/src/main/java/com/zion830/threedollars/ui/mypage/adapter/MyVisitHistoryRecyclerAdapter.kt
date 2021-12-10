package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyVisitHistoryBinding
import com.zion830.threedollars.repository.model.v2.response.visit_history.VisitHistoryContent
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyVisitHistoryRecyclerAdapter(
    private val listener: OnItemClickListener<VisitHistoryContent>,
) : PagingDataAdapter<VisitHistoryContent, BaseViewHolder<ItemMyVisitHistoryBinding, VisitHistoryContent>>(object :
    DiffUtil.ItemCallback<VisitHistoryContent>() {
    override fun areItemsTheSame(oldItem: VisitHistoryContent, newItem: VisitHistoryContent): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: VisitHistoryContent, newItem: VisitHistoryContent): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyVisitHistoryBinding, VisitHistoryContent>(R.layout.item_my_visit_history, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyVisitHistoryBinding, VisitHistoryContent>, position: Int) {
        holder.bind(getItem(position) ?: VisitHistoryContent(), listener)
        val item = getItem(position) ?: VisitHistoryContent()
        val beforeItem = if (position > 0) getItem(position - 1) else null
        val categoryInfo = SharedPrefUtils.getCategories()
        val categories = item.store.categories.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }

        holder.binding.apply {
            tvCreatedAt.text = StringUtils.getTimeString(item.createdAt, "yy.MM.dd")

            layoutDate.isVisible = !(position > 0 && StringUtils.getTimeString(beforeItem?.createdAt, "yy.MM.dd") == tvCreatedAt.text)
            tvCreatedAt.text = StringUtils.getTimeString(item.createdAt, "HH:MM:ss")
            tvDate.text = StringUtils.getTimeString(item.createdAt, "MM월 d일 E요일")
            tvCategories.text = categories
        }
    }
}