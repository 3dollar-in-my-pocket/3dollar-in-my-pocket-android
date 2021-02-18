package com.zion830.threedollars.repository.model

import androidx.annotation.StringRes
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.StringUtils

enum class MenuType(val key: String, @StringRes val displayNameId: Int) {
    BUNGEOPPANG("BUNGEOPPANG", R.string.bung),
    TAKOYAKI("TAKOYAKI", R.string.tako),
    HOTTEOK("HOTTEOK", R.string.hodduck),
    GYERANPPANG("GYERANPPANG", R.string.gye);

    fun getName() = StringUtils.getString(displayNameId)

    companion object {

        fun of(key: String?): MenuType = values().find { it.key == key } ?: BUNGEOPPANG

        fun getKey(name: String?): String = values().find { StringUtils.getString(it.displayNameId) == name }?.key ?: "BUNGEOPPANG"
    }
}