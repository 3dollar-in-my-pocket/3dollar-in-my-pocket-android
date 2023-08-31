package com.zion830.threedollars.datasource.model.v4.districts


import com.google.gson.annotations.SerializedName

data class District(
    @SerializedName("district")
    val district: DistrictX = DistrictX()
)