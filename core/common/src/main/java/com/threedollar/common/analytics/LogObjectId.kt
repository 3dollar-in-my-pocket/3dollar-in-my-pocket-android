package com.threedollar.common.analytics

enum class LogObjectId(val value: String) {
    // Authentication
    SIGN_IN_KAKAO("sign_in_kakao"),
    SIGN_IN_GOOGLE("sign_in_google"),
    SIGN_UP("sign_up"),

    // Home Actions
    RANDOM("random"),
    STORE("store"),
    VISIT("visit"),
    CURRENT_LOCATION("current_location"),
    ADDRESS("address"),
    CATEGORY_FILTER("category_filter"),
    BOSS_FILTER("boss_filter"),
    SORTING("sorting"),
    RECENT_ACTIVITY_FILTER("recent_activity_filter"),
    ONLY_VISIT("only_visit"),

    // Common Actions
    DO_NOT_SHOW_TODAY("do_not_show_today"),
    CLOSE("close"),
    BOTTOM_BUTTON("bottom_button"),
    FAVORITE("favorite"),
    REPORT("report"),
    SHARE("share"),
    NAVIGATION("navigation"),
    WRITE_REVIEW("write_review"),
    COPY_ADDRESS("copy_address"),
    ZOOM_MAP("zoom_map"),
    LIKE("like"),
    SORT("sort"),

    // Store/Review Actions
    DELETE_REVIEW("delete_review"),
    VISIT_SUCCESS("visit_success"),
    VISIT_FAIL("visit_fail"),
    SNS("sns"),
    COPY_ACCOUNT("copy_account"),
    SET_ADDRESS("set_address"),
    NEXT("next"),
    SKIP("skip"),
    CATEGORY("category"),
    BANNER("banner"),
    MENU("menu"),
    ADVERTISEMENT("advertisement"),
    UPLOAD("upload"),
    BOSS("boss"),
    OK("ok"),
    INSTALL("install"),
    ADD_MENU("add_menu"),
    ADD_ADDITIONAL_INFO("add_additional_info"),
    LOCATION("location"),
    INFO("info"),
    EDIT("edit"),

    // Community
    FEED("feed"),
    POLL("poll"),
    POLL_OPTION("poll_option"),
    POLL_CATEGORY("poll_category"),
    DISTRICT("district"),
    FILTER("filter"),
    CREATE_POLL("create_poll"),
    REPORT_REVIEW("report_review"),
    CREATE("create"),

    // My Page
    MEDAL("medal"),
    VISITED_STORE("visited_store"),
    FAVORITED_STORE("favorited_store"),
    REVIEW("review");

    override fun toString(): String = value
}
