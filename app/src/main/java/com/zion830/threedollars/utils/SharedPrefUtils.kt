package com.zion830.threedollars.utils

import android.content.Context
import androidx.core.content.edit
import com.zion830.threedollars.GlobalApplication

object SharedPrefUtils {
    private const val PREFERENCE_FILE_KEY = "preference_file_key"
    private const val KAKAO_ID_KEY = "kakao_id_key"
    private const val USER_NAME_KEY = "user_name_key"
    private const val ACCESS_TOKEN_KEY = "access_token_key"
    private val sharedPreferences = GlobalApplication.getContext().getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    fun saveAccessToken(accessToken: String) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, accessToken)
        commit()
    }

    fun saveUserName(name: String) = sharedPreferences.edit {
        putString(USER_NAME_KEY, name)
        commit()
    }

    fun saveKakaoId(id: String) = sharedPreferences.edit {
        putString(ACCESS_TOKEN_KEY, id)
        commit()
    }

    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun getUserName() = sharedPreferences.getString(USER_NAME_KEY, null)

    fun getKakaoId() = sharedPreferences.getString(KAKAO_ID_KEY, null)
}