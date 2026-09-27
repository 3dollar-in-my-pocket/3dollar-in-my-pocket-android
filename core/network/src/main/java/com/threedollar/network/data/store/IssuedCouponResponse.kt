package com.threedollar.network.data.store

import com.google.gson.annotations.SerializedName

/** `GET /api/v1/my/issued-coupons` 한 건. 쿠폰 정보와 내가 발급받은 상태([issued]), 쿠폰을 낸 가게를 함께 준다. */
data class IssuedCouponResponse(
    @SerializedName("couponId")
    val couponId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("validityPeriod")
    val validityPeriod: ValidityPeriod? = null,
    @SerializedName("issued")
    val issued: Issued? = null,
    @SerializedName("store")
    val store: IssuedCouponStore? = null,
) {
    data class ValidityPeriod(
        @SerializedName("startDateTime")
        val startDateTime: String? = null,
        @SerializedName("endDateTime")
        val endDateTime: String? = null,
    )

    data class Issued(
        @SerializedName("issuedKey")
        val issuedKey: String? = null,
        @SerializedName("status")
        val status: String? = null,
    )

    data class IssuedCouponStore(
        @SerializedName("storeId")
        val storeId: String? = null,
        @SerializedName("storeType")
        val storeType: String? = null,
        @SerializedName("storeName")
        val storeName: String? = null,
        @SerializedName("categories")
        val categories: List<Category>? = null,
    )
}
