package com.login.domain.data

data class AccessCheckModel(
    val ok: Boolean,
    val message: String?,
    val resultCode: String?
)