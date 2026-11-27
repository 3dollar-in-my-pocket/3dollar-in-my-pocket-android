package com.threedollar.common.sdui.model.section.home

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel

/**
 * 홈 동네 소식 섹션 한 페이지. [items] 는 서버 순서 그대로이고, 광고 자리도 서버가 정한다.
 * 다음 페이지는 [hasMore] 이고 [nextCursor] 가 있을 때만 있다.
 */
data class SDHomeNewsSectionModel(
    val header: SDHeaderModel? = null,
    val items: List<SDHomeNewsItemModel> = emptyList(),
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val impressionLog: SDLogModel? = null
) {
    val nextPageCursor: String?
        get() = nextCursor?.takeIf { hasMore && it.isNotBlank() }
}

sealed interface SDHomeNewsItemModel {
    val cardId: String
    val impressionLog: SDLogModel?

    data class ContentCard(
        override val cardId: String,
        val badge: SDChipModel?,
        val trailingText: SDTextModel?,
        val summary: SDHomeNewsSummaryModel?,
        val bodyBlocks: List<SDHomeNewsBodyBlockModel> = emptyList(),
        val link: SDLink?,
        val clickLog: SDLogModel? = null,
        override val impressionLog: SDLogModel? = null
    ) : SDHomeNewsItemModel

    data class AdMob(val card: SDAdMobCardModel) : SDHomeNewsItemModel {
        override val cardId: String get() = card.cardId
        override val impressionLog: SDLogModel? get() = card.impressionLog
    }
}

data class SDHomeNewsSummaryModel(
    val leadingImage: SDImageModel?,
    val overline: SDTextModel?,
    val title: SDTextModel?,
    val metadata: List<SDChipModel> = emptyList()
)

data class SDHomeNewsBodyBlockModel(
    val heading: SDHomeNewsBodyHeadingModel?,
    val images: List<SDImageModel> = emptyList(),
    val textLeadingImage: SDImageModel?,
    val text: SDTextModel?,
    val style: SDSurfaceStyleModel?
)

data class SDHomeNewsBodyHeadingModel(
    val leadingImage: SDImageModel?,
    val title: SDTextModel?,
    val trailingImages: List<SDImageModel> = emptyList()
)
