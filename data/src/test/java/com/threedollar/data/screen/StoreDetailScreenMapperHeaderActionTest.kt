package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StoreDetailScreenMapperHeaderActionTest {

    @Test
    fun mapperMapsHeaderActionSectionsAndPreservesDynamicActionParameterTypes() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {
                  "type":"CALLOUT",
                  "content":{
                    "title":{"text":"공식 인증 가게","isHtml":false,"fontColor":"#101010"},
                    "subTitle":{"text":"안심하고 방문하세요","isHtml":false,"fontColor":"#666666"},
                    "footerLeftButton":{"text":{"text":"자세히","isHtml":false,"fontColor":"#101010"},"link":{"type":"WEB","link":"https://example.com"},"style":{"backgroundColor":"#FFFFFF"}}
                  }
                },
                {
                  "type":"PREVIEW",
                  "header":{"title":{"text":"붕어빵 가게","isHtml":false,"fontColor":"#101010"},"badge":{"url":"badge","style":{"width":16,"height":16}}},
                  "metadata":{"primary":[{"text":{"text":"붕어빵","isHtml":false,"fontColor":"#444444"},"imageAlignment":"START","contentSpacing":4}],"secondary":[],"separator":{"url":"dot","style":{"width":2,"height":2}}},
                  "contributorActionBar":{"type":"CONTRIBUTOR","button":{"text":{"text":"제보자","isHtml":false,"fontColor":"#555555"},"link":{"type":"APP_SCHEME","link":"/contributors"},"style":{"backgroundColor":"#FFFFFF"}}},
                  "actionBars":[{
                    "type":"SHARE",
                    "button":{
                      "text":{"text":"공유","isHtml":false,"fontColor":"#101010"},
                      "customAction":{"actionType":"STORE_PREVIEW_SECTION_SHARE","extraParams":{"STRING":"value","INT":7,"LONG":855453324337299456,"DOUBLE":37.5,"BOOL":true,"NULL":null}},
                      "clickLog":{"screenName":"detail","objectType":"button","objectId":"share_inner","extraParameters":{}},
                      "style":{"backgroundColor":"#FFFFFF"}
                    },
                    "clickLog":{"screenName":"detail","objectType":"button","objectId":"share","extraParameters":{}}
                  }],
                  "images":[{"url":"store","style":{"width":120,"height":120}}],
                  "bodies":[{"text":{"text":"대표 리뷰","isHtml":false,"fontColor":"#444444"},"style":{"backgroundColor":"#F7F7F7"}}],
                  "style":{"backgroundColor":"#FFFFFF"},
                  "additionalInfos":{"type":"STORE","storeId":"100186","storeType":"USER_STORE","isSubscriber":true}
                },
                {"type":"TAB","tabs":[{"type":"INFO","button":{"text":{"text":"정보","isHtml":false,"fontColor":"#101010"},"customAction":{"actionType":"STORE_EDIT_SECTION_UPDATE","extraParams":{"TARGET_SECTION_TYPE":"INFO_V1"}},"style":{"backgroundColor":"#FFFFFF"}}}]},
                {"type":"MAP","location":{"latitude":37.1,"longitude":127.2},"footerLeft":{"type":"COPY","button":{"customAction":{"actionType":"STORE_MAP_SECTION_COPY_ADDRESS","extraParams":{"ADDRESS":"서울"}},"style":{"backgroundColor":"#FFFFFF"}}},"footerRight":{"type":"EXPAND","button":{"customAction":{"actionType":"STORE_MAP_SECTION_MAP_ENLARGE","extraParams":{}},"style":{"backgroundColor":"#FFFFFF"}}}},
                {"type":"EDIT","actionBars":[{"type":"REPORT","button":{"customAction":{"actionType":"STORE_EDIT_SECTION_REPORT","extraParams":{"STORE_ID":100186}},"style":{"backgroundColor":"#FFFFFF"}}}]},
                {"type":"CTA","content":{"title":{"text":"쿠폰함 열기","isHtml":false,"fontColor":"#101010"},"footerLeftButton":{"link":{"type":"APP_SCHEME","link":"/coupons"},"style":{"backgroundColor":"#FFFFFF"}}}}
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val sections = requireNotNull(response.asStoreDetailModelOrNull()).sections
        val callout = sections[0] as StoreDetailSectionModel.Callout
        val preview = sections[1] as StoreDetailSectionModel.Preview
        val tab = sections[2] as StoreDetailSectionModel.Tab
        val map = sections[3] as StoreDetailSectionModel.Map
        val edit = sections[4] as StoreDetailSectionModel.Edit
        val cta = sections[5] as StoreDetailSectionModel.Cta

        assertEquals("공식 인증 가게", callout.content.title.text)
        assertEquals("안심하고 방문하세요", callout.content.subTitle?.text)
        assertEquals("https://example.com", callout.content.footerLeftButton?.link?.link)
        assertEquals("붕어빵 가게", preview.header.title?.text)
        assertEquals("100186", preview.additionalInfos.storeId)
        assertEquals("USER_STORE", preview.additionalInfos.storeType)
        assertEquals(true, preview.additionalInfos.isSubscriber)
        assertEquals("/contributors", preview.contributorActionBar?.button?.link?.link)
        assertEquals("share_inner", preview.actionBars.single().button.clickLog?.objectId)
        assertEquals("대표 리뷰", preview.bodies.single().text.text)
        val params = requireNotNull(preview.actionBars.single().button.customAction).extraParams
        assertEquals("value", (params["STRING"] as SDClickLogValue.StringValue).value)
        assertEquals(7, (params["INT"] as SDClickLogValue.IntValue).value)
        assertEquals(855453324337299456L, (params["LONG"] as SDClickLogValue.LongValue).value)
        assertEquals(37.5, (params["DOUBLE"] as SDClickLogValue.DoubleValue).value, 0.0)
        assertEquals(true, (params["BOOL"] as SDClickLogValue.BoolValue).value)
        assertEquals(SDClickLogValue.Null, params["NULL"])
        assertEquals("INFO_V1", tab.tabs.single().button.customAction?.extraParams?.get("TARGET_SECTION_TYPE")?.anyValue)
        assertEquals(37.1, map.location.latitude, 0.0)
        assertEquals("서울", map.footerLeft?.button?.customAction?.extraParams?.get("ADDRESS")?.anyValue)
        assertEquals("STORE_MAP_SECTION_MAP_ENLARGE", map.footerRight.button.customAction?.actionType)
        assertEquals(100186, edit.actionBars.single().button.customAction?.extraParams?.get("STORE_ID")?.anyValue)
        assertEquals("/coupons", cta.content.footerLeftButton?.link?.link)
        assertNull(cta.content.footerLeftButton?.customAction)
    }
}
