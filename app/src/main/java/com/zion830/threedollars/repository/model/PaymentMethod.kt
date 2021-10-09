package com.zion830.threedollars.repository.model

import com.google.gson.annotations.SerializedName

enum class PaymentMethod {
    @SerializedName("CASH")
    CASH,

    @SerializedName("CARD")
    CARD,

    @SerializedName("ACCOUNT_TRANSFER")
    ACCOUNT_TRANSFER;
}