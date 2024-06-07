package com.threedollar.network.data.user

import com.google.gson.annotations.SerializedName

data class Settings(
    @SerializedName("enableActivitiesPush")
    val enableActivitiesPush: Boolean = true,
    @SerializedName("marketingConsent")
    val marketingConsent: String = "APPROVE"
)
