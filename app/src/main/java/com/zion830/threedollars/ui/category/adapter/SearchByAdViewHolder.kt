package com.zion830.threedollars.ui.category.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreListAdBinding
import com.zion830.threedollars.datasource.model.v2.response.Popups
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg

class SearchByAdViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemStoreListAdBinding, Popups>(R.layout.item_store_list_ad, parent) {

    @SuppressLint("Range")
    override fun bind(item: Popups, listener: OnItemClickListener<Popups>?) {
        super.bind(item, listener)
        if (!item.fontColor.isNullOrEmpty()) {
            binding.tvAdTitle.setTextColor(item.fontColor.toColorInt())
            binding.tvAdBody.setTextColor(item.fontColor.toColorInt())
        }
        if (!item.bgColor.isNullOrEmpty()) {
            binding.layoutItem.setBackgroundColor(item.bgColor.toColorInt())
        }
        binding.ivAdImage.loadUrlImg(item.imageUrl)
    }
}