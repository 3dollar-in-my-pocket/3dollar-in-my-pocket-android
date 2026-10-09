package com.zion830.threedollars.ui.home.data

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.data.screen.asCurationCardsModel
import com.threedollar.data.screen.asCurationModel
import com.threedollar.data.screen.asModel
import com.threedollar.network.data.screen.HomeCurationCardsResponse
import com.threedollar.network.data.screen.HomeCurationSectionResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse

/** 개발 서버에서 캡처한 JSON을 실제 DTO와 mapper로 읽어 상태 테스트 입력을 만든다. */
object HomeCurationTestFixtures {
    private val gson = Gson()

    fun tabs(fileName: String = "HomeFilterScreen.json"): SDHomeBottomSheetTabsModel = gson.fromJson(
        read(fileName),
        HomeFilterScreenResponse::class.java,
    ).asModel().sections.filterIsInstance<HomeScreenSection.HomeBottomSheetTabSectionModel>().single().tabs

    /** mapper의 미지원 탭 제외 전에 raw viewType을 reducer 경계에 넣는 입력이다. */
    fun rawTabs(fileName: String): SDHomeBottomSheetTabsModel {
        val section = JsonParser.parseString(read(fileName)).asJsonObject.getAsJsonArray("sections")
            .single { it.asJsonObject.get("type").asString == "HOME_BOTTOM_SHEET_TAB" }
        return gson.fromJson(section, SDHomeBottomSheetTabsModel::class.java)
    }

    fun section(): SDHomeCurationSectionModel = gson.fromJson(
        read("HomeCurationSection.json"),
        HomeCurationSectionResponse::class.java,
    ).asCurationModel()

    fun cards(): SDHomeCurationCardsModel = gson.fromJson(
        read("HomeCurationCards.json"),
        HomeCurationCardsResponse::class.java,
    ).asCurationCardsModel()

    fun carousel(section: SDHomeCurationSectionModel, carouselId: String): SDHomeCurationItemModel.Carousel =
        section.items.filterIsInstance<SDHomeCurationItemModel.Carousel>().single { it.carouselId == carouselId }

    fun loadedState(): HomeCurationUiState {
        val section = section()
        return HomeCurationUiState(
            tabs = tabs().tabs,
            selectedTabId = "CURATION",
            section = section,
            carousels = section.items.filterIsInstance<SDHomeCurationItemModel.Carousel>().associate { carousel ->
                carousel.carouselId to HomeCurationCarouselState(
                    selectedCategoryId = carousel.defaultCategoryId,
                    cards = SDHomeCurationCardsModel(carousel.cards),
                )
            },
            sectionRequestId = 1L,
            sectionTabId = "CURATION",
            requestLocation = location,
        )
    }

    val location = HomeCurationRequestLocation(
        mapLatitude = 37.5665,
        mapLongitude = 126.9780,
        deviceLatitude = 37.5665,
        deviceLongitude = 126.9780,
    )

    private fun read(fileName: String): String = requireNotNull(
        HomeCurationTestFixtures::class.java.classLoader?.getResourceAsStream("curation/$fileName"),
    ) { "실서버 fixture를 찾을 수 없습니다: $fileName" }.bufferedReader().use { it.readText() }
}
