package com.threedollar.domain.home.request

data class MenuModelRequest(
    val name: String,
    val count: Int? = null,
    val price: Int? = null,
    val category: String,
    val description: String? = null
)