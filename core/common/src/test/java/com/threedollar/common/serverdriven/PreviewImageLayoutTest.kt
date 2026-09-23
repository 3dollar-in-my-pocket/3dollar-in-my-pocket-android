package com.threedollar.common.serverdriven

import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PreviewImageLayoutTest {

    private companion object {
        const val DELTA = 0.001f
    }

    @Test
    fun fillWidth_splitsAvailableWidthEvenlyForSingleImage() {
        assertEquals(320f, PreviewImageLayout.fillWidth(count = 1, availableWidth = 320f, spacing = 6f)!!, DELTA)
    }

    @Test
    fun fillWidth_subtractsSpacingBetweenImages() {
        assertEquals(157f, PreviewImageLayout.fillWidth(count = 2, availableWidth = 320f, spacing = 6f)!!, DELTA)
        assertEquals(102f, PreviewImageLayout.fillWidth(count = 3, availableWidth = 320f, spacing = 6f)!!, DELTA)
    }

    @Test
    fun fillWidth_returnsNullWhenCountExceedsFillMax() {
        assertNull(PreviewImageLayout.fillWidth(count = 4, availableWidth = 320f, spacing = 6f))
    }

    @Test
    fun fillWidth_returnsNullWhenNothingToLayout() {
        assertNull(PreviewImageLayout.fillWidth(count = 0, availableWidth = 320f, spacing = 6f))
        assertNull(PreviewImageLayout.fillWidth(count = 2, availableWidth = 0f, spacing = 6f))
        assertNull(PreviewImageLayout.fillWidth(count = 3, availableWidth = 10f, spacing = 6f))
    }

    @Test
    fun itemWidth_usesFillWidthAndIgnoresServerWidthWhenThreeOrFewer() {
        val width = PreviewImageLayout.itemWidth(
            style = SDImageStyleModel(width = 80.0, height = 80.0),
            count = 3,
            availableWidth = 320f,
            spacing = 6f,
            defaultWidth = 120f,
        )

        assertEquals(102f, width, DELTA)
    }

    @Test
    fun itemWidth_usesServerWidthWhenMoreThanThree() {
        val width = PreviewImageLayout.itemWidth(
            style = SDImageStyleModel(width = 80.0, height = 80.0),
            count = 4,
            availableWidth = 320f,
            spacing = 6f,
            defaultWidth = 120f,
        )

        assertEquals(80f, width, DELTA)
    }

    @Test
    fun itemWidth_fallsBackToDefaultWhenServerWidthMissingOrInvalid() {
        val missing = PreviewImageLayout.itemWidth(
            style = null,
            count = 4,
            availableWidth = 320f,
            spacing = 6f,
            defaultWidth = 120f,
        )
        val invalid = PreviewImageLayout.itemWidth(
            style = SDImageStyleModel(width = 0.0),
            count = 4,
            availableWidth = 320f,
            spacing = 6f,
            defaultWidth = 120f,
        )

        assertEquals(120f, missing, DELTA)
        assertEquals(120f, invalid, DELTA)
    }

    @Test
    fun itemHeight_prefersServerHeightAndFallsBackToDefault() {
        assertEquals(200f, PreviewImageLayout.itemHeight(SDImageStyleModel(height = 200.0), defaultHeight = 120f), DELTA)
        assertEquals(120f, PreviewImageLayout.itemHeight(SDImageStyleModel(height = null), defaultHeight = 120f), DELTA)
        assertEquals(120f, PreviewImageLayout.itemHeight(null, defaultHeight = 120f), DELTA)
    }

    @Test
    fun rowHeight_isZeroWhenThereAreNoImages() {
        assertEquals(0f, PreviewImageLayout.rowHeight(emptyList(), defaultHeight = 120f), DELTA)
    }

    @Test
    fun rowHeight_takesTallestImage() {
        val images = listOf(
            image(height = 100.0),
            image(height = 180.0),
            image(height = null),
        )

        assertEquals(180f, PreviewImageLayout.rowHeight(images, defaultHeight = 120f), DELTA)
    }

    @Test
    fun rowHeight_fallsBackToDefaultWhenServerHeightMissing() {
        val images = listOf(image(height = null), image(height = null))

        assertEquals(120f, PreviewImageLayout.rowHeight(images, defaultHeight = 120f), DELTA)
    }

    private fun image(height: Double?) = SDImageModel(
        url = "https://image.threedollars.co.kr/sample.png",
        style = SDImageStyleModel(width = 120.0, height = height),
    )
}
