package com.threedollar.network.data.neighborhood


import com.google.gson.annotations.SerializedName

data class GetNeighborhoodsResponse(
    @SerializedName("neighborhoods")
    val neighborhoods: List<Neighborhood>? = null
) {
    data class Neighborhood(
        @SerializedName("description")
        val description: String? = null, // string
        @SerializedName("districts")
        val districts: List<District>? = null,
        @SerializedName("province")
        val province: String? = null // string
    ) {
        data class District(
            @SerializedName("description")
            val description: String? = null, // string
            @SerializedName("district")
            val district: String? = null // string
        )
    }
}