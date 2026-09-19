package com.threedollar.common.serverdriven

import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import kotlin.math.floor

/**
 * 서버가 내려준 미리보기 이미지를 화면 폭에 맞춰 그리기 위한 크기 계산기.
 *
 * 이미지가 [FILL_MAX_COUNT]개 이하면 가용 폭을 균등 분할해 꽉 채우고,
 * 그보다 많으면 서버가 준 크기(없으면 기본값)를 그대로 써서 가로로 스크롤한다.
 * 모든 값의 단위는 dp다.
 */
object PreviewImageLayout {

    /** 이 개수 이하일 때만 가용 폭을 균등 분할한다. */
    const val FILL_MAX_COUNT = 3

    /**
     * 가용 폭을 [count]개로 균등 분할한 이미지 한 장의 폭.
     *
     * 분할 대상이 아니거나 폭이 남지 않으면 null을 돌려준다.
     */
    fun fillWidth(count: Int, availableWidth: Float, spacing: Float): Float? {
        if (count <= 0 || count > FILL_MAX_COUNT) return null
        if (availableWidth <= 0f || !availableWidth.isFinite()) return null

        val totalSpacing = spacing * (count - 1)
        val width = floor((availableWidth - totalSpacing) / count)

        return width.takeIf { it > 0f }
    }

    /**
     * 이미지 한 장의 표시 폭.
     *
     * 균등 분할 대상이면 분할된 폭을, 아니면 서버값(없으면 [defaultWidth])을 쓴다.
     */
    fun itemWidth(
        style: SDImageStyleModel?,
        count: Int,
        availableWidth: Float,
        spacing: Float,
        defaultWidth: Float,
    ): Float = fillWidth(count, availableWidth, spacing) ?: resolve(style?.width, defaultWidth)

    /** 이미지 한 장의 표시 높이. 서버값이 없거나 유효하지 않으면 [defaultHeight]를 쓴다. */
    fun itemHeight(style: SDImageStyleModel?, defaultHeight: Float): Float = resolve(style?.height, defaultHeight)

    /** 이미지 행의 높이. 가장 높은 이미지에 맞추고, 이미지가 없으면 0이다. */
    fun rowHeight(images: List<SDImageModel>, defaultHeight: Float): Float {
        if (images.isEmpty()) return 0f

        return images.maxOf { resolve(it.style?.height, defaultHeight) }
    }

    private fun resolve(value: Double?, default: Float): Float {
        val resolved = value?.toFloat() ?: return default

        return if (resolved > 0f) resolved else default
    }
}
