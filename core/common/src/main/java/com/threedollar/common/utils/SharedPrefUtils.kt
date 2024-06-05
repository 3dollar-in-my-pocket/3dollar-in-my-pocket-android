package com.threedollar.common.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.Type
import javax.inject.Inject

class SharedPrefUtils @Inject constructor(@ApplicationContext private val context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun getTodayNotPopupDate() = sharedPreferences.getString(TODAY_NOT_POPUP_DATE, "")

    fun setTodayNotPopupDate(date: String) = sharedPreferences.edit {
        putString(TODAY_NOT_POPUP_DATE, date)
        commit()
    }

    fun setIsClickFilterConditions() = sharedPreferences.edit {
        putBoolean(IS_CLICK_FILTER_CONDITIONS, true)
        commit()
    }

    fun getIsClickFilterConditions() = sharedPreferences.getBoolean(IS_CLICK_FILTER_CONDITIONS, false)

    fun saveAccessToken(accessToken: String?) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken)
        commit()
    }

    fun isFirstPermissionCheck(): Boolean {
        val isFirst = sharedPreferences.getBoolean(FIRST_PERMISSION_CHECK, true)
        if (isFirst) {
            sharedPreferences.edit {
                putBoolean(FIRST_PERMISSION_CHECK, false)
                commit()
            }
        }
        return isFirst
    }

    fun saveUserId(id: Int) = sharedPreferences.edit {
        putInt(USER_ID_KEY, id)
        commit()
    }

    fun saveKakaoToken(accessToken: String?, refreshToken: String?) = sharedPreferences.edit {
        putString(KAKAO_ACCESS_TOKEN, accessToken)
        if (!refreshToken.isNullOrBlank()) {
            putString(KAKAO_REFRESH_TOKEN, refreshToken)
        }
        commit()
    }

    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun getUserId() = sharedPreferences.getInt(USER_ID_KEY, -1)

    fun getKakaoAccessToken() = sharedPreferences.getString(KAKAO_ACCESS_TOKEN, null)

    fun getKakaoRefreshToken() = sharedPreferences.getString(KAKAO_REFRESH_TOKEN, null)

    fun saveLoginType(loginType: String?) = sharedPreferences.edit {
        putString(LOGIN_TYPE, loginType)
        commit()
    }

    fun getLoginType() = sharedPreferences.getString(LOGIN_TYPE, "")

    fun saveGoogleToken(token: String) = sharedPreferences.edit {
        putString(GOOGLE_TOKEN, token)
        commit()
    }

    fun getGoogleToken() = sharedPreferences.getString(GOOGLE_TOKEN, "")

    inline fun <reified T> getList(key: String): List<T> {
        return try {
            val gson = Gson()
            val json = sharedPreferences.getString(key, null)
            val type: Type = object : TypeToken<List<T>?>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun <T> saveList(list: List<T?>?, key: String?) {
        if (list.isNullOrEmpty()) {
            return
        }

        sharedPreferences.edit {
            val gson = Gson()
            val json: String = gson.toJson(list)
            putString(key, json)
            apply()
        }
    }

    fun clearUserInfo() {
        saveAccessToken("")
        saveKakaoToken("", "")
        saveGoogleToken("")
        saveLoginType(null)
        saveUserId(-1)
    }

    companion object {
        private const val PREFERENCE_FILE_KEY = "preference_file_key"
        private const val KAKAO_ACCESS_TOKEN = "kakao_access_token"
        private const val KAKAO_REFRESH_TOKEN = "kakao_refresh_token"
        private const val USER_ID_KEY = "user_id_key"
        private const val ACCESS_TOKEN_KEY = "access_token_key"
        private const val FIRST_PERMISSION_CHECK = "first_permission_check"
        private const val CATEGORY_LIST = "category_list"
        private const val TRUCK_CATEGORY_LIST = "truck_category_list"
        private const val FEED_BACK_LIST = "feed_back_list"
        private const val LOGIN_TYPE = "login_type"
        private const val GOOGLE_TOKEN = "google_token"
        private const val TODAY_NOT_POPUP_DATE = "popup_url"
        private const val FOOD_TRUCK_TOOL_TIP = "food_truck_tool_tip"
        private const val IS_CLICK_FILTER_CONDITIONS = "is_click_filter_conditions"
        val BOSS_FEED_BACK_LIST = "boss_feed_back_list"
    }
}