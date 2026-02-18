package com.threedollar.network.sdui.model.element

data class SDImageModel(
    val url: String?,
    val style: Style?
) {
    data class Style(
        val width: Float,
        val height: Float
    )
}
