package com.zion830.threedollars.ui.mypage.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.repository.model.MenuType


@BindingAdapter("bindMenuIcon")
fun ImageView.bindMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category).colorIcon)
}

@BindingAdapter("bindWhiteMenuIcon")
fun ImageView.bindWhiteMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category).colorIcon)
}

@BindingAdapter("bindMenuIcon", "isSelected")
fun ImageView.bindSelectableMenuIcon(category: String?, isSelected: Boolean = true) {
    if (category == null) {
        return
    }

    setImageResource(
        if (isSelected) {
            MenuType.of(category).colorIcon
        } else {
            MenuType.of(category).grayIcon
        }
    )
}

@BindingAdapter("bindSmallMenuIcon")
fun ImageView.bindSmallMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category).colorIcon)
}

@BindingAdapter("bindSmallMenuIcon")
fun ImageView.bindSmallMenuIcon(category: MenuType?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category.key).colorIcon)
}