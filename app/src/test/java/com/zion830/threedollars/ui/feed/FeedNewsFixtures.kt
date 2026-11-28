package com.zion830.threedollars.ui.feed

import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsBodyBlockModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeNewsSectionModel

/** 동네 소식 섹션 테스트 입력. 카드 id 는 서버 형식(`NEWS:{feedId}`, `ADMOB:{uuid}`)을 따른다. */
internal object FeedNewsFixtures {

    val header = SDHeaderModel(
        title = SDTextModel(text = "우리 동네 가게 소식", isHtml = true, fontColor = "#0F0F0F"),
        subTitle = SDTextModel(text = "내 주변 가게들의 소식을 확인해요.", isHtml = true, fontColor = "#787878"),
    )

    fun newsCard(feedId: String, imageUrls: List<String> = emptyList()) = SDHomeNewsItemModel.ContentCard(
        cardId = "NEWS:$feedId",
        badge = null,
        trailingText = null,
        summary = null,
        bodyBlocks = listOf(
            SDHomeNewsBodyBlockModel(
                heading = null,
                images = imageUrls.map { SDImageModel(url = it, style = SDImageModel.Style(width = 124f, height = 124f)) },
                textLeadingImage = null,
                text = SDTextModel(text = "본문", isHtml = true, fontColor = null),
                style = null,
            ),
        ),
        link = SDLink(type = SDLinkType.APP_SCHEME, link = "/store?storeId=$feedId&view=collapsed"),
        clickLog = SDLogModel(eventType = "CLICK", screenName = "home", objectType = "card", objectId = "home_news_card"),
        impressionLog = SDLogModel(eventType = "IMPRESSION", screenName = "home", objectType = "card", objectId = "home_news_card"),
    )

    fun adMobCard(uuid: String = "00f3ad1c") = SDHomeNewsItemModel.AdMob(
        card = SDAdMobCardModel(cardId = "ADMOB:$uuid", height = 50, adUnitId = "ca-app-pub-test"),
    )

    fun page(vararg feedIds: String, nextCursor: String? = null) = pageOf(feedIds.map { newsCard(it) }, nextCursor)

    fun pageOf(range: IntRange, nextCursor: String?) = pageOf(range.map { newsCard("%024d".format(it)) }, nextCursor)

    fun pageOf(items: List<SDHomeNewsItemModel>, nextCursor: String?) = SDHomeNewsSectionModel(
        header = header,
        items = items,
        nextCursor = nextCursor,
        hasMore = nextCursor != null,
        impressionLog = SDLogModel(eventType = "IMPRESSION", screenName = "home", objectType = "section", objectId = "home_news"),
    )
}
