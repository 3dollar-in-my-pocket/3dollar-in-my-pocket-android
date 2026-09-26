package com.threedollar.common.sdui.model.element

enum class SDCustomActionType {
    STORE_PREVIEW_SECTION_SHARE,
    STORE_PREVIEW_SECTION_NAVIGATION,
    STORE_PREVIEW_SECTION_REVIEW_WRITE,
    STORE_EDIT_SECTION_UPDATE,
    STORE_EDIT_SECTION_REPORT,
    STORE_EDIT_SECTION_COPY_ADDRESS,
    STORE_EDIT_SECTION_MAP_ENLARGE,
    STORE_INFO_V2_SECTION_COPY_ACCOUNT_HOLDER,
    STORE_COUPON_SECTION_COUPON_ISSUE,
    STORE_COUPON_SECTION_COUPON_USE,
    STORE_POST_SECTION_ADD_LIKE,
    STORE_POST_SECTION_CANCEL_LIKE,
    STORE_IMAGE_SECTION_ADD_IMAGE,
    STORE_IMAGE_SECTION_IMAGE_ENLARGE,
    STORE_REVIEW_SECTION_REVIEW_WRITE,
    STORE_REVIEW_SECTION_REPORT,
    STORE_REVIEW_SECTION_DELETE,
    STORE_REVIEW_SECTION_ADD_LIKE,
    STORE_REVIEW_SECTION_CANCEL_LIKE
}

/**
 * 버튼이 링크 대신 클라이언트 동작을 요청할 때 내려오는 값.
 *
 * [actionType]은 모르는 값이면 null 이다(Gson enum 역직렬화 규칙). 서버는 [extraParams]에 문자열·숫자를 섞어
 * 내려주므로 값은 [param]으로 꺼낸다.
 */
data class SDCustomActionModel(
    val actionType: SDCustomActionType?,
    val extraParams: Map<String, Any?>?
) {
    /** 숫자로 내려온 값도 문자열로 돌려준다. 정수면 소수점을 붙이지 않는다. */
    fun param(key: String): String? = when (val value = extraParams?.get(key)) {
        null -> null
        is Number -> {
            val double = value.toDouble()
            if (double % 1.0 == 0.0) double.toLong().toString() else double.toString()
        }
        else -> value.toString()
    }

    fun doubleParam(key: String): Double? = when (val value = extraParams?.get(key)) {
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }

    companion object Keys {
        const val STORE_ID = "STORE_ID"
        const val STORE_TYPE = "STORE_TYPE"
        const val STORE_NAME = "STORE_NAME"
        const val URL = "URL"
        const val ADDRESS = "ADDRESS"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
        const val IMAGE_ID = "IMAGE_ID"
        const val IMAGE_URL = "IMAGE_URL"
        const val REVIEW_ID = "REVIEW_ID"
        const val POST_ID = "POST_ID"
        const val STICKER_ID = "STICKER_ID"
        const val COUPON_ID = "COUPON_ID"
        const val COUPON_ISSUED_KEY = "COUPON_ISSUED_KEY"

        /** 계좌 복사 버튼에 클라이언트가 채워 넣는 복사할 계좌 문구. 서버가 주면 서버 값을 쓴다. */
        const val ACCOUNT_NUMBER = "ACCOUNT_NUMBER"
    }
}
