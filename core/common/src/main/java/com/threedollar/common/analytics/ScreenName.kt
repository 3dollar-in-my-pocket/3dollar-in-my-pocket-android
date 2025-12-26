package com.threedollar.common.analytics

enum class ScreenName(val value: String) {
    // Basic Navigation
    EMPTY("empty"),
    SPLASH("splash"),

    // Authentication
    SIGN_IN("sign_in"),
    SIGN_UP("sign_up"),
    NICKNAME_INIT("nickname_init"),
    ACCOUNT_INFO("accountInfo"),

    // Home
    HOME("home"),
    HOME_LIST("home_list"),
    HOME_MAP("home_map"),
    CATEGORY_FILTER("category_filter"),
    MAIN_AD_BANNER("mainAdBanner"),
    SEARCH_ADDRESS("searchAddress"),

    // Write/Store Creation
    WRITE_ADDRESS("writeAddress"),
    WRITE_ADDRESS_POPUP("writeAddressPopup"),
    WRITE_ADDRESS_DETAIL("write_address_detail"),
    WRITE_ADDRESS_BOSS_BOTTOM_SHEET("writeAddressBossBottomSheet"),
    WRITE_DETAIL_COMPLETE("writeDetailComplete"),
    WRITE_DETAIL_ADDITIONAL_INFO("writeDetailAdditionalInfo"),
    WRITE_DETAIL_CATEGORY("writeDetailCategory"),
    WRITE_DETAIL_CATEGORY_BOTTOM_SHEET("writeDetailCategoryBottomSheet"),
    WRITE_DETAIL_INFO("writeDetailInfo"),
    WRITE_DETAIL_MENU("writeDetailMenu"),
    CATEGORY_SELECTION("categorySelection"),
    CATEGORY("category"),
    EDIT_STORE("editStore"),
    EDIT_STORE_INFO("editStoreInfo"),
    STORE_REGISTER("store_register"),
    STORE_EDIT("store_edit"),

    // Store Details
    STORE_DETAIL("store_detail"),
    UPLOAD_PHOTO("uploadPhoto"),
    REVIEW_LIST("review_list"),
    REPORT_STORE("report_store"),
    REVIEW_BOTTOM_SHEET("review_bottom_sheet"),
    VISIT_STORE("visitStore"),
    BOSS_STORE_DETAIL("boss_store_detail"),
    BOSS_STORE_REVIEW("bossStoreReview"),
    BOSS_STORE_REVIEW_WRITE("bossStoreReviewWrite"),
    BOSS_STORE_PHOTO("bossStorePhoto"),

    // Community
    COMMUNITY("community"),
    POLL_DETAIL("poll_detail"),
    POLL_LIST("poll_list"),
    REPORT_POLL("report_poll"),
    REPORT_REVIEW("report_review"),
    CREATE_POLL("create_poll"),

    // Review
    WRITE_REVIEW("write_review"),

    // My Page
    MY_PAGE("my_page"),
    MY_STORE("my_store"),
    REGISTERED_STORE("registeredStore"),
    VISITED_LIST("visitedList"),
    CLICK_REVIEW("clickReview"),
    MY_REVIEW("myReview"),
    MY_MEDAL("myMedal"),
    MY_BOOKMARK_LIST("myBookmarkList"),
    EDIT_BOOKMARK_LIST("editBookmarkList"),
    BOOKMARK_LIST_VIEWER("bookmarkListViewer"),

    // Settings
    SETTING("setting"),
    EDIT_NICKNAME("edit_nickname"),
    QNA("qna"),
    FAQ("faq"),
    TEAM_INFO("teamInfo"),

    // Feed
    FEED_LIST("feedList"),

    // Dialogs & Popups
    MARKER_POPUP("marker_popup"),
    ADDRESS_POPUP("address_popup"),
    ADDRESS("address");

    override fun toString(): String = value
}
