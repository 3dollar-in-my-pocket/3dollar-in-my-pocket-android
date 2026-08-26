package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Test

class StoreDetailScreenMapperDiscriminatorTest {

    @Test
    fun mapperPreservesAllKnownSectionTypesInServerOrderAndSkipsOnlyUnknownSection() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {"type":"CALLOUT","content":{"title":{"text":"callout","isHtml":false,"fontColor":"#000000"}}},
                {"type":"PREVIEW","header":{},"metadata":{"primary":[],"secondary":[],"separator":{"url":"dot","style":{"width":2,"height":2}}},"actionBars":[],"images":[],"bodies":[],"style":{"backgroundColor":"#FFFFFF"},"additionalInfos":{"type":"EMPTY"}},
                {"type":"AD_MOB","cards":[]},
                {"type":"TAB","tabs":[]},
                {"type":"MAP","location":{"latitude":37.1,"longitude":127.2},"footerRight":{"button":{"style":{"backgroundColor":"#FFFFFF"}}}},
                {"type":"EDIT","actionBars":[]},
                {"type":"COUPON","cards":[]},
                {"type":"VISIT","header":{"title":{"text":"visit","isHtml":false,"fontColor":"#000000"}},"summary":{"title":{"text":"summary","isHtml":false,"fontColor":"#000000"},"stars":{"images":[]},"rating":{"text":"0","isHtml":false,"fontColor":"#000000"},"style":{"backgroundColor":"#FFFFFF"}},"history":{"items":[],"style":{"backgroundColor":"#FFFFFF"}}},
                {"type":"POST","header":{"title":{"text":"post","isHtml":false,"fontColor":"#000000"}},"cards":[]},
                {"type":"IMAGE","header":{"title":{"text":"image","isHtml":false,"fontColor":"#000000"}},"cards":[]},
                {"type":"APPEARANCE_DAY","header":{"title":{"text":"day","isHtml":false,"fontColor":"#000000"}},"items":[]},
                {"type":"RELATED_STORES","header":{"title":{"text":"related","isHtml":false,"fontColor":"#000000"}},"cards":[],"reference":[],"impressionLog":{"screenName":"detail","objectType":"section","objectId":"related","extraParameters":{}}},
                {"type":"CTA","content":{"title":{"text":"cta","isHtml":false,"fontColor":"#000000"}}},
                {"type":"REVIEW","header":{"title":{"text":"review","isHtml":false,"fontColor":"#000000"}},"summary":{"title":{"text":"summary","isHtml":false,"fontColor":"#000000"},"stars":{"images":[]},"rating":{"text":"0","isHtml":false,"fontColor":"#000000"},"style":{"backgroundColor":"#FFFFFF"}},"cards":[]},
                {"type":"INFO_V1","header":{"title":{"text":"info1","isHtml":false,"fontColor":"#000000"}}},
                {"type":"INFO_V2","header":{"title":{"text":"info2","isHtml":false,"fontColor":"#000000"}},"accountCards":[]},
                {"type":"FUTURE_SECTION","payload":"ignored"}
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val model = response.asStoreDetailModelOrNull()

        requireNotNull(model)
        assertEquals(
            listOf(
                "CALLOUT", "PREVIEW", "AD_MOB", "TAB", "MAP", "EDIT", "COUPON", "VISIT",
                "POST", "IMAGE", "APPEARANCE_DAY", "RELATED_STORES", "CTA", "REVIEW", "INFO_V1", "INFO_V2",
            ),
            model.sections.map { it.type },
        )
        assertTrue(model.sections[0] is StoreDetailSectionModel.Callout)
        assertTrue(model.sections[1] is StoreDetailSectionModel.Preview)
        assertTrue(model.sections[2] is StoreDetailSectionModel.AdMob)
        assertTrue(model.sections[3] is StoreDetailSectionModel.Tab)
        assertTrue(model.sections[4] is StoreDetailSectionModel.Map)
        assertTrue(model.sections[5] is StoreDetailSectionModel.Edit)
        assertTrue(model.sections[6] is StoreDetailSectionModel.Coupon)
        assertTrue(model.sections[7] is StoreDetailSectionModel.Visit)
        assertTrue(model.sections[8] is StoreDetailSectionModel.Post)
        assertTrue(model.sections[9] is StoreDetailSectionModel.Image)
        assertTrue(model.sections[10] is StoreDetailSectionModel.AppearanceDay)
        assertTrue(model.sections[11] is StoreDetailSectionModel.RelatedStores)
        assertTrue(model.sections[12] is StoreDetailSectionModel.Cta)
        assertTrue(model.sections[13] is StoreDetailSectionModel.Review)
        assertTrue(model.sections[14] is StoreDetailSectionModel.InfoV1)
        assertTrue(model.sections[15] is StoreDetailSectionModel.InfoV2)
    }

    @Test
    fun mapperRejectsMissingRequiredTopLevelFields() {
        assertNull(StoreDetailScreenResponse(sections = null, viewLog = null).asStoreDetailModelOrNull())
        assertNull(StoreDetailScreenResponse(sections = emptyList(), viewLog = null).asStoreDetailModelOrNull())
    }

    @Test
    fun mapperSkipsMalformedKnownSectionWithoutDroppingValidSiblings() {
        val response = Gson().fromJson(
            """
            {
              "sections":[
                {"type":"CALLOUT"},
                {"type":"CTA","content":{"title":{"text":"valid","isHtml":false,"fontColor":"#111111"}}}
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        assertEquals(listOf("CTA"), requireNotNull(response.asStoreDetailModelOrNull()).sections.map { it.type })
    }
}
