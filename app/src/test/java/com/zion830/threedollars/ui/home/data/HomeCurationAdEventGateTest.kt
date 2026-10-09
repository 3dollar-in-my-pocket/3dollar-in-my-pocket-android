package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeCurationAdEventGateTest {

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_로드전에는_로그를_기록하지_않고_로드만으로_노출을_소비하지_않는다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        assertFalse(gate.recordImpression())
        assertFalse(gate.recordClick())

        // When
        gate.onLoaded()
        val firstImpression = gate.recordImpression()

        // Then
        assertTrue(firstImpression)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_같은광고의_중복노출콜백은_한번만_기록한다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        gate.onLoaded()

        // When
        val first = gate.recordImpression()
        val duplicate = gate.recordImpression()

        // Then
        assertTrue(first)
        assertFalse(duplicate)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_자동갱신으로_새광고가_로드되면_새노출을_한번_기록한다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        gate.onLoaded()
        assertTrue(gate.recordImpression())

        // When
        gate.onLoaded()
        val refreshed = gate.recordImpression()
        val duplicate = gate.recordImpression()

        // Then
        assertTrue(refreshed)
        assertFalse(duplicate)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_서로다른_실제광고클릭은_각각_기록한다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        gate.onLoaded()

        // When
        val first = gate.recordClick()
        val second = gate.recordClick()
        val firstImpression = gate.recordImpression()
        val duplicateImpression = gate.recordImpression()

        // Then
        assertTrue(first)
        assertTrue(second)
        assertTrue(firstImpression)
        assertFalse(duplicateImpression)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_로드실패후에는_콜백을_무시하고_새로드가_성공하면_기록을_재개한다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        gate.onLoaded()

        // When
        gate.onFailed()
        val failedImpression = gate.recordImpression()
        val failedClick = gate.recordClick()
        gate.onLoaded()
        val retriedImpression = gate.recordImpression()
        val retriedClick = gate.recordClick()

        // Then
        assertFalse(failedImpression)
        assertFalse(failedClick)
        assertTrue(retriedImpression)
        assertTrue(retriedClick)
    }

    // TH-1401 TC7
    @Test
    fun `TH1401_TC7_광고뷰가_해제되면_늦은로드와_노출과_클릭콜백을_무시한다`() {
        // Given
        val gate = HomeCurationAdEventGate()
        gate.onLoaded()

        // When
        gate.dispose()
        val disposedImpression = gate.recordImpression()
        val disposedClick = gate.recordClick()
        gate.onLoaded()
        val lateImpression = gate.recordImpression()
        val lateClick = gate.recordClick()

        // Then
        assertFalse(disposedImpression)
        assertFalse(disposedClick)
        assertFalse(lateImpression)
        assertFalse(lateClick)
    }
}
