package com.threedollar.network.request

import com.google.gson.annotations.SerializedName

data class MarketingConsentRequest(
    @SerializedName("marketingConsent")
    val marketingConsent: String? = ""
)