package com.zion830.threedollars.ui.mypage.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.visit_history.MyVisitHistoryV2
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyVisitHistoryBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.onSingleClick

class MyVisitHistoryRecyclerAdapter(
    private val listener: OnItemClickListener<MyVisitHistoryV2>,
    private val deleteListener: OnItemClickListener<MyVisitHistoryV2>,
    private val isDeletable: (MyVisitHistoryV2) -> Boolean,
) : PagingDataAdapter<MyVisitHistoryV2, BaseViewHolder<ItemMyVisitHistoryBinding, MyVisitHistoryV2>>(BaseDiffUtilCallback()) {

    private var isEditMode = false

    fun setEditMode(isEditMode: Boolean) {
        if (this.isEditMode == isEditMode) return
        this.isEditMode = isEditMode
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyVisitHistoryBinding, MyVisitHistoryV2>(R.layout.item_my_visit_history, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyVisitHistoryBinding, MyVisitHistoryV2>, position: Int) {
        val item = getItem(position) ?: MyVisitHistoryV2()
        holder.bind(item, if (item.store.isDeleted == false && !isEditMode) listener else null)

        val beforeItem = if (position > 0) getItem(position - 1) else null
        val categories = item.store.categories.orEmpty().map { it.name }.joinToString(" ") { "#$it" }

        holder.binding.apply {
            tvCreatedAt.text = StringUtils.getTimeString(item.visit.createdAt, "yy.MM.dd")

            layoutDate.isVisible = !(position > 0 && StringUtils.getTimeString(beforeItem?.visit?.createdAt, "yy.MM.dd") == tvCreatedAt.text)
            tvCreatedAt.text = StringUtils.getTimeString(item.visit.createdAt, "HH:MM:ss")
            tvDate.text = StringUtils.getTimeString(item.visit.createdAt, "MM월 d일 E요일")
            tvCategories.text = categories
            ivDelete.isVisible = isEditMode && isDeletable(item)
            ivDelete.onSingleClick { deleteListener.onClick(item) }
        }
    }
}
