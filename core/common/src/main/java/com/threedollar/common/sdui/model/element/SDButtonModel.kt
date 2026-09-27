package com.threedollar.common.sdui.model.element

data class SDButtonModel(
    val text: SDTextModel? = null,
    val image: SDImageModel? = null,
    val imageAlignment: SDImageAlignment? = null,
    val link: SDLink? = null,
    val customAction: SDCustomActionModel? = null,
    val clickLog: SDLogModel? = null,
    val style: SDSurfaceStyleModel? = null
)

/**
 * 버튼 한 개를 감싼 액션 바. 로그는 버튼([SDButtonModel.clickLog]) 또는 바([clickLog]) 어느 쪽에나 올 수 있다.
 */
data class SDActionBarModel(
    val button: SDButtonModel?,
    val clickLog: SDLogModel? = null
)

/**
 * 좋아요처럼 선택 여부에 따라 다른 버튼을 보여주는 토글.
 */
data class SDToggleActionModel(
    val selected: SDButtonModel?,
    val unselected: SDButtonModel?,
    val isSelected: Boolean?
) {
    val current: SDButtonModel?
        get() = if (isSelected == true) selected else unselected
}

data class SDRatingChipModel(
    val images: List<SDImageModel>?,
    val style: SDSurfaceStyleModel? = null
)
