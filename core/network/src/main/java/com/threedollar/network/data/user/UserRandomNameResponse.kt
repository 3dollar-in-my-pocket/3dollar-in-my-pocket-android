package com.threedollar.network.data.user

data class UserRandomNameResponse(
    val contents: List<UserRandomNameContentResponse> = emptyList()
)

data class UserRandomNameContentResponse(
    val name: String = ""
)
