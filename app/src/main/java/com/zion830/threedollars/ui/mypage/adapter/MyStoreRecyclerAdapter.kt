package com.zion830.threedollars.ui.mypage.adapter

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.store.MyReportedContent
import com.threedollar.network.data.store.MyReportedStore
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyStoreBinding
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class MyStoreRecyclerAdapter(
    private val listener: OnItemClickListener<MyReportedStore>
) : PagingDataAdapter<MyReportedContent, BaseViewHolder<ItemMyStoreBinding, MyReportedContent>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyStoreBinding, MyReportedContent>(R.layout.item_my_store, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyStoreBinding, MyReportedContent>, position: Int) {
        val item = getItem(position)
        holder.binding.item = item?.store ?: MyReportedStore()
        holder.binding.listener = if (item?.store?.isDeleted == false) listener else null
        val rating = "${item?.store?.rating ?: 0}점"
        val visitCount = ((item?.visits?.count?.existsCounts ?: 0) + (item?.visits?.count?.notExistsCounts ?: 0)).toString() + "명"
        val categoryInfo = LegacySharedPrefUtils.getCategories()
        val categories =
            item?.store?.categories?.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.categoryId == it.categoryId }?.name}" }
        holder.binding.apply {
            tvStoreName.text = if (item?.store?.isDeleted == true) "없어진 가게에요 :(" else item?.store?.name
            tvCategories.text = categories
            tvRating.text = rating
            tvVisitCount.text = visitCount
            tvVisitCount.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(root.context, if (item?.store?.isDeleted == true) R.drawable.ic_badge_off else R.drawable.ic_badge_mini),
                null,
                null,
                null
            )
            ivStar.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (item?.store?.isDeleted == true) R.color.gray60 else R.color.color_white
                )
            )
        }
    }
}