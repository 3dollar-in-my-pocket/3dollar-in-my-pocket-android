package com.zion830.threedollars.repository.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.StringUtils

enum class MenuType(
    val key: String,
    @StringRes val displayNameId: Int,
    @DrawableRes val colorIcon: Int,
    @DrawableRes val grayIcon: Int,
) {
    BUNGEOPPANG("BUNGEOPPANG", R.string.bung, R.drawable.icon_menu_bungeoppang_selected, R.drawable.icon_menu_bungeoppang_nomal),
    TAKOYAKI("TAKOYAKI", R.string.tako, R.drawable.icon_menu_takoyaki_selected, R.drawable.icon_menu_takoyaki_nomal),
    GYERANPPANG("GYERANPPANG", R.string.gye, R.drawable.icon_menu_gyeranppang_selected, R.drawable.icon_menu_gyeranppang_nomal),
    HOTTEOK("HOTTEOK", R.string.hodduck, R.drawable.icon_menu_hodduck_selected, R.drawable.icon_menu_hodduck_nomal),
    EOMUK("EOMUK", R.string.eomuk, R.drawable.icon_menu_eomug_selected, R.drawable.icon_menu_eomug_nomal),
    GUNGOGUMA("GUNGOGUMA", R.string.gungoguma, R.drawable.icon_menu_gungoguma_selected, R.drawable.icon_menu_gungoguma_nomal),
    TTEOKBOKKI("TTEOKBOKKI", R.string.tteokbokki, R.drawable.icon_menu_tteogbokki_selected, R.drawable.icon_menu_tteogbokki_nomal),
    TTANGKONGPPANG(
        "TTANGKONGPPANG",
        R.string.ttangkongppang,
        R.drawable.icon_menu_ttangkongppang_selected,
        R.drawable.icon_menu_ttangkongppang_nomal
    ),
    GUNOKSUSU("GUNOKSUSU", R.string.gunoksusu, R.drawable.icon_menu_oaksusu_selected, R.drawable.icon_menu_oaksusu_nomal),
    KKOCHI("KKOCHI", R.string.kkochi, R.drawable.icon_menu_dalgkkochi_selected, R.drawable.icon_menu_dalgkkochi_nomal),
    TOAST("TOAST", R.string.toast, R.drawable.icon_menu_toast_selected, R.drawable.icon_menu_toast_nomal),
    WAFFLE("WAFFLE", R.string.waffle, R.drawable.icon_menu_waffle_selected, R.drawable.icon_menu_waffle_nomal),
    GUKWAPPANG("GUKWAPPANG", R.string.gukwappang, R.drawable.icon_menu_gughwappang_selected, R.drawable.icon_menu_gughwappang_nomal),
    SUNDAE("SUNDAE", R.string.sundae, R.drawable.icon_menu_sundae_selected, R.drawable.icon_menu_sundae_nomal);

    fun getName() = StringUtils.getString(displayNameId)

    companion object {

        fun of(key: String?): MenuType = values().find { it.key == key } ?: BUNGEOPPANG

        fun getKey(name: String?): String = values().find { StringUtils.getString(it.displayNameId) == name }?.key ?: "BUNGEOPPANG"
    }
}