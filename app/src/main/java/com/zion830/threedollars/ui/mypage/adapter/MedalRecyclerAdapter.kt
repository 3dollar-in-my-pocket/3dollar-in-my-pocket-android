package com.zion830.threedollars.ui.mypage.adapter

import androidx.core.content.ContextCompat
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMedalsBinding
import com.zion830.threedollars.repository.model.v2.response.my.Medal
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg

class MedalRecyclerAdapter(
    private val onClick: (MyMedal) -> Unit
) : BaseRecyclerView<ItemMedalsBinding, MyMedal>(R.layout.item_medals) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMedalsBinding, MyMedal>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                onClick(item)
            }
            tvName.text = item.medal.name
            tvName.setTextColor(
                ContextCompat.getColor(
                    root.context,
                    if (item.isSelected || item.isExist) R.color.color_sub_red else R.color.gray60
                )
            )
            tvName.setBackgroundResource(
                when {
                    item.isSelected -> {
                        R.drawable.rect_gray_radius8
                    }
                    item.isExist -> {
                        R.drawable.rect_gray_pink_radius8
                    }
                    else -> {
                        R.drawable.rect_gray95_solid
                    }
                }
            )
            ivMedalIcon.loadUrlImg(if (item.isExist) item.medal.iconUrl else item.medal.disableIconUrl)
            ivMedalIcon.background = ContextCompat.getDrawable(
                root.context,
                when {
                    item.isSelected -> {
                        R.drawable.rect_gray_pink_radius8
                    }
                    else -> {
                        R.drawable.rect_gray95_solid
                    }
                }
            )
        }
    }
}

data class MyMedal(
    val medal: Medal,
    val isSelected: Boolean,
    val isExist: Boolean
)
