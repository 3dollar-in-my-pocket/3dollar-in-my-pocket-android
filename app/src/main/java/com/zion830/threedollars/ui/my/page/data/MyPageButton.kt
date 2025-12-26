package com.zion830.threedollars.ui.my.page.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.threedollar.common.R as CommonR
import com.threedollar.domain.my.model.UserInfoModel

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
        MyPageButton(activity.storesCount.toString(), stringResource(CommonR.string.str_button_create_store), clickCreateStore),
        MyPageButton(activity.reviewsCount.toString(), stringResource(CommonR.string.str_button_write_review), clickWriteReview),
        MyPageButton(activity.medalCount.toString(), stringResource(CommonR.string.str_button_medal), clickMedals), // TODO: medals count 필드 추가 필요
    )
}