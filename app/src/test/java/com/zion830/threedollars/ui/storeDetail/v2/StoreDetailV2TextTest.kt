package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.SDTextModel
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2TextTest {

    @Test
    fun `missing contributor name never renders literal null`() {
        val serverText = SDTextModel(
            text = "<span style=\"font-weight:600\">null</span><span>님이 가게 정보를 등록했어요</span>",
            isHtml = true,
        )

        assertEquals("가게 정보를 등록했어요", serverText.withoutMissingContributorName().displayText())
    }
}
