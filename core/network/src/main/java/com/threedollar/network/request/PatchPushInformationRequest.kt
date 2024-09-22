package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class PatchPushInformationRequest(
    @SerializedName("enableActivitiesPush")
    val enableActivitiesPush: Boolean,
    @SerializedName("marketingConsent")
    val marketingConsent: String
)