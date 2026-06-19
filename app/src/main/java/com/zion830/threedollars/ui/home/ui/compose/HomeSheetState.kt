package com.zion830.threedollars.ui.home.ui.compose

internal enum class HomeSheetValue {
    Collapsed,
    FullList,
}

internal data class HomeSheetAnchors(
    val fullListOffset: Float,
    val collapsedOffset: Float,
) {
    fun clamp(offset: Float): Float = offset.coerceIn(fullListOffset, collapsedOffset)

    fun offsetOf(value: HomeSheetValue): Float {
        return when (value) {
            HomeSheetValue.Collapsed -> collapsedOffset
            HomeSheetValue.FullList -> fullListOffset
        }
    }
}

internal object HomeSheetStateCalculator {
    private const val FlingThresholdPxPerSecond = 1_200f

    fun anchors(
        containerHeightPx: Int,
        fullListTopPx: Int,
        collapsedPeekHeightPx: Int,
    ): HomeSheetAnchors {
        val collapsedOffset = (containerHeightPx - collapsedPeekHeightPx)
            .coerceAtLeast(0)
            .toFloat()
        val fullListOffset = fullListTopPx
            .coerceAtLeast(0)
            .coerceAtMost(collapsedOffset.toInt())
            .toFloat()
        return HomeSheetAnchors(
            fullListOffset = fullListOffset,
            collapsedOffset = collapsedOffset,
        )
    }

    fun settleValue(
        currentOffset: Float,
        anchors: HomeSheetAnchors,
        velocityY: Float = 0f,
    ): HomeSheetValue {
        return when {
            velocityY <= -FlingThresholdPxPerSecond -> HomeSheetValue.FullList
            velocityY >= FlingThresholdPxPerSecond -> HomeSheetValue.Collapsed
            currentOffset <= (anchors.fullListOffset + anchors.collapsedOffset) / 2f -> HomeSheetValue.FullList
            else -> HomeSheetValue.Collapsed
        }
    }

    fun visibleHeight(
        containerHeightPx: Int,
        currentOffset: Float,
    ): Float {
        return (containerHeightPx - currentOffset)
            .coerceAtLeast(0f)
    }

    fun previewOffset(
        containerHeightPx: Int,
        desiredVisibleHeightPx: Int,
        minimumVisibleHeightPx: Int,
    ): Float {
        val visibleHeight = desiredVisibleHeightPx
            .coerceAtLeast(minimumVisibleHeightPx)
            .coerceAtMost(containerHeightPx)
        return (containerHeightPx - visibleHeight).toFloat()
    }

    fun previewTargetOffset(
        containerHeightPx: Int,
        desiredVisibleHeightPx: Int,
        minimumVisibleHeightPx: Int,
        anchors: HomeSheetAnchors,
    ): Float {
        return previewOffset(
            containerHeightPx = containerHeightPx,
            desiredVisibleHeightPx = desiredVisibleHeightPx,
            minimumVisibleHeightPx = minimumVisibleHeightPx,
        ).coerceAtLeast(anchors.fullListOffset)
    }

    fun restoreAfterPreview(previousListValue: HomeSheetValue): HomeSheetValue {
        return previousListValue
    }
}
