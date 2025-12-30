package com.threedollar.network.data.visit_history


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.network.data.store.Store

@JsonClass(generateAdapter = true)
data class VisitHistoryContent(
    @Json(name = "createdAt")
    val createdAt: String? = "",
    @Json(name = "dateOfVisit")
    val dateOfVisit: String? = "",
    @Json(name = "store")
    val store: Store = Store(),
    @Json(name = "type")
    val type: String? = "",
    @Json(name = "updatedAt")
    val updatedAt: String? = "",
    @Json(name = "visitHistoryId")
    val visitHistoryId: Int? = 0
) : AdAndStoreItem {
    fun isExist() = type == "EXISTS"
}