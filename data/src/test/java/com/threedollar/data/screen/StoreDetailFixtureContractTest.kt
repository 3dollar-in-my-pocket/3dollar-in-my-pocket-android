package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailFixtureContractTest {
    private val gson = Gson()

    @Test
    fun deploymentReplyPreservesAuthorDateBodyAndBackground() {
        val response = gson.fromJson(resource("source-derived-review-reply.json"), StoreDetailScreenResponse::class.java)
        val model = requireNotNull(response.asStoreDetailModelOrNull())
        val reply = requireNotNull(model.sections.filterIsInstance<StoreDetailSectionModel.Review>().single().cards.single().reply)
        assertEquals("테스트 사장님", reply.header.title.displayText())
        assertEquals("2026.09.09", reply.header.subTitle.displayText())
        assertEquals("답글 계약 확인", reply.body.displayText())
        assertEquals("#F4F4F4", reply.style.backgroundColor)
    }

    @Test
    fun anonymizedProductionFixturesPreserveAllSectionsAndEditMap() {
        val expectedCounts = mapOf(
            "prod-store-120024-anonymized.json" to 14,
            "prod-store-106775-anonymized.json" to 15,
            "prod-store-121173-anonymized.json" to 15,
            "prod-store-525611-anonymized.json" to 16,
        )

        expectedCounts.forEach { (fileName, expectedCount) ->
            val root = JsonParser.parseString(resource(fileName)).asJsonObject
            val response = gson.fromJson(root["data"], StoreDetailScreenResponse::class.java)
            val model = requireNotNull(response.asStoreDetailModelOrNull())

            assertEquals(fileName, expectedCount, model.sections.size)
            val edit = model.sections.filterIsInstance<StoreDetailSectionModel.Edit>().single()
            assertEquals(fileName, 37.1234, requireNotNull(edit.map).location.latitude, 0.0)
            assertEquals(fileName, 127.5678, edit.map?.location?.longitude ?: error("missing longitude"), 0.0)
            assertTrue(fileName, edit.map?.footerRight?.button?.image?.url?.contains("zoom_3x.png") == true)
        }
    }

    @Test
    fun sourceDerivedFixtureCoversSixteenDeploymentSectionTypesAndCouponPostShapes() {
        val response = gson.fromJson(resource("source-derived-all-types.json"), StoreDetailScreenResponse::class.java)
        val model = requireNotNull(response.asStoreDetailModelOrNull())

        assertEquals(
            setOf(
                "AD_MOB", "APPEARANCE_DAY", "CALLOUT", "COUPON", "CTA", "EDIT", "IMAGE", "INFO_V1",
                "INFO_V2", "MARGIN", "POST", "PREVIEW", "RELATED_STORES", "REVIEW", "TAB", "VISIT",
            ),
            model.sections.map { it.type }.toSet(),
        )
        val coupon = model.sections.filterIsInstance<StoreDetailSectionModel.Coupon>().single()
        assertEquals(
            "issued-test",
            coupon.cards.single().trailingButton.customAction?.extraParams?.get("COUPON_ISSUED_KEY")?.anyValue,
        )
        assertEquals("post body", model.sections.filterIsInstance<StoreDetailSectionModel.Post>().single().cards.single().body.text)
    }

    private fun resource(fileName: String): String {
        val path = "sdui/store-detail/$fileName"
        return requireNotNull(javaClass.classLoader?.getResource(path)) { "Missing fixture: $path" }.readText()
    }
}
