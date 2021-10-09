package com.zion830.threedollars.ui.mypage.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.repository.model.MenuType


@BindingAdapter("bindMenuIcon")
fun ImageView.bindMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category).colorIcon)
}

@BindingAdapter("bindMenuIcons")
fun ImageView.bindMenuIcons(category: List<MenuType>?) {
    if (category.isNullOrEmpty()) {
        return
    }

    setImageResource(category.first().colorIcon)
}

@BindingAdapter("bindMenuIntroTitle")
fun TextView.bindMenuIntroTitle(menuType: MenuType?) {
    if (menuType == null) {
        return
    }

    val menuName = if (context.getString(menuType.displayNameId) == "꼬치") "꼬치꼬치" else context.getString(menuType.displayNameId)
    val index = menuType.introTitle.indexOf(menuName)
    val spannableString = SpannableStringBuilder(menuType.introTitle).apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            index,
            index + menuName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    text = spannableString
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