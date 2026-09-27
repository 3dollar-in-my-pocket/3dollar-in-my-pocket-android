package com.zion830.threedollars.ui.storeDetail.user

import com.zion830.threedollars.ui.storeDetail.user.model.StoreCertificationProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreCertificationProgressTest {

    // TH-1375
    @Test
    fun `TH1375_300m보다_멀면_진행막대는_절반에_고정된다`() {
        // Given
        val farDistances = listOf(301f, 6504f)

        // When
        val percents = farDistances.map(StoreCertificationProgress::percent)

        // Then
        assertEquals(listOf(50, 50), percents)
    }

    // TH-1375
    @Test
    fun `TH1375_300m안에서는_가까울수록_진행막대가_찬다`() {
        // Given
        val distances = listOf(300f, 150f, 0f)

        // When
        val percents = distances.map(StoreCertificationProgress::percent)

        // Then
        assertEquals(listOf(0, 50, 100), percents)
    }
}
