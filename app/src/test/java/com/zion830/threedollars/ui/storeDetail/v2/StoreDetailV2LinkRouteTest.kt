package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.SDHeaderModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
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
        assertEquals(
            StoreDetailV2LinkRoute.Contributors,
            appLink("/store-contributors?storeId=120024").storeDetailV2Route(),
        )
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

    @Test
    fun `live tab fragments map to their actual section targets`() {
        assertEquals("PREVIEW", actionWithLink("/stores/120024#home").targetSectionTypeOrNull())
        assertEquals("INFO", actionWithLink("/stores/120024#info").targetSectionTypeOrNull())
        assertEquals("IMAGE", actionWithLink("/stores/120024#images").targetSectionTypeOrNull())
        assertEquals("REVIEW", actionWithLink("/stores/120024#reviews").targetSectionTypeOrNull())
    }

    @Test
    fun `generic info tab targets the concrete server info variant`() {
        val sections = listOf(
            StoreDetailSectionModel.InfoV1(
                type = "INFO_V1",
                header = SDHeaderModel(title = SDTextModel("가게 정보", false)),
                informationCard = null,
                menuCard = null,
            ),
            StoreDetailSectionModel.Cta(
                type = "CTA",
                content = com.threedollar.common.serverdriven.model.StoreDetailContentModel(
                    title = SDTextModel("CTA", false),
                ),
            ),
        )

        assertEquals(0, sections.indexOfStoreDetailTarget("INFO"))
    }

    private fun appLink(link: String) = SDLinkModel(type = "APP_SCHEME", link = link)

    private fun actionWithLink(link: String) = StoreActionBarModel(
        type = "ACTION_BAR",
        button = SDButtonModel(
            text = SDTextModel(text = "tab", isHtml = false),
            link = appLink(link),
        ),
    )
}
