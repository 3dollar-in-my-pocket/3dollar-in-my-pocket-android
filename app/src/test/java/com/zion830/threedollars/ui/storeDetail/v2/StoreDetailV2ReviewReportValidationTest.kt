package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.domain.home.data.store.ReasonModel
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2ReviewReportValidationTest {

    private val reasons = listOf(
        ReasonModel(description = "스팸", type = "SPAM", hasReasonDetail = false),
        ReasonModel(description = "기타", type = "ETC", hasReasonDetail = true),
    )

    @Test
    fun `report requires an explicit reason and required detail`() {
        assertEquals(false, canSubmitStoreDetailReviewReport(reasons, -1, ""))
        assertEquals(true, canSubmitStoreDetailReviewReport(reasons, 0, ""))
        assertEquals(false, canSubmitStoreDetailReviewReport(reasons, 1, "  "))
        assertEquals(true, canSubmitStoreDetailReviewReport(reasons, 1, "상세 사유"))
    }
}
