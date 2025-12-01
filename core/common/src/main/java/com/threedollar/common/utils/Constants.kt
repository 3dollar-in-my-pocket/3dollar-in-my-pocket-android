package com.threedollar.common.utils

object Constants {

    /* Intent code */
    const val ADD_STORE: Int = 10
    const val SHOW_STORE_BY_CATEGORY: Int = 11
    const val SHOW_STORE_DETAIL: Int = 12
    const val GET_LOCATION_PERMISSION: Int = 13
    const val GOOGLE_SIGN_IN: Int = 14
    const val MODE_ROAD_FOOD: Int = 15
    const val MODE_FOOD_TRUCK: Int = 16

    /* GA event key */
    // common
    const val REFERRAL = "referral"
    const val CATEGORY_BANNER_CLICKED = "category_banner_clicked"
    const val BACK_BTN_CLICKED = "back_button_clicked"
    const val CURRENT_LOCATION_BTN_CLICKED = "current_location_button_clicked"

    //login
    const val KAKAO_BTN_CLICKED = "click_sign_in_kakao"
    const val GOOGLE_BTN_CLICKED = "click_sign_in_google"

    //signUp
    const val CLICK_SIGN_UP = "click_sign_up"

    // nickname_init
    const val NICKNAME_CHANGE_BTN_CLICKED = "nickname_change_button_clicked"
    const val NICKNAME_ALREADY_EXISTED = "nickname_already_existed"

    // home
    const val HOME_REOPEN = "home_reopen"
    const val CLICK_STORE = "click_store"
    const val CLICK_VISIT = "click_visit"
    const val CLICK_CURRENT_LOCATION = "click_current_location"
    const val CLICK_MARKER = "click_marker"
    const val CLICK_ADDRESS_FIELD = "click_address_field"
    const val CLICK_CATEGORY_FILTER = "click_category_filter"
    const val CLICK_BOSS_FILTER = "click_boss_filter"
    const val CLICK_SORTING = "click_sorting"
    const val CLICK_AD_CARD = "click_ad_card"
    const val CLICK_AD_MARKER = "click_ad_marker"
    const val CLICK_RECENT_ACTIVITY_FILTER = "click_recent_activity_filter"
    const val CLICK_ADDRESS = "click_address"
    const val CLICK_ONLY_VISIT = "click_only_visit"

    // category
    const val CLICK_CATEGORY = "click_category"
    const val CLICK_AD_BANNER = "click_ad_banner"

    // main_ad_banner
    const val CLICK_NOT_SHOW_TODAY = "click_not_show_today"
    const val CLICK_CLOSE = "click_close"

    // marker_popup
    const val CLICK_BOTTOM_BUTTON = "click_bottom_button"

    // store detail
    const val CLICK_FAVORITE = "click_favorite"
    const val CLICK_REPORT = "click_report"
    const val CLICK_SHARE = "click_share"
    const val CLICK_NAVIGATION = "click_navigation"
    const val CLICK_WRITE_REVIEW = "click_write_review"
    const val CLICK_COPY_ADDRESS = "click_copy_address"
    const val CLICK_ZOOM_MAP = "click_zoom_map"

    // upload_photo
    const val CLICK_UPLOAD = "click_upload"

    // review_list
    const val CLICK_SORT = "click_sort"
    const val CLICK_EDIT_REVIEW = "click_edit_review"

    // review_bottom_sheet
    const val CLICK_REVIEW_BOTTOM_BUTTON = "click_review_bottom_button"

    // visit_store
    const val CLICK_VISIT_SUCCESS = "click_visit_success"
    const val CLICK_VISIT_FAIL = "click_visit_fail"

    // boss_store_detail
    const val CLICK_SNS = "click_sns"
    const val CLICK_NUMBER = "click_number"

    // write_address
    const val CLICK_SET_ADDRESS = "click_set_address"

    // write_address_popup
    const val CLICK_ADDRESS_OK = "click_address_ok"

    // store edit
    const val CLICK_WRITE_STORE = "click_write_store"

    // store register
    const val EDIT_ADDRESS_BTN_CLICKED = "edit_address_button_clicked"
    const val STORE_REGISTER_SUBMIT_BTN_CLICKED = "store_register_submit_button_clicked"

    // store delete
    const val DELETE_POPUP_CLOSE_BTN_CLICKED = "delete_request_popup_close_button_clicked"
    const val DELETE_REQUEST_SUBMIT_BTN_CLICKED = "delete_request_submit_button_clicked"

    // community
    const val CLICK_POLL = "click_poll"
    const val CLICK_POLL_OPTION = "click_poll_option"
    const val CLICK_POLL_CATEGORY = "click_poll_category"
    const val CLICK_DISTRICT = "click_district"
    const val CLICK_POPULAR_FILTER = "click_popular_filter"
    const val CLICK_CREATE_POLL = "click_create_poll"

    // my info
    const val SETTING_BTN_CLICKED = "setting_button_clicked"
    const val SHOW_ALL_MY_STORE_BTN_CLICKED = "show_all_my_store_button_clicked"
    const val SHOW_ALL_MY_MEDAL_BTN_CLICKED = "show_all_my_medal_button_clicked"
    const val SHOW_ALL_MY_REVIEW_BTN_CLICKED = "show_all_my_review_button_clicked"

    // setting
    const val NICKNAME_CHANGE_PAGE_BTN_CLICKED = "nickname_change_page_button_clicked"
    const val LOGOUT_BTN_CLICKED = "logout_button_clicked"
    const val SIGNOUT_BTN_CLICKED = "signout_button_clicked"
    const val SIGNOUT_WITHDRAW_BTN_CLICKED = "signout_alert_withdraw_button_clicked"
    const val SIGNOUT_CANCEL_BTN_CLICKED = "signout_alert_cancel_button_clicked"
    const val INQUIRY_BTN_CLICKED = "inquiry_button_clicked"
    const val TERMS_OF_USE_BTN_CLICKED = "terms_of_use_button_clicked"
    const val PRIVACY_POLICY_OF_USE_BTN_CLICKED = "privacy_policy_of_use_button_clicked"
    const val TEAM_INTRODUCE_BTN_CLICKED = "team_introduce_button_clicked"
    const val FIREBASE_INTERVAL: Long = 3600L

    // orderType
    const val DISTANCE_ASC = "DISTANCE_ASC"
    const val LATEST = "LATEST"
    const val TOTAL_FEEDBACKS_COUNTS_DESC = "TOTAL_FEEDBACKS_COUNTS_DESC"
    const val REVIEW_DESC = "REVIEW_DESC"

    //storeType
    const val BOSS_STORE = "BOSS_STORE"
    const val USER_STORE = "USER_STORE"
    const val STORE = "STORE"

    const val FAVORITE_STORE = "FAVORITE_STORE"
}