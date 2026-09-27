package com.threedollar.common.sdui.model.element

enum class SDLinkType {
    APP_SCHEME,
    WEB
}

data class SDLink(
    val type: SDLinkType?,
    val link: String?
)
