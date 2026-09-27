package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

/**
 * `GET /api/v5/store/{storeId}` 응답 중 방문 인증 화면이 쓰는 값만 받는다. 제보·사장님 가게 모두 같은 형태다.
 */
data class StoreV5Response(
    @SerializedName("storeId")
    val storeId: String? = null,
    @SerializedName("storeType")
    val storeType: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("categories")
    val categories: List<Category>? = null,
)
