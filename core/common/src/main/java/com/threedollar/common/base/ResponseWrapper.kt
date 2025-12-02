package com.threedollar.common.base

data class ResponseWrapper<T>(
    val ok: Boolean,
    val message: String,
    val data: T?
)