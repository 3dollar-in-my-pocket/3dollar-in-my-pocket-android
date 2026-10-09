package com.zion830.threedollars.core.ui.sdui.preview

import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCategoryFilterModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.threedollar.common.sdui.model.section.home.SDHomeTabAppearanceModel

/**
 * Figma 11349:46867의 표시 예시를 공통 SDUI 모델로 옮긴 Preview 입력이다. 실서버 응답이나 테스트 fixture가 아니다.
 * debug 디자인 화면에서는 기존 Figma 원본 로컬 asset으로 이미지만 대체한다.
 */
object HomeCurationPreviewFixtures {
    private const val WHITE = "#FFFFFF"
    private const val GRAY50 = "#969696"
    private const val PHOTO = "https://storage.dev.threedollars.co.kr/boss/store/v1/v1-bd8f9756-c3a2-4333-be66-f6cfc39054bb.jpeg"
    private const val MENU = "https://storage.threedollars.co.kr/menu/v1_bungeoppang.png?version=1"
    private const val APP_IMAGES = "https://storage.threedollars.co.kr/app/"

    val tabs = SDHomeBottomSheetTabsModel(
        tabs = listOf(
            tab("CURATION", "CURATION", "🔥 요즘 뜨는 간식", true),
            tab("DEFAULT", "STORE_LIST", "내 주변 간식", false),
        ),
    )

    val section = SDHomeCurationSectionModel(
        items = listOf(
            carousel(
                "POPULAR_SNACKS", "요즘 뜨는 간식",
                listOf("BUNGEOPPANG" to "붕어빵", "PIZZA_SEOLGI" to "피자설기", "SALT_BREAD" to "왁뿌소금빵", "FRUIT_SANDO" to "과일산도", "DUBAI" to "두바이"),
            ),
            carousel(
                "TASTE_SNACKS", "입맛별로 찾는 간식",
                listOf("MATCHA" to "🍃 말차", "DUBAI" to "🧆 두바이", "GRANDMA_TASTE" to "👵 할미입맛", "FRUIT" to "🍑 과일간식", "BAKERY" to "베이커리"),
            ),
        ),
    )

    val cardsByCarousel: Map<String, SDHomeCurationCardsModel> = section.items
        .filterIsInstance<SDHomeCurationItemModel.Carousel>()
        .associate { it.carouselId to SDHomeCurationCardsModel(it.cards) }

    private fun tab(id: String, viewType: String, title: String, defaultSelected: Boolean) = SDHomeBottomSheetTabModel(
        tabId = id,
        viewType = viewType,
        selected = SDHomeTabAppearanceModel(text(title, "#181818"), surface(WHITE, "#E2E2E2")),
        unselected = SDHomeTabAppearanceModel(text(title, "#5A5A5A"), surface("#F4F4F4")),
        defaultSelected = defaultSelected,
    )

    private fun carousel(id: String, title: String, categories: List<Pair<String, String>>) = SDHomeCurationItemModel.Carousel(
        carouselId = id,
        header = SDHeaderModel(html(title, size = 20, weight = 700)),
        defaultCategoryId = categories.first().first,
        categoryFilters = categories.map { (categoryId, label) ->
            SDHomeCurationCategoryFilterModel(
                categoryId = categoryId,
                selected = SDChipModel(image = null, text = text(label, "#DE616B"), style = surface("#FFF3F4", "#DE616B")),
                unselected = SDChipModel(image = null, text = text(label, "#232323"), style = surface(WHITE, "#E2E2E2")),
            )
        },
        cards = listOf(
            card("1", "효공잉어빵", "129m", "4.9", PHOTO),
            card("2", "혜원빙수", "935m", "4.9", PHOTO),
            card("3", "청계산입구역 1번 출구", "1km +", "4.5", MENU, 47f, 30f),
            card("4", "강남역 0번 출구 앞 붕어빵", "1km +", "4.5", MENU, 47f, 30f),
        ),
    )

    private fun card(id: String, name: String, distance: String, rating: String, image: String, imageSize: Float = 100f, imageHeight: Float = imageSize) = ImagePreviewCardModel(
        cardId = "S:$id",
        image = SDImageModel(image, SDImageModel.Style(imageSize, imageHeight)),
        title = text(name),
        style = ImagePreviewCardModel.Style(WHITE),
        metricLabel = listOf(
            SDChipModel(image = icon("location_gray.png"), text = text(distance, GRAY50)),
            SDChipModel(image = icon("review_gray.png"), text = text("23개", GRAY50)),
        ),
        contextLabel = listOf(SDChipModel(image = icon("star_pink.png"), text = text(rating, "#FF858F"))),
        link = SDLink(SDLinkType.APP_SCHEME, "/store?storeId=$id&storeType=USER_STORE"),
        refs = null,
    )

    private fun text(value: String, color: String = "#0F0F0F") = SDTextModel(value, isHtml = false, fontColor = color)
    private fun html(value: String, size: Int, weight: Int) = SDTextModel(
        text = "<span style=\"font-size:${size}px; font-weight:$weight\">$value</span>",
        isHtml = true,
        fontColor = "#0F0F0F",
    )
    private fun surface(background: String, border: String? = null) = SDSurfaceStyleModel(background, border?.let { SDSurfaceStyleModel.Border(it, 1f) })
    private fun icon(name: String) = SDImageModel(APP_IMAGES + name, SDImageModel.Style(12f, 12f))
}
