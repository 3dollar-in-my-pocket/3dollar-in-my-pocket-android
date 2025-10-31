package com.zion830.threedollars.datasource.model

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

enum class MenuType(
    val key: String,
    @DrawableRes val colorIcon: Int,
    @DrawableRes val grayIcon: Int,
) {
    @SerializedName("DALGONA")
    DALGONA("DALGONA", DesignSystemR.drawable.icon_menu_dalgona_selected, DesignSystemR.drawable.icon_menu_dalgona_normal),

    @SerializedName("BUNGEOPPANG")
    BUNGEOPPANG("BUNGEOPPANG", DesignSystemR.drawable.icon_menu_bungeoppang_selected, DesignSystemR.drawable.icon_menu_bungeoppang_nomal),

    @SerializedName("TAKOYAKI")
    TAKOYAKI("TAKOYAKI", DesignSystemR.drawable.icon_menu_takoyaki_selected, DesignSystemR.drawable.icon_menu_takoyaki_nomal),

    @SerializedName("GYERANPPANG")
    GYERANPPANG("GYERANPPANG", DesignSystemR.drawable.icon_menu_gyeranppang_selected, DesignSystemR.drawable.icon_menu_gyeranppang_nomal),

    @SerializedName("HOTTEOK")
    HOTTEOK("HOTTEOK", DesignSystemR.drawable.icon_menu_hodduck_selected, DesignSystemR.drawable.icon_menu_hodduck_nomal),

    @SerializedName("EOMUK")
    EOMUK("EOMUK", DesignSystemR.drawable.icon_menu_eomug_selected, DesignSystemR.drawable.icon_menu_eomug_nomal),

    @SerializedName("GUNGOGUMA")
    GUNGOGUMA("GUNGOGUMA", DesignSystemR.drawable.icon_menu_gungoguma_selected, DesignSystemR.drawable.icon_menu_gungoguma_nomal),

    @SerializedName("TTEOKBOKKI")
    TTEOKBOKKI("TTEOKBOKKI", DesignSystemR.drawable.icon_menu_tteogbokki_selected, DesignSystemR.drawable.icon_menu_tteogbokki_nomal),

    @SerializedName("TTANGKONGPPANG")
    TTANGKONGPPANG("TTANGKONGPPANG", DesignSystemR.drawable.icon_menu_ttangkongppang_selected, DesignSystemR.drawable.icon_menu_ttangkongppang_nomal),

    @SerializedName("GUNOKSUSU")
    GUNOKSUSU("GUNOKSUSU", DesignSystemR.drawable.icon_menu_oaksusu_selected, DesignSystemR.drawable.icon_menu_oaksusu_nomal),

    @SerializedName("KKOCHI")
    KKOCHI("KKOCHI", DesignSystemR.drawable.icon_menu_dalgkkochi_selected, DesignSystemR.drawable.icon_menu_dalgkkochi_nomal),

    @SerializedName("TOAST")
    TOAST("TOAST", DesignSystemR.drawable.icon_menu_toast_selected, DesignSystemR.drawable.icon_menu_toast_nomal),

    @SerializedName("WAFFLE")
    WAFFLE("WAFFLE", DesignSystemR.drawable.icon_menu_waffle_selected, DesignSystemR.drawable.icon_menu_waffle_nomal),

    @SerializedName("GUKWAPPANG")
    GUKWAPPANG("GUKWAPPANG", DesignSystemR.drawable.icon_menu_gughwappang_selected, DesignSystemR.drawable.icon_menu_gughwappang_nomal),

    @SerializedName("SUNDAE")
    SUNDAE("SUNDAE", DesignSystemR.drawable.icon_menu_sundae_selected, DesignSystemR.drawable.icon_menu_sundae_nomal);

    companion object {

        fun of(key: String?): MenuType = values().find { it.key == key } ?: BUNGEOPPANG
    }
}