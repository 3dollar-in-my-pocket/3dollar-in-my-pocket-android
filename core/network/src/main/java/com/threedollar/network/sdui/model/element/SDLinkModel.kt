package com.threedollar.network.sdui.model.element

enum class SDLinkType {
    APP_SCHEME,
    WEB
}

data class SDLink(
    val type: SDLinkType?,
    val link: String?
)
