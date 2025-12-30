package com.threedollar.domain.login.data

data class AccessCheckModel(
    val ok: Boolean,
    val message: String?,
    val resultCode: String?
)