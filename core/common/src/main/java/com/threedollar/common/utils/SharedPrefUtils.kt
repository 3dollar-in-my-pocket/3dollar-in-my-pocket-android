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


    fun getSelectNeighborhoodDescription() = sharedPreferences.getString(SELECT_NEIGHBORHOOD_DESCRIPTION, "").toString()

    fun getSelectNeighborhoodDistrict() = sharedPreferences.getString(SELECT_NEIGHBORHOOD_DISTRICT, "").toString()

    fun saveSelectNeighborhoodDescription(description: String) = sharedPreferences.edit {
        putString(SELECT_NEIGHBORHOOD_DESCRIPTION, description)
        commit()
    }


    fun saveSelectNeighborhoodDistrict(district: String) = sharedPreferences.edit {
        putString(SELECT_NEIGHBORHOOD_DISTRICT, district)
        commit()
    }


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

    fun savePushToken(value: String) {
        sharedPreferences.edit(commit = true) {
            putString(PUSH_TOKEN, value)
        }
    }

    fun getPushToken(): String = sharedPreferences.getString(PUSH_TOKEN, null).orEmpty()

    fun clearUserInfo() {
        saveAccessToken("")
        saveKakaoToken("", "")
        saveGoogleToken("")
        saveLoginType(null)
        saveUserId(-1)
    }

    /**
     * 마지막으로 확인된 사용자 위치를 저장한다.
     *
     * 위치 획득에 실패했을 때 서울 중심 좌표로 떨어지는 대신 직전에 확인된 위치를 쓰기 위한 캐시다.
     */
    fun saveUserLastLocation(latitude: Double, longitude: Double) = sharedPreferences.edit {
        putLong(USER_LAST_LATITUDE, latitude.toRawBits())
        putLong(USER_LAST_LONGITUDE, longitude.toRawBits())
        commit()
    }

    /** 저장된 사용자 위치를 `위도 to 경도`로 반환한다. 저장된 값이 없으면 null. */
    fun getUserLastLocation(): Pair<Double, Double>? {
        if (!sharedPreferences.contains(USER_LAST_LATITUDE) || !sharedPreferences.contains(USER_LAST_LONGITUDE)) {
            return null
        }

        val latitude = Double.fromBits(sharedPreferences.getLong(USER_LAST_LATITUDE, 0L))
        val longitude = Double.fromBits(sharedPreferences.getLong(USER_LAST_LONGITUDE, 0L))
        return latitude to longitude
    }

    companion object {
        private const val PREFERENCE_FILE_KEY = "preference_file_key"
        private const val USER_LAST_LATITUDE = "user_last_latitude"
        private const val USER_LAST_LONGITUDE = "user_last_longitude"
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
        private const val SELECT_NEIGHBORHOOD_DESCRIPTION = "select_neighborhood_description"
        private const val SELECT_NEIGHBORHOOD_DISTRICT = "select_neighborhood_district"
        private const val PUSH_TOKEN = "push_token"
        val BOSS_FEED_BACK_LIST = "boss_feed_back_list"
    }
}