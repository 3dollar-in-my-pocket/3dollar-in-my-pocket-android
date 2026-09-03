package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.StoreDetailDetailRowModel
import com.threedollar.common.serverdriven.model.StoreDetailInformationRowModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailScreenMapperInfoTest {

    @Test
    fun mapperMapsInfoRowsMenusAccountsLinksAndAppearanceItems() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {
                  "type":"INFO_V1",
                  "header":{"title":{"text":"가게 정보","isHtml":false,"fontColor":"#111111"}},
                  "informationCard":{"style":{"backgroundColor":"#FFFFFF"},"rows":[
                    {"type":"CHIP_GROUP","label":{"text":"결제","isHtml":false,"fontColor":"#111111"},"chips":[{"text":{"text":"현금","isHtml":false,"fontColor":"#555555"}}]},
                    {"type":"INLINE_OPTION","label":{"text":"포장","isHtml":false,"fontColor":"#111111"},"items":[{"text":{"text":"가능","isHtml":false,"fontColor":"#555555"},"isSelected":true}]},
                    {"type":"TRAILING_TEXT","label":{"text":"위치","isHtml":false,"fontColor":"#111111"},"value":{"text":"역 1번 출구","isHtml":false,"fontColor":"#555555"}},
                    {"type":"FUTURE_ROW","label":{"text":"skip","isHtml":false,"fontColor":"#111111"}}
                  ]},
                  "menuCard":{"style":{"backgroundColor":"#FFFFFF"},"groups":[{"header":{"text":{"text":"간식","isHtml":false,"fontColor":"#111111"}},"items":[
                    {"primaryText":{"text":"붕어빵","isHtml":false,"fontColor":"#111111"},"secondaryText":{"text":"1,000원","isHtml":false,"fontColor":"#555555"}},
                    {"primaryText":{"text":"<span style=\"font-size:12px\"></span>","isHtml":true,"fontColor":"#111111"}}
                  ]}]}
                },
                {
                  "type":"INFO_V2",
                  "header":{"title":{"text":"매장 정보","isHtml":false,"fontColor":"#111111"}},
                  "imageGallery":{"images":[{"url":"menu-image","style":{"width":120,"height":120}}]},
                  "detailCard":{"style":{"backgroundColor":"#FFFFFF"},"rows":[
                    {"type":"LINK","label":{"text":"SNS","isHtml":false,"fontColor":"#111111"},"value":{"text":"instagram","isHtml":false,"fontColor":"#555555"},"link":{"type":"WEB","link":"https://instagram.com/store"}},
                    {"type":"TEXT","title":{"text":"소개","isHtml":false,"fontColor":"#111111"},"body":{"text":"매일 운영","isHtml":false,"fontColor":"#555555"}},
                    {"type":"FUTURE_DETAIL"}
                  ]},
                  "accountCards":[{"title":{"text":"계좌","isHtml":false,"fontColor":"#111111"},"account":{"text":{"text":"000-000","isHtml":false,"fontColor":"#555555"}},"copyButton":{"customAction":{"actionType":"STORE_MAP_SECTION_COPY_ADDRESS","extraParams":{"VALUE":"000-000"}},"style":{"backgroundColor":"#FFFFFF"}},"style":{"backgroundColor":"#FFFFFF"}}],
                  "menuListCard":{"style":{"backgroundColor":"#FFFFFF"},"items":[{"image":{"url":"menu","style":{"width":80,"height":80}},"primaryText":{"text":"떡볶이","isHtml":false,"fontColor":"#111111"},"secondaryText":{"text":"3,000원","isHtml":false,"fontColor":"#555555"}}]}
                },
                {
                  "type":"APPEARANCE_DAY",
                  "header":{"title":{"text":"영업일","isHtml":false,"fontColor":"#111111"},"subTitle":{"text":"이번 주","isHtml":false,"fontColor":"#555555"},"trailingAction":{"text":{"text":"더보기","isHtml":false,"fontColor":"#111111"},"link":{"type":"APP_SCHEME","link":"/schedule"},"style":{"backgroundColor":"#FFFFFF"}}},
                  "items":[{"leadingText":{"text":"월","isHtml":false,"fontColor":"#111111"},"primaryText":{"text":"강남역","isHtml":false,"fontColor":"#111111"},"secondaryText":{"text":"12:00-18:00","isHtml":false,"fontColor":"#555555"},"style":{"backgroundColor":"#FFFFFF"}}]
                }
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val sections = requireNotNull(response.asStoreDetailModelOrNull()).sections
        val infoV1 = sections[0] as StoreDetailSectionModel.InfoV1
        val infoV2 = sections[1] as StoreDetailSectionModel.InfoV2
        val appearance = sections[2] as StoreDetailSectionModel.AppearanceDay

        val informationRows = requireNotNull(infoV1.informationCard).rows
        assertEquals(3, informationRows.size)
        assertTrue(informationRows[0] is StoreDetailInformationRowModel.ChipGroup)
        assertTrue(informationRows[1] is StoreDetailInformationRowModel.InlineOption)
        assertTrue(informationRows[2] is StoreDetailInformationRowModel.TrailingText)
        val menuItems = requireNotNull(infoV1.menuCard).groups.single().items
        assertEquals(1, menuItems.size)
        assertEquals("붕어빵", menuItems.single().primaryText.text)
        assertEquals("menu-image", requireNotNull(infoV2.imageGallery).images.single().url)
        val detailRows = requireNotNull(infoV2.detailCard).rows
        assertEquals(2, detailRows.size)
        assertTrue(detailRows[0] is StoreDetailDetailRowModel.Link)
        assertTrue(detailRows[1] is StoreDetailDetailRowModel.Text)
        assertEquals("https://instagram.com/store", (detailRows[0] as StoreDetailDetailRowModel.Link).link.link)
        assertEquals("000-000", infoV2.accountCards.single().account.text.text)
        assertEquals("떡볶이", requireNotNull(infoV2.menuListCard).items.single().primaryText.text)
        assertEquals("이번 주", appearance.header.subTitle?.text)
        assertEquals("/schedule", appearance.header.trailingAction?.link?.link)
        assertEquals("12:00-18:00", appearance.items.single().secondaryText?.text)
    }
}
