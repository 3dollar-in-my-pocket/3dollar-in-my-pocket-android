package com.zion830.threedollars.ui.my.page.data

import androidx.annotation.DrawableRes
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

data class MyPageSectionTitleData(
    val topTitle: String,
    @DrawableRes val topIcon: Int,
    val bottomTitle: String,
    val count:Int? = null,
    /** 개수 문구를 직접 지정할 때(예: `N+개`). 있으면 [count] 대신 쓴다. */
    val countText: String? = null,
    val onClick: () -> Unit,
)

val myPageSectionTitlePreview = listOf(
    MyPageSectionTitleData(
        topTitle = "방문인증",
        topIcon = DesignSystemR.drawable.ic_badge_gray,
        bottomTitle = "내가 방문한 가게 알아보기",
        count = 5
    ) {},
    MyPageSectionTitleData(
        topTitle = "줄겨찾기",
        topIcon = DesignSystemR.drawable.ic_favorite_gray,
        bottomTitle = "내가 좋아하는 가게는?",
        count = 11
    ) {},
)