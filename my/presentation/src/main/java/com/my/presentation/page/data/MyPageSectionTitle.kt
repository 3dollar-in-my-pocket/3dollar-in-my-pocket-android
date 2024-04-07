package com.my.presentation.page.data

import androidx.annotation.DrawableRes
import com.my.presentation.R

data class MyPageSectionTitle(
    val topTitle: String,
    @DrawableRes val topIcon: Int,
    val bottomTitle: String,
    val count:Int,
    val onClick: () -> Unit,
)

val myPageSectionTitlePreview = listOf(
    MyPageSectionTitle(
        topTitle = "방문인증",
        topIcon = R.drawable.ic_badge_gray,
        bottomTitle = "내가 방문한 가게 알아보기",
        count = 5
    ) {},
    MyPageSectionTitle(
        topTitle = "줄겨찾기",
        topIcon = R.drawable.ic_favorite_gray,
        bottomTitle = "내가 좋아하는 가게는?",
        count = 11
    ) {},
)