package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreReviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel

/**
 * 가게 상세 v2 순수 로직 테스트용 섹션 목록. 실서버 응답(`StoreScreenV2UserStore.json`)의 섹션 순서를 따른다.
 */
object StoreDetailSduiFixtures {
    const val STORE_ID = "118"

    fun appLink(link: String) = SDLink(type = SDLinkType.APP_SCHEME, link = link)

    fun customAction(type: SDCustomActionType?, vararg params: Pair<String, Any?>) =
        SDCustomActionModel(actionType = type, extraParams = params.toMap())

    val tab = SDStoreTabSectionModel(
        sectionId = "TAB",
        tabs = listOf("PREVIEW", "INFO", "IMAGE", "REVIEW").map { fragment ->
            SDActionBarModel(button = SDButtonModel(link = appLink("/stores/$STORE_ID#$fragment")))
        },
        style = null,
    )

    val sections: List<SDSectionModel> = listOf(
        SDStorePreviewSectionModel("PREVIEW", null, null, null, emptyList(), emptyList(), emptyList(), null, null),
        SDStoreMarginSectionModel("MARGIN", 8, null),
        tab,
        SDStoreEditSectionModel(
            sectionId = "EDIT",
            map = SDStoreEditSectionModel.Map(SDStoreEditSectionModel.Location(37.4979, 127.0276), null, null),
            actionBars = emptyList(),
            style = null,
        ),
        SDStoreInfoV1SectionModel("INFO", null, null, null, null),
        SDStoreMarginSectionModel("MARGIN", 8, null),
        SDStoreImageSectionModel(
            sectionId = "IMAGE",
            header = null,
            cards = listOf("a", "b", "c").mapIndexed { index, name ->
                SDStoreImageSectionModel.Card(
                    cardId = "I:$index",
                    image = SDImageModel(url = "https://image/$name.png", style = null),
                    title = null,
                    subTitle = null,
                    link = null,
                    customAction = customAction(
                        SDCustomActionType.STORE_IMAGE_SECTION_IMAGE_ENLARGE,
                        SDCustomActionModel.IMAGE_ID to "$index",
                        SDCustomActionModel.IMAGE_URL to "https://image/$name.png",
                    ),
                    style = null,
                    clickLog = null,
                )
            },
            style = null,
        ),
        SDStoreMarginSectionModel("MARGIN", 8, null),
        SDStoreReviewSectionModel("REVIEW", null, null, emptyList(), null, null),
        SDStoreMarginSectionModel("MARGIN", 8, null),
    )

    const val PREVIEW_INDEX = 0
    const val TAB_INDEX = 2
    const val INFO_INDEX = 4
    const val IMAGE_INDEX = 6
    const val REVIEW_INDEX = 8
}
