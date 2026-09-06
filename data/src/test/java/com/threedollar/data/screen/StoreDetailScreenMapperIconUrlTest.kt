package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailScreenMapperIconUrlTest {
    private val gson = Gson()

    @Test
    fun missingAppPathIsRepairedOnlyForTheSixConfirmedIcons() {
        val names = listOf("copy.png", "zoom_3x.png", "deletion.png", "Edit_fill.png", "heart_fill.png", "heart_line.png")
        val urls = names.map { "https://storage.threedollars.co.kr/$it" }
        val response = responseWithIcons(urls)
        val original = gson.toJson(response)

        val buttons = mapButtons(response)

        assertEquals(names.map { "https://storage.threedollars.co.kr/app/$it" }, buttons.map { it.image?.url })
        buttons.forEach { button ->
            assertEquals(16.0, button.image?.style?.width)
            assertEquals(20.0, button.image?.style?.height)
            assertEquals("icon", button.text.text)
            assertEquals("#FFFFFF", button.style?.backgroundColor)
            assertEquals("icon", button.clickLog?.objectId)
        }
        assertEquals(original, gson.toJson(response))
    }

    @Test
    fun validPathsOtherHostsAndUnknownFilesStayUnchanged() {
        val urls = listOf(
            "https://storage.threedollars.co.kr/app/copy.png",
            "https://storage.threedollars.co.kr/app/heart_fill.png?version=2#icon",
            "https://storage.threedollars.co.kr/app/share_4x.png",
            "https://storage.threedollars.co.kr/unknown.png",
            "https://storage.threedollars.co.kr/photos/copy.png",
            "https://storage.threedollars.co.kr/copy.png/extra",
            "https://storage.threedollars.co.kr/edit_fill.png",
            "https://storage.dev.threedollars.co.kr/copy.png",
            "https://example.com/copy.png",
            "https://storage.threedollars.co.kr.example.com/copy.png",
            "https://storage.threedollars.co.kr@other.example/copy.png",
            "https://storage.threedollars.co.kr:443/copy.png",
            "http://storage.threedollars.co.kr/copy.png",
            "https://storage.threedollars.co.kr/%63opy.png",
        )

        assertEquals(urls, mapButtons(responseWithIcons(urls)).map { it.image?.url })
    }

    @Test
    fun queryAndFragmentArePreservedAndButtonLinksAreNotRewritten() {
        val url = "https://storage.threedollars.co.kr/copy.png?version=2&value=a%2Fb#icon"
        val response = responseWithIcons(listOf(url))
        val buttonJson = response.sections!!.single().getAsJsonArray("actionBars")[0]
            .asJsonObject.getAsJsonObject("button")
        buttonJson.add("link", gson.toJsonTree(mapOf("type" to "WEB", "link" to url)))

        val button = mapButtons(response).single()

        assertEquals("https://storage.threedollars.co.kr/app/copy.png?version=2&value=a%2Fb#icon", button.image?.url)
        assertEquals(url, button.link?.link)
    }

    private fun responseWithIcons(urls: List<String>): StoreDetailScreenResponse {
        val actions = urls.joinToString(",") { url ->
            """{
              "type":"ACTION_BAR",
              "button":{
                "text":{"text":"icon","isHtml":false},
                "image":{"url":${gson.toJson(url)},"style":{"width":16,"height":20}},
                "style":{"backgroundColor":"#FFFFFF"},
                "clickLog":{"screenName":"store_detail","objectType":"button","objectId":"icon"}
              }
            }"""
        }
        return gson.fromJson(
            """{"sections":[{"type":"EDIT","actionBars":[$actions]}],"viewLog":{"screenName":"store_detail"}}""",
            StoreDetailScreenResponse::class.java,
        )
    }

    private fun mapButtons(response: StoreDetailScreenResponse) =
        (requireNotNull(response.asStoreDetailModelOrNull()).sections.single() as StoreDetailSectionModel.Edit)
            .actionBars.map { it.button }
}
