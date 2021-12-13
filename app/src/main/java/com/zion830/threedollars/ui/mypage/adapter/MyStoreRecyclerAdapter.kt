package com.zion830.threedollars.ui.mypage.adapter

import android.content.res.ColorStateList
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMyStoreBinding
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class MyStoreRecyclerAdapter(
    private val listener: OnItemClickListener<StoreInfo>
) : PagingDataAdapter<StoreInfo, BaseViewHolder<ItemMyStoreBinding, StoreInfo>>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemMyStoreBinding, StoreInfo>(R.layout.item_my_store, parent) {}

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMyStoreBinding, StoreInfo>, position: Int) {
        holder.bind(getItem(position) ?: StoreInfo(), listener)
        val item = getItem(position)
        val rating = "${item?.rating ?: 0}점"
        val visitCount = ((item?.visitHistory?.existsCounts ?: 0) + (item?.visitHistory?.notExistsCounts ?: 0)).toString() + "명"
        val categoryInfo = SharedPrefUtils.getCategories()
        val categories = item?.categories?.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }
        holder.binding.apply {
            tvStoreName.text = if (item?.isDeleted == true) "없어진 가게에요 :(" else item?.storeName
            tvCategories.text = categories
            tvRating.text = rating
            tvVisitCount.text = visitCount
            tvVisitCount.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(root.context, if (item?.isDeleted == true) R.drawable.ic_badge_off else R.drawable.ic_badge_16),
                null,
                null,
                null
            )
            ivStar.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (item?.isDeleted == true) R.color.gray60 else R.color.white
                )
            )
        }
    }
}