package com.zion830.threedollars.ui.storeDetail.user.ui.compose

import com.threedollar.domain.home.data.store.ReasonModel
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItem
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailDisplayItemEffectKeyTest {

    @Test
    fun displayEffectKey_ignoresVisitSubmissionState() {
        val item = StoreDetailDisplayItem.VisitInducement(
            storeId = 120024,
            trigger = null,
        )

        assertEquals(
            item.displayEffectKey(),
            item.copy(isSubmitting = true).displayEffectKey(),
        )
    }

    @Test
    fun displayEffectKey_ignoresDisappearanceTransientState() {
        val item = StoreDetailDisplayItem.DisappearanceInquiry(
            storeId = 120023,
            trigger = null,
        )
        val reason = ReasonModel(
            description = "없어진 가게예요",
            type = "NOSTORE",
        )

        assertEquals(
            item.displayEffectKey(),
            item.copy(
                reasons = listOf(reason),
                selectedReason = reason,
                isReasonLoading = false,
                isSubmitting = true,
            ).displayEffectKey(),
        )
    }
}
