package com.zion830.threedollars.datasource.model.v4.user.request

data class ConnectAccountRequest(
    val token: String,
    val socialType: String,
)