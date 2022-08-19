package com.zion830.threedollars.ui.mypage.adapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.BossCategoriesResponse
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo


@BindingAdapter("bindMenuIcon")
fun ImageView.bindMenuIcon(category: String?) {
    if (category == null) {
        return
    }

    setImageResource(MenuType.of(category).colorIcon)
}

@BindingAdapter("bindMenuIcons", "isSelected", requireAll = false)
fun ImageView.bindMenuIcons(category: List<String>? = emptyList(), isSelected: Boolean? = true) {
    if (category.isNullOrEmpty()) {
        return
    }

    val menu = MenuType.of(category.first())
    setImageResource(if (isSelected == null || isSelected) menu.colorIcon else menu.grayIcon)
}

@BindingAdapter("bindMenuIntroTitle")
fun TextView.bindMenuIntroTitle(menuType: CategoryInfo?) {
    if (menuType == null) {
        return
    }

    val menuName = if (menuType.name == "닭꼬치") "꼬치꼬치" else menuType.name
    val index = menuType.description.indexOf(menuName)
    val spannableString = SpannableStringBuilder(menuType.description).apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            index,
            index + menuName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    text = spannableString
}

@BindingAdapter("bindMenuIntroTitle")
fun TextView.bindMenuIntroTitle(menuType: BossCategoriesResponse.BossCategoriesModel?) {
    if (menuType == null) {
        return
    }

    val menuName = menuType.name
    val index = menuType.description.indexOf(menuName)
    val spannableString = SpannableStringBuilder(menuType.description).apply {
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

@BindingAdapter("bindWhiteMenuIcon")
fun ImageView.bindWhiteMenuIcon(categories: List<String>?) {
    if (categories.isNullOrEmpty()) {
        return
    }

    setImageResource(MenuType.of(categories.firstOrNull()).colorIcon)
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