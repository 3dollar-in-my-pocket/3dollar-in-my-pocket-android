package com.zion830.threedollars.ui.mypage.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.R


@BindingAdapter("bindMenuIcon")
fun ImageView.bindMenuIcon(category: String) {
    setImageResource(
        when (category) {
            "BUNGEOPPANG" -> R.drawable.ic_fish
            "TAKOYAKI" -> R.drawable.ic_takoyaki
            "HOTTEOK" -> R.drawable.ic_hodduck
            else -> R.drawable.ic_egg
        }
    )
}