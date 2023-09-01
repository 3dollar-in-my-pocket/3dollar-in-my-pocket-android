package com.zion830.threedollars.datasource.model.v4.poll


import com.google.gson.annotations.SerializedName

data class PollPolicyResponse(
    @SerializedName("createPolicy")
    val createPolicy: CreatePolicy = CreatePolicy()
)