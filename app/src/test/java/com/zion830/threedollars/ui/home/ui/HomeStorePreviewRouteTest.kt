package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.HomeListStoreReferenceModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeStorePreviewRouteTest {

    @Test
    fun `relative store link resolves store id and type`() {
        val route = HomeStorePreviewRoute.fromLink("/store?storeType=BOSS_STORE&storeId=525183")

        assertEquals(HomeStorePreviewRoute(storeId = 525183L, storeType = "BOSS_STORE"), route)
    }

    @Test
    fun `fallback id is used when link has only store type`() {
        val route = HomeStorePreviewRoute.fromLink(
            link = "/store?storeType=USER_STORE",
            fallbackStoreId = 7L,
        )

        assertEquals(HomeStorePreviewRoute(storeId = 7L, storeType = "USER_STORE"), route)
    }

    @Test
    fun `fallback id and type are used when link is missing`() {
        val route = HomeStorePreviewRoute.fromLink(
            link = null,
            fallbackStoreId = 100186L,
            fallbackStoreType = "USER_STORE",
        )

        assertEquals(HomeStorePreviewRoute(storeId = 100186L, storeType = "USER_STORE"), route)
    }

    @Test
    fun `route is null without store id`() {
        val route = HomeStorePreviewRoute.fromLink("/store?storeType=USER_STORE")

        assertNull(route)
    }

    @Test
    fun `card route keeps resolved ref when card link points elsewhere`() {
        val card = HomeListCardModel.BasicCard(
            type = "BASIC_CARD",
            cardId = "S:1",
            header = HomeListCardHeaderModel(title = SDTextModel("가게", false)),
            metadata = HomeListCardMetadataModel(),
            link = SDLinkModel("APP_SCHEME", "/store?storeId=1&storeType=USER_STORE"),
            refs = listOf(HomeListStoreReferenceModel("STORE", "9", "BOSS_STORE")),
        )

        assertEquals(
            HomeStorePreviewRoute(storeId = 9L, storeType = "BOSS_STORE"),
            HomeStorePreviewRoute.fromCard(card),
        )
    }
}
