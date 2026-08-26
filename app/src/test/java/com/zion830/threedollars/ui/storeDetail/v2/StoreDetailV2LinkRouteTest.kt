package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDLinkModel
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2LinkRouteTest {

    @Test
    fun `known store routes use their existing destinations`() {
        assertEquals(StoreDetailV2LinkRoute.Visit, appLink("/visit?storeId=1").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Contributors, appLink("/contributors").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Reviews, appLink("/review?id=9").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Reviews, appLink("/reviews").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Dynamic, appLink("/store?storeId=2").storeDetailV2Route())
    }

    @Test
    fun `unknown app routes never fall through to dynamic link home`() {
        assertEquals(StoreDetailV2LinkRoute.Unsupported, appLink("/coupons").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Unsupported, appLink("/post?id=7").storeDetailV2Route())
        assertEquals(StoreDetailV2LinkRoute.Unsupported, appLink("/schedule").storeDetailV2Route())
    }

    @Test
    fun `web links stay external and malformed links are unsupported`() {
        assertEquals(
            StoreDetailV2LinkRoute.External,
            SDLinkModel(type = "WEB", link = "https://example.com").storeDetailV2Route(),
        )
        assertEquals(StoreDetailV2LinkRoute.Unsupported, appLink("").storeDetailV2Route())
    }

    private fun appLink(link: String) = SDLinkModel(type = "APP_SCHEME", link = link)
}
