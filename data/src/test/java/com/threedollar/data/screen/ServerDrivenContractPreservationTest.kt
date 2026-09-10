package com.threedollar.data.screen

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeListSectionResponse
import com.threedollar.network.data.screen.StoreContributorScreenResponse
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ServerDrivenContractPreservationTest {
    private val gson = Gson()

    @Test
    fun homeResponsesPreserveConfigurationBoundsReferencesBodiesAndNullableMarker() {
        val filter = gson.fromJson(
            """{"configuration":{"initialMapZoomLevel":13.3},"sections":[{"type":"HOME_FILTER","bars":[{"type":"CATEGORY_BAR","categoriesFilter":{"imageAlignment":"END","contentSpacing":4,"text":{"text":"category"}}},{"type":"ACTION_BAR","button":{"text":{"text":"action"},"imageAlignment":"END","customAction":{"actionType":"OPEN","extraParams":{"source":"home"}},"clickLog":{"eventType":"CLICK","screenName":"home","objectType":"button","objectId":"action"}}}]}],"viewLog":{"eventType":"PAGE_VIEW","screenName":"home","extraParameters":{"preset":"DEFAULT"}}}""",
            HomeFilterScreenResponse::class.java,
        )
        val list = gson.fromJson(
            """
            {
              "cards":[{
                "type":"BASIC_CARD","cardId":"S:test-store","header":{},"metadata":{},"images":[],
                "bodies":[{"text":{"text":"test body","isHtml":false},"style":{"backgroundColor":"#F4F4F4"}}],
                "marker":null,"refs":[{"type":"STORE","storeId":"test-store","storeType":"USER_STORE"}]
              }],
              "cursor":{"hasMore":false},
              "focusBounds":{"southWest":{"latitude":33.43,"longitude":126.90},"northEast":{"latitude":33.45,"longitude":126.92}}
            }
            """.trimIndent(),
            HomeListSectionResponse::class.java,
        )

        val filterJson = JsonParser.parseString(gson.toJson(filter)).asJsonObject
        val listJson = JsonParser.parseString(gson.toJson(list)).asJsonObject
        assertNotNull("home configuration must survive DTO parsing", filterJson["configuration"])
        assertNotNull("home viewLog eventType must survive DTO parsing", filterJson["viewLog"].asJsonObject["eventType"])
        assertNotNull("home viewLog extras must survive DTO parsing", filterJson["viewLog"].asJsonObject["extraParameters"])
        assertNotNull("home list focusBounds must survive DTO parsing", listJson["focusBounds"])
        assertNotNull("home card references must survive DTO parsing", listJson["cards"].asJsonArray[0].asJsonObject["refs"])
        assertNotNull("home body style must survive DTO parsing", listJson["cards"].asJsonArray[0].asJsonObject["bodies"].asJsonArray[0].asJsonObject["style"])
        assertEquals(13.3, filterJson["configuration"].asJsonObject["initialMapZoomLevel"].asDouble, 0.0)
        assertEquals("PAGE_VIEW", filterJson["viewLog"].asJsonObject["eventType"].asString)
        assertEquals("DEFAULT", filterJson["viewLog"].asJsonObject["extraParameters"].asJsonObject["preset"].asString)
        assertEquals(33.43, listJson["focusBounds"].asJsonObject["southWest"].asJsonObject["latitude"].asDouble, 0.0)
        assertEquals("STORE", listJson["cards"].asJsonArray[0].asJsonObject["refs"].asJsonArray[0].asJsonObject["type"].asString)
        assertEquals("#F4F4F4", listJson["cards"].asJsonArray[0].asJsonObject["bodies"].asJsonArray[0].asJsonObject["style"].asJsonObject["backgroundColor"].asString)
        assertEquals(1, list.asModel().cards.size)

        val filterModelJson = JsonParser.parseString(gson.toJson(filter.asModel())).asJsonObject
        val listModelJson = JsonParser.parseString(gson.toJson(list.asModel())).asJsonObject
        assertEquals(13.3, filterModelJson["configuration"].asJsonObject["initialMapZoomLevel"].asDouble, 0.0)
        assertEquals("DEFAULT", filterModelJson["viewLog"].asJsonObject["extraParameters"].asJsonObject["preset"].asJsonObject["value"].asString)
        assertEquals("END", filterModelJson["sections"].asJsonArray[0].asJsonObject["bars"].asJsonArray[0].asJsonObject["categoriesFilter"].asJsonObject["imageAlignment"].asString)
        assertEquals(4.0, filterModelJson["sections"].asJsonArray[0].asJsonObject["bars"].asJsonArray[0].asJsonObject["categoriesFilter"].asJsonObject["contentSpacing"].asDouble, 0.0)
        assertEquals("OPEN", filterModelJson["sections"].asJsonArray[0].asJsonObject["bars"].asJsonArray[1].asJsonObject["button"].asJsonObject["customAction"].asJsonObject["actionType"].asString)
        assertEquals(33.43, listModelJson["focusBounds"].asJsonObject["southWest"].asJsonObject["latitude"].asDouble, 0.0)
        assertEquals("STORE", listModelJson["cards"].asJsonArray[0].asJsonObject["refs"].asJsonArray[0].asJsonObject["type"].asString)
        assertEquals("#F4F4F4", listModelJson["cards"].asJsonArray[0].asJsonObject["bodies"].asJsonArray[0].asJsonObject["style"].asJsonObject["backgroundColor"].asString)
    }

    @Test
    fun contributorResponsePreservesOptionalPrimitivesLogsAndCalloutDiscriminator() {
        val response = gson.fromJson(
            """
            {
              "sections":[
                {"type":"SCREEN_HEADER","header":{"title":{"text":"title"},"subTitle":{"text":"subtitle"},"trailingAction":{"text":{"text":"more"}}}},
                {"type":"HISTORIES","cards":[{"type":"CALLOUT_CARD","cardId":"callout","title":{"text":"notice"},"description":null,"clickLog":{"eventType":"CLICK","screenName":"contributors","objectType":"card","objectId":"callout"}},{"type":"HISTORY_CARD","cardId":"history","title":{"text":"history"},"subTitles":[],"subTitleChip":{"text":{"text":"chip"},"imageAlignment":"END","contentSpacing":6}}]},
                {"type":"ACTION","actionBar":{"button":{"text":{"text":"edit"},"imageAlignment":"END","customAction":{"actionType":"EDIT","extraParams":{"STORE_ID":"test-store"}},"clickLog":{"eventType":"CLICK","screenName":"contributors","objectType":"button","objectId":"edit"}},"clickLog":{"eventType":"CLICK","screenName":"contributors","objectType":"action_bar","objectId":"edit_bar"}}}
              ],
              "viewLog":{"eventType":"PAGE_VIEW","screenName":"store_contributors","extraParameters":{"store_id":"test-store"}}
            }
            """.trimIndent(),
            StoreContributorScreenResponse::class.java,
        )

        val json = JsonParser.parseString(gson.toJson(response)).asJsonObject
        assertNotNull("header subtitle must survive DTO parsing", json["sections"].asJsonArray[0].asJsonObject["header"].asJsonObject["subTitle"])
        assertNotNull("button alignment must survive DTO parsing", json["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["button"].asJsonObject["imageAlignment"])
        assertNotNull("action bar log must survive DTO parsing", json["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["clickLog"])
        assertNotNull("contributor viewLog must survive DTO parsing", json["viewLog"])
        assertEquals("subtitle", json["sections"].asJsonArray[0].asJsonObject["header"].asJsonObject["subTitle"].asJsonObject["text"].asString)
        assertEquals("END", json["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["button"].asJsonObject["imageAlignment"].asString)
        assertEquals("edit_bar", json["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["clickLog"].asJsonObject["objectId"].asString)
        assertEquals("test-store", json["viewLog"].asJsonObject["extraParameters"].asJsonObject["store_id"].asString)

        val modelJson = JsonParser.parseString(gson.toJson(response.asModel())).asJsonObject
        assertEquals("CALLOUT_CARD", modelJson["sections"].asJsonArray[1].asJsonObject["cards"].asJsonArray[0].asJsonObject["type"].asString)
        assertNotNull(modelJson["viewLog"])
        assertEquals("subtitle", modelJson["sections"].asJsonArray[0].asJsonObject["header"].asJsonObject["subTitle"].asJsonObject["text"].asString)
        assertEquals("END", modelJson["sections"].asJsonArray[1].asJsonObject["cards"].asJsonArray[1].asJsonObject["subTitleChip"].asJsonObject["imageAlignment"].asString)
        assertEquals("EDIT", modelJson["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["button"].asJsonObject["customAction"].asJsonObject["actionType"].asString)
        assertEquals("edit_bar", modelJson["sections"].asJsonArray[2].asJsonObject["actionBar"].asJsonObject["clickLog"].asJsonObject["objectId"].asString)
    }

    @Test
    fun detailMapperPreservesDeploymentSectionShapesAndNormalizesNegativeMargin() {
        val response = gson.fromJson(
            """
            {
              "sections":[
                {"type":"CALLOUT","content":{"image":{"url":"server-icon","style":{"width":21,"height":21}},"text":{"text":"verified","isHtml":false},"style":{"backgroundColor":"#232323"}}},
                {"type":"MARGIN","height":-8},
                {"type":"EDIT","map":{"location":{"latitude":37.1,"longitude":127.2},"footerLeft":{"type":"ACTION_BAR","button":{"text":{"text":"address"}}},"footerRight":{"type":"ACTION_BAR","button":{"image":{"url":"zoom"}}}},"actionBars":[]},
                {"type":"CTA","content":{"title":{"text":"cta","isHtml":false}}}
              ],
              "viewLog":{"eventType":"PAGE_VIEW","screenName":"store_detail","extraParameters":{"store_id":"test-store"}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val model = requireNotNull(response.asStoreDetailModelOrNull())
        val json = JsonParser.parseString(gson.toJson(model)).asJsonObject
        assertEquals(listOf("CALLOUT", "MARGIN", "EDIT", "CTA"), model.sections.map { it.type })
        assertEquals("verified", json["sections"].asJsonArray[0].asJsonObject["content"].asJsonObject["text"].asJsonObject["text"].asString)
        assertEquals(0, json["sections"].asJsonArray[1].asJsonObject["height"].asInt)
        assertEquals(37.1, json["sections"].asJsonArray[2].asJsonObject["map"].asJsonObject["location"].asJsonObject["latitude"].asDouble, 0.0)
    }
}
