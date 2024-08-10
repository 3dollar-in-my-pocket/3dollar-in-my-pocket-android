package com.threedollar.domain.data

data class Neighborhoods(
    val neighborhoods: List<Neighborhood>,
) {
    data class Neighborhood(
        val description: String, // string
        val districts: List<District>,
        val province: String, // string
    ) {
        data class District(
            val description: String = "", // string
            val district: String = "", // string
        )
    }
}

data class NeighborhoodModel(
    val description: String = "",
    val districts: List<Neighborhoods.Neighborhood.District> = listOf(),
)