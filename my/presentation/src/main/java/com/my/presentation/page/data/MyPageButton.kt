package com.my.presentation.page.data

data class MyPageButton(
    val topText: String,
    val bottomText: String,
    val onClick: () -> Unit
)

val myPageButtonPreview = listOf(
    MyPageButton("5","제보한 가게") {},
    MyPageButton("11","내가쓴 리뷰") {},
    MyPageButton("5","내 칭호") {},

)