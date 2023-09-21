package com.zion830.threedollars.datasource.model.v2.response.store


import com.google.gson.annotations.SerializedName

data class PlatformStoreFoodCategoryResponse(
    @SerializedName("data")
    val `data`: List<Data>? = listOf(),
    @SerializedName("message")
    val message: String? = "", // string
    @SerializedName("resultCode")
    val resultCode: String? = "" // string
) {
    data class Data(
        @SerializedName("categoryId")
        val categoryId: String? = "", // BUNGEOPPANG
        @SerializedName("classification")
        val classification: Classification? = Classification(),
        @SerializedName("description")
        val description: String? = "", // string
        @SerializedName("disableImageUrl")
        val disableImageUrl: String? = "", // string
        @SerializedName("imageUrl")
        val imageUrl: String? = "", // string
        @SerializedName("isNew")
        val isNew: Boolean? = false, // true
        @SerializedName("name")
        val name: String? = "" // string
    ) {
        data class Classification(
            @SerializedName("description")
            val description: String? = "", // string
            @SerializedName("type")
            val type: String? = "" // MEAL
        )
    }
}