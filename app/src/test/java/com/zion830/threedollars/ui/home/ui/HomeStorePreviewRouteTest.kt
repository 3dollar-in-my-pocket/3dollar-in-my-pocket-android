package com.zion830.threedollars.ui.home.ui

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
    fun `route is null without store id`() {
        val route = HomeStorePreviewRoute.fromLink("/store?storeType=USER_STORE")

        assertNull(route)
    }
}
