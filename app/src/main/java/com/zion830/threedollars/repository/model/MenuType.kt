package com.zion830.threedollars.repository.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.StringUtils

enum class MenuType(
    val key: String,
    @DrawableRes val colorIcon: Int,
    @DrawableRes val grayIcon: Int,
) {
    @SerializedName("DALGONA")
    DALGONA("DALGONA", R.drawable.icon_menu_dalgona_selected, R.drawable.icon_menu_dalgona_normal),

    @SerializedName("BUNGEOPPANG")
    BUNGEOPPANG("BUNGEOPPANG", R.drawable.icon_menu_bungeoppang_selected, R.drawable.icon_menu_bungeoppang_nomal),

    @SerializedName("TAKOYAKI")
    TAKOYAKI("TAKOYAKI", R.drawable.icon_menu_takoyaki_selected, R.drawable.icon_menu_takoyaki_nomal),

    @SerializedName("GYERANPPANG")
    GYERANPPANG("GYERANPPANG", R.drawable.icon_menu_gyeranppang_selected, R.drawable.icon_menu_gyeranppang_nomal),

    @SerializedName("HOTTEOK")
    HOTTEOK("HOTTEOK", R.drawable.icon_menu_hodduck_selected, R.drawable.icon_menu_hodduck_nomal),

    @SerializedName("EOMUK")
    EOMUK("EOMUK", R.drawable.icon_menu_eomug_selected, R.drawable.icon_menu_eomug_nomal),

    @SerializedName("GUNGOGUMA")
    GUNGOGUMA("GUNGOGUMA", R.drawable.icon_menu_gungoguma_selected, R.drawable.icon_menu_gungoguma_nomal),

    @SerializedName("TTEOKBOKKI")
    TTEOKBOKKI("TTEOKBOKKI", R.drawable.icon_menu_tteogbokki_selected, R.drawable.icon_menu_tteogbokki_nomal),

    @SerializedName("TTANGKONGPPANG")
    TTANGKONGPPANG("TTANGKONGPPANG", R.drawable.icon_menu_ttangkongppang_selected, R.drawable.icon_menu_ttangkongppang_nomal),

    @SerializedName("GUNOKSUSU")
    GUNOKSUSU("GUNOKSUSU", R.drawable.icon_menu_oaksusu_selected, R.drawable.icon_menu_oaksusu_nomal),

    @SerializedName("KKOCHI")
    KKOCHI("KKOCHI", R.drawable.icon_menu_dalgkkochi_selected, R.drawable.icon_menu_dalgkkochi_nomal),

    @SerializedName("TOAST")
    TOAST("TOAST", R.drawable.icon_menu_toast_selected, R.drawable.icon_menu_toast_nomal),

    @SerializedName("WAFFLE")
    WAFFLE("WAFFLE", R.drawable.icon_menu_waffle_selected, R.drawable.icon_menu_waffle_nomal),

    @SerializedName("GUKWAPPANG")
    GUKWAPPANG("GUKWAPPANG", R.drawable.icon_menu_gughwappang_selected, R.drawable.icon_menu_gughwappang_nomal),

    @SerializedName("SUNDAE")
    SUNDAE("SUNDAE", R.drawable.icon_menu_sundae_selected, R.drawable.icon_menu_sundae_nomal);

    companion object {

        fun of(key: String?): MenuType = values().find { it.key == key } ?: BUNGEOPPANG
    }
}