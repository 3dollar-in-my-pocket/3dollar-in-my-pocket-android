package com.zion830.threedollars.ui.favorite.viewer

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.zion830.threedollars.R
import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.common.R as CommonR

@BindingAdapter("textFavoriteUserName")
fun TextView.textFavoriteUserName(textFavoriteUserName: String) {
    text = context.getString(CommonR.string.favorite_user_name, textFavoriteUserName)
    val span = text as Spannable
    span.setSpan(StyleSpan(Typeface.BOLD), 0, textFavoriteUserName.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = span
}

@BindingAdapter("textFavoriteTitle")
fun TextView.textFavoriteTitle(item: MyFavoriteFolderResponse) {
    val title = item.name.ifEmpty { context.getString(CommonR.string.favorite_user_name, item.user.name) }
    text = title
}

@BindingAdapter("textCategory")
fun TextView.textCategory(item:List<MyFavoriteFolderResponse.MyFavoriteFolderCategoryModel>){
    val category = item.joinToString(" ") { "#${it.name}" }.trim()
    text = category
}
