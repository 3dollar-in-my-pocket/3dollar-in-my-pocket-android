package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.network.data.screen.HomeListCardHeaderResponse
import com.threedollar.network.data.screen.HomeListCardMetadataResponse
import com.threedollar.network.data.screen.HomeListCardResponse
import com.threedollar.network.data.screen.HomeListMarkerResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.SDButtonResponse
import com.threedollar.network.data.screen.SDChipResponse
import com.threedollar.network.data.screen.SDClickLogResponse
import com.threedollar.network.data.screen.SDCursorResponse
import com.threedollar.network.data.screen.SDCustomActionResponse
import com.threedollar.network.data.screen.SDImageResponse
import com.threedollar.network.data.screen.SDImageStyleResponse
import com.threedollar.network.data.screen.SDImpressionLogResponse
import com.threedollar.network.data.screen.SDLinkResponse
import com.threedollar.network.data.screen.SDLocationResponse
import com.threedollar.network.data.screen.SDPageViewLogResponse
import com.threedollar.network.data.screen.SDSurfaceStyleResponse
import com.threedollar.network.data.screen.SDTextResponse
import com.threedollar.network.data.screen.StoreActionBarResponse
import com.threedollar.network.data.screen.StoreScreenResponse
import com.threedollar.network.data.screen.StoreSectionAdditionalInfosResponse
import com.threedollar.network.data.screen.StoreSectionResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeBottomSheetScreenMapperTest {

    @Test
    fun homeListSectionMapper_mapsServerShapeAliasesUsedByHomeSectionListApi() {
        val response = Gson().fromJson(
            """
            {
              "cards": [
                {
                  "type": "BASIC",
                  "cardId": "S:100186",
                  "header": {
                    "title": { "content": "강남역 붕어빵", "fontColor": "#0F0F0F" }
                  },
                  "metadata": {
                    "primary": [{ "text": { "content": "붕어빵" } }],
                    "secondary": [{ "text": { "content": "영업 중" } }]
                  },
                  "bodies": [
                    {
                      "text": {
                        "text": "<span style=\"font-size:12px; color:#5A5A5A\">따뜻한 리뷰</span>",
                        "isHtml": true,
                        "fontColor": "#5A5A5A"
                      },
                      "style": { "backgroundColor": "#F4F4F4" }
                    }
                  ],
                  "marker": {
                    "focused": { "text": { "content": "영업중" } },
                    "unfocused": { "text": { "content": "" } },
                    "location": { "lat": 37.1, "lng": 127.2 },
                    "link": { "type": "APP_SCHEME", "url": "/storePreview?storeId=100186" }
                  },
                  "link": { "type": "APP_SCHEME", "url": "/storePreview?storeId=100186" }
                }
              ]
            }
            """.trimIndent(),
            HomeListSectionResponse::class.java,
        )

        val model = response.asModel()
        val card = model.cards.single() as HomeListCardModel.BasicCard

        assertEquals("강남역 붕어빵", card.header.title?.text)
        assertEquals("붕어빵", card.metadata.primary.single().text.text)
        assertEquals("<span style=\"font-size:12px; color:#5A5A5A\">따뜻한 리뷰</span>", card.bodies.single().text)
        assertEquals(true, card.bodies.single().isHtml)
        assertEquals("#5A5A5A", card.bodies.single().fontColor)
        assertEquals(37.1, card.marker.location.latitude, 0.0)
        assertEquals(127.2, card.marker.location.longitude, 0.0)
        assertEquals("/storePreview?storeId=100186", card.link?.link)
    }

    @Test
    fun homeListSectionMapper_mapsBasicCardAndIgnoresUnsupportedTypes() {
        val response = HomeListSectionResponse(
            cards = listOf(
                HomeListCardResponse(
                    type = "BASIC_CARD",
                    cardId = "S:100186",
                    header = HomeListCardHeaderResponse(
                        title = SDTextResponse.fromText(text = "강남역 0번 출구 앞 붕어빵", fontColor = "#0F0F0F"),
                        badge = SDImageResponse(
                            url = "https://example.com/new.png",
                            style = SDImageStyleResponse(width = 14.0, height = 14.0),
                        ),
                    ),
                    metadata = HomeListCardMetadataResponse(
                        primary = listOf(chip("붕어빵"), chip("4.6 (23)")),
                        secondary = listOf(chip("영업 중"), chip("1km +"), chip("최근 방문 5명")),
                        separator = SDImageResponse(url = "https://example.com/dot.png"),
                    ),
                    images = listOf(SDImageResponse(url = "https://example.com/store.png")),
                    bodies = listOf(SDTextResponse.fromText("따뜻한 리뷰")),
                    marker = HomeListMarkerResponse(
                        focused = chip("영업중"),
                        unfocused = chip(""),
                        location = SDLocationResponse(latitude = 37.1, longitude = 127.2),
                        link = SDLinkResponse(type = "APP_SCHEME", link = "/storePreview?storeId=100186"),
                        clickLog = clickLog("marker", "store"),
                    ),
                    link = SDLinkResponse(type = "APP_SCHEME", link = "/storePreview?storeId=100186"),
                    style = SDSurfaceStyleResponse(backgroundColor = "#FFFFFF"),
                    clickLog = clickLog("card", "store"),
                    impressionLog = SDImpressionLogResponse(
                        eventType = "IMPRESSION",
                        screenName = "home",
                        objectType = "card",
                        objectId = "store",
                        extraParameters = mapOf("STORE_ID" to JsonPrimitive(100186)),
                    ),
                ),
                HomeListCardResponse(type = "UNKNOWN_CARD", cardId = "ignored"),
            ),
            cursor = SDCursorResponse(nextCursor = "next", hasMore = true),
        )

        val model = response.asModel()
        val card = model.cards.single() as HomeListCardModel.BasicCard

        assertEquals("next", model.cursor?.nextCursor)
        assertEquals(true, model.cursor?.hasMore)
        assertEquals("S:100186", card.cardId)
        assertEquals("강남역 0번 출구 앞 붕어빵", card.header.title?.text)
        assertEquals(14.0, card.header.badge?.style?.width)
        assertEquals(listOf("붕어빵", "4.6 (23)"), card.metadata.primary.map { it.text.text })
        assertEquals(listOf("영업 중", "1km +", "최근 방문 5명"), card.metadata.secondary.map { it.text.text })
        assertEquals("https://example.com/store.png", card.images.single().url)
        assertEquals("따뜻한 리뷰", card.bodies.single().text)
        assertEquals(37.1, card.marker.location.latitude, 0.0)
        assertEquals(127.2, card.marker.location.longitude, 0.0)
        assertEquals("/storePreview?storeId=100186", card.marker.link?.link)
        assertEquals("store", card.clickLog?.objectId)
        assertEquals("IMPRESSION", card.impressionLog?.eventType)
        assertEquals(100186, (card.impressionLog?.extraParameters?.get("STORE_ID") as SDClickLogValue.IntValue).value)
    }

    @Test
    fun homeListSectionMapper_mapsAdMobTypeAlias() {
        val response = HomeListSectionResponse(
            cards = listOf(
                HomeListCardResponse(
                    type = "AD_MOB",
                    cardId = "ad-1",
                    clickLog = clickLog("card", "ad"),
                    impressionLog = SDImpressionLogResponse(
                        eventType = "IMPRESSION",
                        screenName = "home",
                        objectType = "card",
                        objectId = "ad",
                    ),
                ),
            ),
        )

        val card = response.asModel().cards.single() as HomeListCardModel.AdMobCard

        assertEquals("AD_MOB", card.type)
        assertEquals("ad-1", card.cardId)
        assertEquals("ad", card.clickLog?.objectId)
        assertEquals("IMPRESSION", card.impressionLog?.eventType)
    }

    @Test
    fun homeListSectionMapper_preservesServerFontWeightAndMarkerText() {
        val response = Gson().fromJson(
            """
            {
              "cards": [
                {
                  "type": "BASIC_CARD",
                  "cardId": "S:100186",
                  "header": {
                    "title": { "content": "강남역 붕어빵", "fontWeight": "BOLD" }
                  },
                  "metadata": {
                    "primary": [{ "text": { "content": "붕어빵" } }],
                    "secondary": [
                      { "text": { "content": "영업종료", "fontWeight": "NORMAL" } },
                      { "text": { "content": "1km +", "fontWeight": "SEMI_BOLD" } }
                    ]
                  },
                  "marker": {
                    "focused": { "text": { "content": "영업중" } },
                    "unfocused": { "text": { "content": "" } },
                    "location": { "lat": 37.1, "lng": 127.2 }
                  }
                }
              ]
            }
            """.trimIndent(),
            HomeListSectionResponse::class.java,
        )

        val card = response.asModel().cards.single() as HomeListCardModel.BasicCard

        assertEquals("BOLD", card.header.title?.fontWeight)
        assertEquals("NORMAL", card.metadata.secondary[0].text.fontWeight)
        assertEquals("SEMI_BOLD", card.metadata.secondary[1].text.fontWeight)
        assertEquals("영업중", card.marker.focused.text.text)
        assertEquals("", card.marker.unfocused.text.text)
    }

    @Test
    fun storeScreenMapper_mapsPreviewSectionAdditionalInfosCustomActionsAndViewLogExtras() {
        val response = StoreScreenResponse(
            sections = listOf(
                StoreSectionResponse(
                    type = "PREVIEW",
                    header = HomeListCardHeaderResponse(
                        title = SDTextResponse.fromText("강남역 0번 출구 앞 붕어빵"),
                    ),
                    metadata = HomeListCardMetadataResponse(
                        primary = listOf(chip("붕어빵"), chip("4.6 (8)")),
                        secondary = listOf(chip("영업 중"), chip("1km +")),
                    ),
                    additionalInfos = StoreSectionAdditionalInfosResponse(
                        type = "STORE",
                        isSubscriber = false,
                    ),
                    actionBars = listOf(
                        StoreActionBarResponse(
                            type = "ACTION_BAR",
                            button = SDButtonResponse(
                                text = SDTextResponse.fromText(text = "방문 인증", fontColor = "#FFFFFF"),
                                imageAlignment = "END",
                                link = SDLinkResponse(type = "APP_SCHEME", link = "/visit?storeId=100186"),
                            ),
                            clickLog = clickLog("button", "visit"),
                        ),
                        StoreActionBarResponse(
                            type = "ACTION_BAR",
                            button = SDButtonResponse(
                                text = SDTextResponse.fromText("리뷰 작성"),
                                customAction = SDCustomActionResponse(
                                    actionType = "STORE_PREVIEW_SECTION_REVIEW_WRITE",
                                    extraParams = mapOf(
                                        "STORE_ID" to JsonPrimitive(100186),
                                        "STORE_TYPE" to JsonPrimitive("USER_STORE"),
                                    ),
                                ),
                            ),
                            clickLog = clickLog("button", "review"),
                        ),
                        StoreActionBarResponse(
                            type = "ACTION_BAR",
                            button = SDButtonResponse(
                                text = SDTextResponse.fromText("공유"),
                                customAction = SDCustomActionResponse(
                                    actionType = "STORE_PREVIEW_SECTION_SHARE",
                                    extraParams = mapOf(
                                        "STORE_ID" to JsonPrimitive(100186),
                                        "STORE_TYPE" to JsonPrimitive("USER_STORE"),
                                    ),
                                ),
                            ),
                            clickLog = clickLog("button", "share"),
                        ),
                        StoreActionBarResponse(
                            type = "ACTION_BAR",
                            button = SDButtonResponse(
                                text = SDTextResponse.fromText("길안내"),
                                customAction = SDCustomActionResponse(
                                    actionType = "STORE_PREVIEW_SECTION_NAVIGATION",
                                    extraParams = mapOf("STORE_NAME" to JsonPrimitive("강남역 0번 출구 앞 붕어빵")),
                                ),
                            ),
                            clickLog = clickLog("button", "navigation"),
                        ),
                    ),
                    images = listOf(SDImageResponse(url = "https://example.com/store.png")),
                    bodies = listOf(SDTextResponse.fromText("대표 리뷰")),
                    style = SDSurfaceStyleResponse(backgroundColor = "#FFFFFF"),
                ),
                StoreSectionResponse(type = "UNSUPPORTED"),
            ),
            viewLog = SDPageViewLogResponse(
                eventType = "PAGE_VIEW",
                screenName = "store_bottom_sheet",
                objectType = "screen",
                objectId = "preview",
                extraParameters = mapOf("STORE_ID" to JsonPrimitive(100186)),
            ),
        )

        val model = response.asModel()
        val preview = model.sections.single() as StoreSectionModel.Preview

        assertEquals("store_bottom_sheet", model.viewLog?.screenName)
        assertEquals(100186, (model.viewLog?.extraParameters?.get("STORE_ID") as SDClickLogValue.IntValue).value)
        assertEquals("강남역 0번 출구 앞 붕어빵", preview.header.title?.text)
        assertEquals("4.6 (8)", preview.metadata.primary[1].text.text)
        assertEquals("STORE", preview.additionalInfos.type)
        assertEquals(false, preview.additionalInfos.isSubscriber)
        assertTrue(preview.topActionBars.isEmpty())
        assertEquals("END", preview.actionBars[0].button.imageAlignment)
        assertEquals("/visit?storeId=100186", preview.actionBars[0].button.link?.link)
        assertNull(preview.actionBars[0].button.customAction)
        assertEquals("STORE_PREVIEW_SECTION_REVIEW_WRITE", preview.actionBars[1].button.customAction?.actionType)
        assertEquals("STORE_PREVIEW_SECTION_SHARE", preview.actionBars[2].button.customAction?.actionType)
        assertEquals(100186, (preview.actionBars[2].button.customAction?.extraParams?.get("STORE_ID") as SDClickLogValue.IntValue).value)
        assertEquals("USER_STORE", (preview.actionBars[2].button.customAction?.extraParams?.get("STORE_TYPE") as SDClickLogValue.StringValue).value)
        assertEquals("STORE_PREVIEW_SECTION_NAVIGATION", preview.actionBars[3].button.customAction?.actionType)
        assertEquals("https://example.com/store.png", preview.images.single().url)
        assertEquals("대표 리뷰", preview.bodies.single().text)
        assertTrue(preview.actionBars.all { it.type == "ACTION_BAR" })
    }

    @Test
    fun storeScreenMapper_mapsMissingPreviewAdditionalInfosToDefault() {
        val response = StoreScreenResponse(
            sections = listOf(StoreSectionResponse(type = "PREVIEW")),
        )

        val model = response.asModel()
        val preview = model.sections.single() as StoreSectionModel.Preview

        assertEquals("EMPTY", preview.additionalInfos.type)
        assertEquals(false, preview.additionalInfos.isSubscriber)
    }

    private fun chip(text: String): SDChipResponse = SDChipResponse(
        text = SDTextResponse.fromText(text),
    )

    private fun clickLog(objectType: String, objectId: String): SDClickLogResponse = SDClickLogResponse(
        eventType = "CLICK",
        screenName = "home",
        objectType = objectType,
        objectId = objectId,
        extraParameters = emptyMap(),
    )
}
