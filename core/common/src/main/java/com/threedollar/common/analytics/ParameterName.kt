package com.threedollar.common.analytics

enum class ParameterName(val value: String) {
    SCREEN("screen"),
    OBJECT_ID("object_id"),
    OBJECT_TYPE("object_type"),
    TYPE("type"),
    NICKNAME("nickname"),
    ADDRESS("address"),
    CATEGORY_NAME("category_name"),
    STORE_ID("store_id"),
    VALUE("value"),
    ADVERTISEMENT_ID("advertisement_id"),
    CATEGORY_ID("category_id"),
    COUNT("count"),
    REVIEW_ID("review_id"),
    RATING("rating"),
    POLL_ID("poll_id"),
    OPTION_ID("option_id"),
    TITLE("title"),
    POLL_FIRST_OPTION("poll_first_option"),
    POLL_SECOND_OPTION("poll_second_option"),
    BUILDING_NAME("building_name"),
    MEDAL_ID("medal_id"),
    STORE_TYPE("store_type");

    override fun toString(): String = value
}
