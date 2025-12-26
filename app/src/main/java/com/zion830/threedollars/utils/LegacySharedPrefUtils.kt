package com.zion830.threedollars.utils

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.threedollar.domain.home.data.store.CategoryModel
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.response.store.CategoriesModel
import java.lang.reflect.Type

object LegacySharedPrefUtils {
    private const val PREFERENCE_FILE_KEY = "preference_file_key"
    private const val USER_ID_KEY = "user_id_key"
    private const val ACCESS_TOKEN_KEY = "access_token_key"
    private const val FIRST_PERMISSION_CHECK = "first_permission_check"
    private const val CATEGORY_LIST = "category_list"
    private const val TRUCK_CATEGORY_LIST = "truck_category_list"
    private const val LOGIN_TYPE = "login_type"
    private const val FIRST_MARKETING = "first_marketing"

    private val sharedPreferences = GlobalApplication.getContext()
        .getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

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

    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    fun saveCategories(categoryInfo: List<CategoriesModel>) {
        saveList(categoryInfo, CATEGORY_LIST)
    }

    fun saveTruckCategories(categoryInfo: List<CategoriesModel>) {
        saveList(categoryInfo, TRUCK_CATEGORY_LIST)
    }

    fun saveLoginType(loginType: LoginType?) = sharedPreferences.edit {
        putString(LOGIN_TYPE, loginType?.socialName)
        commit()
    }

    fun getLoginType() = sharedPreferences.getString(LOGIN_TYPE, "")

    fun getCategories(): List<CategoryModel> {
        return try {
            val gson = Gson()
            val json = sharedPreferences.getString(CATEGORY_LIST, null)
            val type: Type = object : TypeToken<List<CategoryModel>?>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getTruckCategories(): List<CategoryModel> {
        return try {
            val gson = Gson()
            val json = sharedPreferences.getString(TRUCK_CATEGORY_LIST, null)
            val type: Type = object : TypeToken<List<CategoryModel>?>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getFirstMarketing(): Boolean = sharedPreferences.getBoolean(FIRST_MARKETING, false)
    fun setFirstMarketing() {
        sharedPreferences.edit {
            putBoolean(FIRST_MARKETING, true)
            commit()
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
        saveLoginType(null)
        saveUserId(-1)
    }
}