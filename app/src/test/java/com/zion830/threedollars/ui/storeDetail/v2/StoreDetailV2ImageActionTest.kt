package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.StoreDetailImageCardModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2ImageActionTest {

    @Test
    fun `live IMAGE_ID resolves the matching server card index`() {
        val screen = StoreDetailScreenModel(
            sections = listOf(
                StoreDetailSectionModel.Image(
                    type = "IMAGE",
                    header = com.threedollar.common.serverdriven.model.SDHeaderModel(
                        title = SDTextModel("가게 사진", false),
                    ),
                    cards = listOf(imageCard("I:531"), imageCard("I:530")),
                )
            ),
            viewLog = SDViewLogModel(screenName = "store_detail"),
        )
        val action = SDCustomActionModel(
            actionType = "STORE_IMAGE_SECTION_IMAGE_ENLARGE",
            extraParams = mapOf(
                "IMAGE_ID" to SDClickLogValue.StringValue("530"),
                "IMAGE_URL" to SDClickLogValue.StringValue("https://example.com/530.jpg"),
            ),
        )

        assertEquals(1, screen.imageIndexFor(action))
    }

    private fun imageCard(id: String) = StoreDetailImageCardModel(
        cardId = id,
        image = SDImageModel(url = "https://example.com/$id.jpg"),
        title = null,
        subTitle = null,
        link = null,
        customAction = null,
        style = SDSurfaceStyleModel(backgroundColor = "#FFFFFF"),
        clickLog = null,
    )
}
