package com.zion830.threedollars.ui.home.design

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionAdditionalInfosModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.home.ui.compose.HomeBottomSheetContent

class HomeStorePreviewDesignActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()

        val storeScreen = designStoreScreen()

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE8EDF0)),
            ) {
                HomeBottomSheetContent(
                    homeListSection = HomeListSectionModel(),
                    storeScreen = storeScreen,
                    onCardClick = {},
                    onLoadNextPage = {},
                    onClosePreview = {},
                    onActionClick = {},
                    fullListTopPx = 0,
                )
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

private fun Context.designStoreScreen(): StoreScreenModel {
    val badge = SDImageModel(
        url = drawableUri(R.drawable.design_store_preview_badge),
        style = SDImageStyleModel(width = 16.0, height = 16.0),
    )
    val star = SDImageModel(
        url = drawableUri(R.drawable.design_store_preview_star),
        style = SDImageStyleModel(width = 16.0, height = 16.0),
    )
    val image1 = SDImageModel(url = drawableUri(R.drawable.design_store_preview_image_1))
    val image2 = SDImageModel(url = drawableUri(R.drawable.design_store_preview_image_2))
    val review = SDTextModel(
        text = "안녕하세요! 소중한 시간을 내어 저희 붕어빵에 대한 따뜻한 리뷰를 남겨주셔서 정말 감사드립니다. 고객님의 칭찬 ",
        isHtml = false,
        fontColor = "#5A5A5A",
    )

    return StoreScreenModel(
        sections = listOf(
            StoreSectionModel.Preview(
                type = "PREVIEW",
                header = HomeListCardHeaderModel(
                    title = SDTextModel(
                        text = "강남역 0번 출구 앞 붕어빵",
                        isHtml = false,
                        fontColor = "#0F0F0F",
                    ),
                    badge = badge,
                ),
                metadata = HomeListCardMetadataModel(
                    primary = listOf(
                        SDChipModel(
                            text = SDTextModel(
                                text = "붕어빵",
                                isHtml = false,
                                fontColor = "#5A5A5A",
                            ),
                        ),
                        SDChipModel(
                            image = star,
                            text = SDTextModel(
                                text = "4.6",
                                isHtml = false,
                                fontColor = "#787878",
                            ),
                            additionalText = SDTextModel(
                                text = "(8)",
                                isHtml = false,
                                fontColor = "#787878",
                            ),
                        ),
                    ),
                    secondary = listOf(
                        SDChipModel(
                            text = SDTextModel(
                                text = "영업 중",
                                isHtml = false,
                                fontColor = "#2E2E2E",
                            ),
                        ),
                        SDChipModel(
                            text = SDTextModel(
                                text = "1km +",
                                isHtml = false,
                                fontColor = "#787878",
                            ),
                        ),
                        SDChipModel(
                            text = SDTextModel(
                                text = "최근 방문 5명",
                                isHtml = false,
                                fontColor = "#787878",
                            ),
                        ),
                    ),
                ),
                additionalInfos = StoreSectionAdditionalInfosModel(
                    type = "STORE",
                    isSubscriber = false,
                ),
                actionBars = listOf(
                    StoreActionBarModel(
                        type = "VISIT",
                        button = SDButtonModel(
                            text = SDTextModel(
                                text = "방문 인증",
                                isHtml = false,
                                fontColor = "#FFFFFF",
                            ),
                            imageAlignment = "END",
                            link = SDLinkModel(type = "APP", link = "/visit"),
                        ),
                    ),
                    StoreActionBarModel(
                        type = "REVIEW",
                        button = SDButtonModel(
                            text = SDTextModel(
                                text = "리뷰 작성",
                                isHtml = false,
                                fontColor = "#F9737E",
                            ),
                        ),
                    ),
                    StoreActionBarModel(
                        type = "SHARE",
                        button = SDButtonModel(
                            text = SDTextModel(
                                text = "공유",
                                isHtml = false,
                                fontColor = "#5A5A5A",
                            ),
                            customAction = SDCustomActionModel(actionType = "STORE_PREVIEW_SECTION_SHARE"),
                        ),
                    ),
                    StoreActionBarModel(
                        type = "NAVIGATION",
                        button = SDButtonModel(
                            text = SDTextModel(
                                text = "길안내",
                                isHtml = false,
                                fontColor = "#5A5A5A",
                            ),
                            customAction = SDCustomActionModel(actionType = "STORE_PREVIEW_SECTION_NAVIGATION"),
                        ),
                    ),
                ),
                images = listOf(image1, image2, image2),
                bodies = listOf(review, review),
            ),
        ),
    )
}

private fun Context.drawableUri(@DrawableRes resId: Int): String {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(packageName)
        .appendPath(resId.toString())
        .build()
        .toString()
}
