package com.zion830.threedollars.utils

import android.content.Context
import androidx.core.content.edit
import com.zion830.threedollars.GlobalApplication

object SharedPrefUtils {
    private const val PREFERENCE_FILE_KEY = "preference_file_key"
    private const val KAKAO_ACCESS_TOKEN = "kakao_access_token"
    private const val KAKAO_REFRESH_TOKEN = "kakao_refresh_token"
    private const val USER_NAME_KEY = "user_name_key"
    private const val USER_ID_KEY = "user_id_key"
    private const val ACCESS_TOKEN_KEY = "access_token_key"
    private const val FIRST_PERMISSION_CHECK = "first_permission_check"

    private val sharedPreferences = GlobalApplication.getContext().getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: String?) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken)
        commit()
    }

    fun saveUserName(name: String?) = sharedPreferences.edit {
        putString(USER_NAME_KEY, name)
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

    fun saveKakaoToken(accessToken: String?, refreshToken: String) = sharedPreferences.edit {
        putString(KAKAO_ACCESS_TOKEN, accessToken)
        putString(KAKAO_REFRESH_TOKEN, refreshToken)
        commit()
    }

    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun getUserId() = sharedPreferences.getInt(USER_ID_KEY, -1)

    fun getKakaoToken() = sharedPreferences.getString(KAKAO_ACCESS_TOKEN, null)

    fun getKakaoRefreshToken() = sharedPreferences.getString(KAKAO_REFRESH_TOKEN, null)

    fun clearUserInfo() {
        saveUserName(null)
        saveAccessToken("")
        saveKakaoToken("", "")
        saveUserId(-1)
    }
}