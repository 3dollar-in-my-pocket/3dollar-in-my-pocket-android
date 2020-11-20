package com.zion830.threedollars.repository.model.response


import com.google.gson.annotations.SerializedName


data class Response(
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("path")
    val path: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("timestamp")
    val timestamp: String
)