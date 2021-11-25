package com.zion830.threedollars.utils

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.repository.model.LoginType
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import java.lang.reflect.Type

object SharedPrefUtils {
    private const val PREFERENCE_FILE_KEY = "preference_file_key"
    private const val KAKAO_ACCESS_TOKEN = "kakao_access_token"
    private const val KAKAO_REFRESH_TOKEN = "kakao_refresh_token"
    private const val USER_ID_KEY = "user_id_key"
    private const val ACCESS_TOKEN_KEY = "access_token_key"
    private const val FIRST_PERMISSION_CHECK = "first_permission_check"
    private const val CATEGORY_LIST = "category_list"
    private const val LOGIN_TYPE = "login_type"
    private const val GOOGLE_TOKEN = "google_token"

    private val sharedPreferences = GlobalApplication.getContext().getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: String?) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken)
        commit()
    }

    fun changeServerStatus() = sharedPreferences.edit {
        putBoolean("test", !isTestServer())
        commit()
    }

    fun isTestServer() = sharedPreferences.getBoolean("test", true) // true일때 개발서버

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
        putString(KAKAO_REFRESH_TOKEN, refreshToken)
        commit()
    }

    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun getUserId() = sharedPreferences.getInt(USER_ID_KEY, -1)

    fun getKakaoAccessToken() = sharedPreferences.getString(KAKAO_ACCESS_TOKEN, null)

    fun getKakaoRefreshToken() = sharedPreferences.getString(KAKAO_REFRESH_TOKEN, null)

    fun saveCategories(categoryInfo: List<CategoryInfo>) {
        saveList(categoryInfo, CATEGORY_LIST)
    }

    fun saveLoginType(loginType: LoginType?) = sharedPreferences.edit {
        putString(LOGIN_TYPE, loginType?.socialName)
        commit()
    }

    fun getLoginType() = sharedPreferences.getString(LOGIN_TYPE, "")

    fun saveGoogleToken(token: String) = sharedPreferences.edit {
        putString(GOOGLE_TOKEN, token)
        commit()
    }

    fun getGoogleToken() = sharedPreferences.getString(GOOGLE_TOKEN, "")

    fun getCategories(): List<CategoryInfo> {
        return try {
            val gson = Gson()
            val json = sharedPreferences.getString(CATEGORY_LIST, null)
            val type: Type = object : TypeToken<List<CategoryInfo>?>() {}.type
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
}