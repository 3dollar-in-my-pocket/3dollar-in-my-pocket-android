package com.zion830.threedollars.ui.mypage.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.model.MenuType
import java.util.*


@BindingAdapter("bindMenuIcon")
fun ImageView.bindMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(
        when (category.toUpperCase(Locale.ROOT)) {
            "BUNGEOPPANG" -> R.drawable.ic_fish
            "TAKOYAKI" -> R.drawable.ic_takoyaki
            "HOTTEOK" -> R.drawable.ic_hodduck
            else -> R.drawable.ic_egg
        }
    )
}

@BindingAdapter("bindWhiteMenuIcon")
fun ImageView.bindWhiteMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(
        when (category.toUpperCase(Locale.ROOT)) {
            "BUNGEOPPANG" -> R.drawable.ic_fish_white
            "TAKOYAKI" -> R.drawable.ic_tako_white
            "HOTTEOK" -> R.drawable.ic_hodduck_white
            else -> R.drawable.ic_egg_white
        }
    )
}

@BindingAdapter("bindMenuIcon", "isSelected")
fun ImageView.bindMenuIcon(category: String?, isSelected: Boolean) {
    if (category == null) {
        return
    }

    setImageResource(
        if (isSelected) {
            when (category.toUpperCase(Locale.ROOT)) {
                "BUNGEOPPANG" -> R.drawable.ic_fish
                "TAKOYAKI" -> R.drawable.ic_takoyaki
                "HOTTEOK" -> R.drawable.ic_hodduck
                else -> R.drawable.ic_egg
            }
        } else {
            when (category.toUpperCase(Locale.ROOT)) {
                "BUNGEOPPANG" -> R.drawable.img_fish_card_off
                "TAKOYAKI" -> R.drawable.img_takoyaki_card_off
                "HOTTEOK" -> R.drawable.ic_hodduck_card_off
                else -> R.drawable.ic_egg_card_off
            }
        }
    )
}

@BindingAdapter("bindMenuBigIcon", "isSelected", requireAll = true)
fun ImageView.bindMenuBigIcon(category: String?, isSelected: Boolean) {
    if (category == null) {
        return
    }

    setImageResource(
        when (category.toUpperCase()) {
            "BUNGEOPPANG" -> if (isSelected) R.drawable.ic_fish else R.drawable.ic_gray_fish
            "TAKOYAKI" -> if (isSelected) R.drawable.ic_takoyaki else R.drawable.ic_takoyaki_gray
            "HOTTEOK" -> if (isSelected) R.drawable.ic_hodduck else R.drawable.ic_hodduck_gray
            else -> if (isSelected) R.drawable.ic_egg else R.drawable.ic_egg_gray
        }
    )
}

@BindingAdapter("bindSmallMenuIcon")
fun ImageView.bindSmallMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(
        when (category.toUpperCase()) {
            "BUNGEOPPANG" -> R.drawable.ic_menu1
            "TAKOYAKI" -> R.drawable.ic_menu2
            "HOTTEOK" -> R.drawable.ic_menu4
            else -> R.drawable.ic_menu3
        }
    )
}

@BindingAdapter("bindSmallMenuIcon")
fun ImageView.bindSmallMenuIcon(category: MenuType?) {
    if (category == null) {
        return
    }

    setImageResource(
        when (category.key) {
            "BUNGEOPPANG" -> R.drawable.ic_menu1
            "TAKOYAKI" -> R.drawable.ic_menu2
            "HOTTEOK" -> R.drawable.ic_menu4
            else -> R.drawable.ic_menu3
        }
    )
}