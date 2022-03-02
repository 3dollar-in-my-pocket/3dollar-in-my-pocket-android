package com.zion830.threedollars.ui.category.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStoreListAdBinding
import com.zion830.threedollars.repository.model.v2.response.Popups
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class SearchByAdViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemStoreListAdBinding, Popups>(R.layout.item_store_list_ad, parent) {

    @SuppressLint("Range")
    override fun bind(item: Popups, listener: OnItemClickListener<Popups>?) {
        super.bind(item, listener)
        if (!item.fontColor.isNullOrEmpty()) {
            binding.tvAdTitle.setTextColor(Color.parseColor(item.fontColor))
            binding.tvAdBody.setTextColor(Color.parseColor(item.fontColor))
        }
        if (!item.bgColor.isNullOrEmpty()) {
            binding.layoutItem.setBackgroundColor(Color.parseColor(item.bgColor))
        }

        Glide.with(binding.ivAdImage)
            .load(item.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivAdImage)
    }
}