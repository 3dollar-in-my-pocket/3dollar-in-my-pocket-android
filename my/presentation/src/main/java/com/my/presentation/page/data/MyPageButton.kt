package com.my.presentation.page.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.my.presentation.R
import com.my.domain.model.UserInfoModel

data class MyPageButton(
    val topText: String,
    val bottomText: String,
    val onClick: () -> Unit
)

val myPageButtonPreview = listOf(
    MyPageButton("5", "제보한 가게") {},
    MyPageButton("11", "내가쓴 리뷰") {},
    MyPageButton("5", "내 칭호") {},

    )

@Composable
fun UserInfoModel.toMyPageButtons(
    clickCreateStore: () -> Unit,
    clickWriteReview: () -> Unit,
    clickMedals: () -> Unit
): List<MyPageButton> {
    return listOf(
        MyPageButton(activity.storesCount.toString(), stringResource(R.string.str_button_create_store), clickCreateStore),
        MyPageButton(activity.reviewsCount.toString(), stringResource(R.string.str_button_write_review), clickWriteReview),
        MyPageButton("0", stringResource(R.string.str_button_medal), clickMedals), // TODO: medals count 필드 추가 필요
    )
}