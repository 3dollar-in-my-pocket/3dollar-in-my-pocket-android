package com.zion830.threedollars.repository.model.v2.response.store

data class NearExistResponse(
    val nearExist: NearExist,
    val message: String,
    val resultCode: String
)

data class NearExist(
    val isExists: Boolean
)