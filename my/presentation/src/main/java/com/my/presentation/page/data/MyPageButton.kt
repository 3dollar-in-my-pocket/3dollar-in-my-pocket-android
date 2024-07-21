package com.my.presentation.page.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.my.presentation.R
import com.threedollar.network.data.user.UserWithDetailApiResponse

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
fun UserWithDetailApiResponse.toMyPageButtons(
    clickCreateStore: () -> Unit,
    clickWriteReview: () -> Unit,
    clickMedals: () -> Unit
): List<MyPageButton> {
    return listOf(
        MyPageButton(activities.createStoreCount.toString(), stringResource(R.string.str_button_create_store), clickCreateStore),
        MyPageButton(activities.writeReviewCount.toString(), stringResource(R.string.str_button_write_review), clickWriteReview),
        MyPageButton(ownedMedals.size.toString(), stringResource(R.string.str_button_medal), clickMedals),
    )
}