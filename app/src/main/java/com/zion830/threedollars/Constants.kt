package com.zion830.threedollars

object Constants {

    /* Intent code */
    const val ADD_STORE: Int = 10
    const val SHOW_STORE_BY_CATEGORY: Int = 11
    const val SHOW_STORE_DETAIL: Int = 12
    const val GET_LOCATION_PERMISSION: Int = 13
    const val GOOGLE_SIGN_IN: Int = 14

    /* GA event key */
    // common
    const val BACK_BTN_CLICKED = "back_button_clicked"
    const val CURRENT_LOCATION_BTN_CLICKED = "current_location_button_clicked"
    const val TOSS_BTN_CLICKED = "toss_button_clicked"

    //login
    const val KAKAO_BTN_CLICKED = "kakao_login_button_clicked"

    // nickname_init
    const val NICKNAME_CHANGE_BTN_CLICKED = "nickname_change_button_clicked"
    const val NICKNAME_ALREADY_EXISTED = "nickname_already_existed"

    // home
    const val SEARCH_BTN_CLICKED = "search_button_clicked"
    const val STORE_CARD_BTN_CLICKED = "store_card_button_clicked"

    // search
    const val CLOSE_BTN_CLICKED = "close_button_clicked"
    const val LOCATION_ITEM_CLICKED = "location_item_clicked"
    const val CATEGORY_BTN_CLICKED_FORMAT = "_button_clicked"

    // store list
    const val ORDER_BY_DISTANCE_BTN_CLICKED = "order_by_distance_button_list"
    const val ORDER_BY_RATING_BTN_CLICKED = "order_by_rating_button_list"
    const val STORE_LIST_ITEM_CLICKED = "store_list_item_clicked"

    // store detail
    const val STORE_DELETE_BTN_CLICKED = "store_delete_request_button_clicked"
    const val SHARE_BTN_CLICKED = "share_button_clicked"
    const val STORE_MODIFY_BTN_CLICKED = "store_modify_button_clicked"
    const val IMAGE_ATTACH_BTN_CLICKED = "image_attach_button_clicked"
    const val REVIEW_WRITE_BTN_CLICKED = "review_write_button_clicked"

    // review writrequestStoreInfo e
    const val REVIEW_WRITE_CLOSE_BTN_CLICKED = "review_write_close_button_clicked"
    const val STAR_BTN_CLICKED = "review_write_close_button_clicked"
    const val REVIEW_REGISTER_BTN_CLICKED = "review_write_close_button_clicked"

    // store edit
    const val ADDRESS_EDIT_BTN_CLICKED = "address_edit_button_clicked"
    const val STORE_EDIT_BTN_CLICKED = "store_edit_submit_button_clicked"

    // store register
    const val EDIT_ADDRESS_BTN_CLICKED = "edit_address_button_clicked"
    const val STORE_REGISTER_SUBMIT_BTN_CLICKED = "store_register_submit_button_clicked"

    // store delete
    const val DELETE_POPUP_CLOSE_BTN_CLICKED = "delete_request_popup_close_button_clicked"
    const val DELETE_REQUEST_SUBMIT_BTN_CLICKED = "delete_request_submit_button_clicked"

    // my info
    const val SETTING_BTN_CLICKED = "setting_button_clicked"
    const val SHOW_ALL_MY_STORE_BTN_CLICKED = "show_all_my_store_button_clicked"
    const val SHOW_ALL_MY_REVIEW_BTN_CLICKED = "show_all_my_review_button_clicked"

    // setting
    const val NICKNAME_CHANGE_PAGE_BTN_CLICKED = "nickname_change_page_button_clicked"
    const val LOGOUT_BTN_CLICKED = "logout_button_clicked"
    const val SIGNOUT_BTN_CLICKED = "signout_button_clicked"
    const val SIGNOUT_WITHDRAW_BTN_CLICKED = "signout_alert_withdraw_button_clicked"
    const val SIGNOUT_CANCEL_BTN_CLICKED = "signout_alert_cancel_button_clicked"
    const val INQUIRY_BTN_CLICKED = "inquiry_button_clicked"
    const val TERMS_OF_USE_BTN_CLICKED = "terms_of_use_button_clicked"
    const val FIREBASE_INTERVAL: Long = 3600L

    // orderType
    const val DISTANCE_ASC = "DISTANCE_ASC"
    const val REVIEW_DESC = "REVIEW_DESC"
}