package com.threedollar.common.sdui.model.element

data class SDChipModel(
    val image: SDImageModel?,
    val text: SDTextModel?,
    val imageAlignment: SDImageAlignment? = null,
    val additionalText: SDTextModel? = null,
    val contentSpacing: Float? = null,
    val style: SDSurfaceStyleModel? = null
)
