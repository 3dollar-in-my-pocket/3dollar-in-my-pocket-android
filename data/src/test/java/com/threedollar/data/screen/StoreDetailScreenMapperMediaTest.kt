package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailScreenMapperMediaTest {

    @Test
    fun mapperMapsAdImageAndRelatedStoreCardsWithReferencesAndLogs() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {"type":"AD_MOB","cards":[{"type":"ADMOB_CARD","cardId":"ad-1","clickLog":{"screenName":"detail","objectType":"card","objectId":"ad_click","extraParameters":{}},"impressionLog":{"screenName":"detail","objectType":"card","objectId":"ad_impression","extraParameters":{}}}]},
                {"type":"IMAGE","header":{"title":{"text":"사진","isHtml":false,"fontColor":"#111111"}},"cards":[{"cardId":"image-1","image":{"url":"store-photo","style":{"width":120,"height":120}},"title":{"text":"가게 사진","isHtml":false,"fontColor":"#111111"},"subTitle":{"text":"사용자 제보","isHtml":false,"fontColor":"#555555"},"customAction":{"actionType":"STORE_IMAGE_SECTION_IMAGE_ENLARGE","extraParams":{"STORE_ID":100186,"IMAGE_INDEX":0}},"style":{"backgroundColor":"#FFFFFF"},"clickLog":{"screenName":"detail","objectType":"card","objectId":"image","extraParameters":{}}}]},
                {"type":"RELATED_STORES","header":{"title":{"text":"주변 가게","isHtml":false,"fontColor":"#111111"}},"cards":[{"type":"IMAGE_PREVIEW_CARD","cardId":"related-1","image":{"url":"related","style":{"width":160,"height":120}},"title":{"text":"호떡 가게","isHtml":false,"fontColor":"#111111"},"metricLabel":[{"text":{"text":"200m","isHtml":false,"fontColor":"#555555"}}],"contextLabel":[{"text":{"text":"호떡","isHtml":false,"fontColor":"#555555"}}],"link":{"type":"APP_SCHEME","link":"/store?storeId=200&storeType=BOSS_STORE"},"style":{"backgroundColor":"#FFFFFF"},"refs":[{"type":"STORE","storeId":"200","storeType":"BOSS_STORE"}],"clickLog":{"screenName":"detail","objectType":"card","objectId":"related","extraParameters":{}}}],"reference":[{"type":"EXPERIMENT","experimentKey":"related_type","variant":"visible"}],"impressionLog":{"screenName":"detail","objectType":"section","objectId":"related_section","extraParameters":{"STORE_ID":"100186"}}}
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val sections = requireNotNull(response.asStoreDetailModelOrNull()).sections
        val ad = sections[0] as StoreDetailSectionModel.AdMob
        val image = sections[1] as StoreDetailSectionModel.Image
        val related = sections[2] as StoreDetailSectionModel.RelatedStores

        assertEquals("ad-1", ad.cards.single().cardId)
        assertEquals("ad_impression", ad.cards.single().impressionLog.objectId)
        assertEquals("STORE_IMAGE_SECTION_IMAGE_ENLARGE", image.cards.single().customAction?.actionType)
        assertEquals("store-photo", image.cards.single().image.url)
        assertEquals("200", related.cards.single().refs.single().storeId)
        assertEquals("BOSS_STORE", related.cards.single().refs.single().storeType)
        assertEquals("related_type", related.references.single().experimentKey)
        assertEquals("related_section", related.impressionLog.objectId)
    }
}
