package com.threedollar.network.data.screen

import com.google.gson.annotations.SerializedName

/** `GET /api/v1/screen/bottom-navigation` 응답. 서버가 앱 버전별로 하단 탭 목록을 내려준다(TH-1647). */
data class BottomNavigationResponse(
    @SerializedName("tabs")
    val tabs: List<BottomNavigationTabResponse>? = emptyList(),
)

data class BottomNavigationTabResponse(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("icon")
    val icon: SDImageResponse? = null,
    @SerializedName("selectedTintColor")
    val selectedTintColor: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("link")
    val link: SDLinkResponse? = null,
    @SerializedName("clickLog")
    val clickLog: SDClickLogResponse? = null,
)
