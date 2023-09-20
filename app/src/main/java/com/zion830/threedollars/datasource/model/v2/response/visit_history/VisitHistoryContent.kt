package com.zion830.threedollars.datasource.model.v2.response.visit_history


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.threedollar.common.data.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo

@JsonClass(generateAdapter = true)
data class VisitHistoryContent(
    @Json(name = "createdAt")
    val createdAt: String? = "",
    @Json(name = "dateOfVisit")
    val dateOfVisit: String? = "",
    @Json(name = "store")
    val store: StoreInfo = StoreInfo(),
    @Json(name = "type")
    val type: String? = "",
    @Json(name = "updatedAt")
    val updatedAt: String? = "",
    @Json(name = "visitHistoryId")
    val visitHistoryId: Int? = 0
) : AdAndStoreItem {
    fun isExist() = type == "EXISTS"
}