package com.zion830.threedollars.datasource.model.v4.districts


import com.google.gson.annotations.SerializedName

data class DistrictsResponse(
    @SerializedName("cityProvince")
    val cityProvince: CityProvince = CityProvince(),
    @SerializedName("districts")
    val districts: List<District> = listOf()
)