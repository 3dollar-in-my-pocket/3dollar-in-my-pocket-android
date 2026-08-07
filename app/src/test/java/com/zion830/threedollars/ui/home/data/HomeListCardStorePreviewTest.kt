package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListMarkerModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListCardStorePreviewTest {

    @Test
    fun `store preview id resolves from marker link when card link is missing`() {
        val card = card(
            cardId = "home-card",
            cardLink = null,
            markerLink = "/storePreview?storeId=100186&storeType=USER_STORE",
        )

        assertEquals(100186L, card.storePreviewStoreIdOrNull())
        assertEquals("USER_STORE", card.storePreviewStoreTypeOrNull())
    }

    @Test
    fun `store preview id falls back to card id`() {
        val card = card(
            cardId = "S:100186",
            cardLink = null,
            markerLink = null,
        )

        assertEquals(100186L, card.storePreviewStoreIdOrNull())
        assertEquals("USER_STORE", card.storePreviewStoreTypeOrNull())
    }

    @Test
    fun `fallback store preview screen includes bottom sheet actions`() {
        val card = card(
            cardId = "S:100186",
            cardLink = "/store?storeType=USER_STORE&storeId=100186",
            markerLink = "/store_bottom_sheet?storeId=100186",
        )

        val screen = card.toFallbackStorePreviewScreen()
        val preview = screen?.sections?.single() as? StoreSectionModel.Preview

        assertNotNull(preview)
        assertEquals("가게", preview?.header?.title?.text)
        assertEquals("STORE", preview?.additionalInfos?.type)
        assertEquals(4, preview?.actionBars?.size)
        assertEquals("/visit?storeId=100186", preview?.actionBars?.get(0)?.button?.link?.link)
        assertEquals("STORE_PREVIEW_SECTION_REVIEW_WRITE", preview?.actionBars?.get(1)?.button?.customAction?.actionType)
        assertEquals("STORE_PREVIEW_SECTION_SHARE", preview?.actionBars?.get(2)?.button?.customAction?.actionType)
        assertEquals("STORE_PREVIEW_SECTION_NAVIGATION", preview?.actionBars?.get(3)?.button?.customAction?.actionType)
        assertEquals(100186, (preview?.actionBars?.get(1)?.button?.customAction?.extraParams?.get("STORE_ID") as SDClickLogValue.IntValue).value)
        assertEquals("USER_STORE", (preview.actionBars[1].button.customAction?.extraParams?.get("STORE_TYPE") as SDClickLogValue.StringValue).value)
        assertEquals(37.1, (preview.actionBars[3].button.customAction?.extraParams?.get("LATITUDE") as SDClickLogValue.DoubleValue).value, 0.0)
        assertEquals(127.2, (preview.actionBars[3].button.customAction?.extraParams?.get("LONGITUDE") as SDClickLogValue.DoubleValue).value, 0.0)
    }

    @Test
    fun `fallback store preview screen keeps local favorite state`() {
        val card = card(
            cardId = "S:100186",
            cardLink = "/store?storeType=USER_STORE&storeId=100186",
            markerLink = "/store_bottom_sheet?storeId=100186",
        )

        val screen = card.toFallbackStorePreviewScreen(isSubscriber = true)
        val preview = screen?.sections?.single() as? StoreSectionModel.Preview

        assertEquals(true, preview?.additionalInfos?.isSubscriber)
    }

    @Test
    fun `store preview screen applies local favorite state override`() {
        val card = card(
            cardId = "S:100186",
            cardLink = "/store?storeType=USER_STORE&storeId=100186",
            markerLink = "/store_bottom_sheet?storeId=100186",
        )
        val screen = requireNotNull(card.toFallbackStorePreviewScreen(isSubscriber = true))

        val updatedScreen = screen.withStorePreviewFavoriteOverride(isSubscriber = false)
        val preview = updatedScreen.sections.single() as? StoreSectionModel.Preview

        assertEquals(false, preview?.additionalInfos?.isSubscriber)
    }

    private fun card(
        cardId: String,
        cardLink: String?,
        markerLink: String?,
    ): HomeListCardModel.BasicCard {
        return HomeListCardModel.BasicCard(
            type = "BASIC_CARD",
            cardId = cardId,
            header = HomeListCardHeaderModel(title = SDTextModel("가게", isHtml = false)),
            metadata = HomeListCardMetadataModel(),
            marker = HomeListMarkerModel(
                focused = chip(""),
                unfocused = chip(""),
                location = SDLocationModel(latitude = 37.1, longitude = 127.2),
                link = markerLink?.let { SDLinkModel(type = "APP_SCHEME", link = it) },
            ),
            link = cardLink?.let { SDLinkModel(type = "APP_SCHEME", link = it) },
        )
    }

    private fun chip(text: String): SDChipModel {
        return SDChipModel(text = SDTextModel(text = text, isHtml = false))
    }
}
