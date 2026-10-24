package com.zion830.threedollars.ui.home.ui.compose

import kotlin.math.abs

internal enum class HomeSheetValue {
    FullList,
    Collapsed,
    Minimized,
}

internal data class HomeSheetAnchors(
    val fullListOffset: Float,
    val collapsedOffset: Float,
    val minimizedOffset: Float = collapsedOffset,
) {
    fun clamp(offset: Float): Float = offset.coerceIn(fullListOffset, minimizedOffset)

    fun offsetOf(value: HomeSheetValue): Float {
        return when (value) {
            HomeSheetValue.FullList -> fullListOffset
            HomeSheetValue.Collapsed -> collapsedOffset
            HomeSheetValue.Minimized -> minimizedOffset
        }
    }

    fun isAtAnchor(offset: Float): Boolean {
        return HomeSheetValue.entries.any { abs(offsetOf(it) - offset) <= ANCHOR_TOLERANCE_PX }
    }

    fun nearestValue(offset: Float): HomeSheetValue {
        return HomeSheetValue.entries.minBy { abs(offsetOf(it) - offset) }
    }

    fun nextValueAbove(offset: Float): HomeSheetValue {
        return HomeSheetValue.entries.lastOrNull { offsetOf(it) < offset - ANCHOR_TOLERANCE_PX }
            ?: HomeSheetValue.FullList
    }

    fun nextValueBelow(offset: Float): HomeSheetValue {
        return HomeSheetValue.entries.firstOrNull { offsetOf(it) > offset + ANCHOR_TOLERANCE_PX }
            ?: HomeSheetValue.Minimized
    }

    private companion object {
        const val ANCHOR_TOLERANCE_PX = 1f
    }
}

internal object HomeSheetStateCalculator {
    private const val FlingThresholdPxPerSecond = 1_200f

    val initialValue: HomeSheetValue = HomeSheetValue.Collapsed

    fun anchors(
        containerHeightPx: Int,
        fullListTopPx: Int,
        collapsedPeekHeightPx: Int,
        minimizedPeekHeightPx: Int = collapsedPeekHeightPx,
    ): HomeSheetAnchors {
        val collapsedOffset = (containerHeightPx - collapsedPeekHeightPx)
            .coerceAtLeast(0)
            .toFloat()
        val fullListOffset = fullListTopPx
            .coerceAtLeast(0)
            .coerceAtMost(collapsedOffset.toInt())
            .toFloat()
        val minimizedOffset = (containerHeightPx - minimizedPeekHeightPx)
            .coerceAtLeast(collapsedOffset.toInt())
            .toFloat()
        return HomeSheetAnchors(
            fullListOffset = fullListOffset,
            collapsedOffset = collapsedOffset,
            minimizedOffset = minimizedOffset,
        )
    }

    fun settleValue(
        currentOffset: Float,
        anchors: HomeSheetAnchors,
        velocityY: Float = 0f,
    ): HomeSheetValue {
        return when {
            velocityY <= -FlingThresholdPxPerSecond -> anchors.nextValueAbove(currentOffset)
            velocityY >= FlingThresholdPxPerSecond -> anchors.nextValueBelow(currentOffset)
            else -> anchors.nearestValue(currentOffset)
        }
    }

    fun settleValueAfterDrag(
        currentOffset: Float,
        anchors: HomeSheetAnchors,
        totalDragY: Float,
        dragThresholdPx: Float,
    ): HomeSheetValue {
        return when {
            totalDragY <= -dragThresholdPx -> anchors.nextValueAbove(currentOffset)
            totalDragY >= dragThresholdPx -> anchors.nextValueBelow(currentOffset)
            else -> anchors.nearestValue(currentOffset)
        }
    }

    fun fullListProgress(
        currentOffset: Float,
        anchors: HomeSheetAnchors,
    ): Float {
        val range = anchors.collapsedOffset - anchors.fullListOffset
        if (range <= 0f) return 0f

        return ((anchors.collapsedOffset - currentOffset) / range).coerceIn(0f, 1f)
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
