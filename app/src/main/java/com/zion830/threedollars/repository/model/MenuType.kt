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
    val introTitle: String
) {
    BUNGEOPPANG("BUNGEOPPANG", R.string.bung, R.drawable.icon_menu_bungeoppang_selected, R.drawable.icon_menu_bungeoppang_nomal, "붕어빵 만나기 30초 전"),
    TAKOYAKI("TAKOYAKI", R.string.tako, R.drawable.icon_menu_takoyaki_selected, R.drawable.icon_menu_takoyaki_nomal, "문어빵 다 내꺼야"),
    GYERANPPANG("GYERANPPANG", R.string.gye, R.drawable.icon_menu_gyeranppang_selected, R.drawable.icon_menu_gyeranppang_nomal, "계란빵, 내 입으로"),
    HOTTEOK("HOTTEOK", R.string.hodduck, R.drawable.icon_menu_hodduck_selected, R.drawable.icon_menu_hodduck_nomal, "호떡아 기다려"),
    EOMUK("EOMUK", R.string.eomuk, R.drawable.icon_menu_eomug_selected, R.drawable.icon_menu_eomug_nomal, "날 쏴줘 어묵탕"),
    GUNGOGUMA("GUNGOGUMA", R.string.gungoguma, R.drawable.icon_menu_gungoguma_selected, R.drawable.icon_menu_gungoguma_nomal, "널 생각하면 목이막혀,\n군고구마"),
    TTEOKBOKKI(
        "TTEOKBOKKI",
        R.string.tteokbokki,
        R.drawable.icon_menu_tteogbokki_selected,
        R.drawable.icon_menu_tteogbokki_nomal,
        "떡볶이...\n너 500원이었잖아"
    ),
    TTANGKONGPPANG(
        "TTANGKONGPPANG",
        R.string.ttangkongppang,
        R.drawable.icon_menu_ttangkongppang_selected,
        R.drawable.icon_menu_ttangkongppang_nomal,
        "땅콩빵, 오늘 널 갖겠어"
    ),
    GUNOKSUSU("GUNOKSUSU", R.string.gunoksusu, R.drawable.icon_menu_oaksusu_selected, R.drawable.icon_menu_oaksusu_nomal, "버터까지 발라서 굽겠어\n군옥수수"),
    KKOCHI("KKOCHI", R.string.kkochi, R.drawable.icon_menu_dalgkkochi_selected, R.drawable.icon_menu_dalgkkochi_nomal, "꼬치꼬치 캐묻지마 ♥︎"),
    TOAST("TOAST", R.string.toast, R.drawable.icon_menu_toast_selected, R.drawable.icon_menu_toast_nomal, "너네 사이에 나도 껴주라,\n토스트"),
    WAFFLE("WAFFLE", R.string.waffle, R.drawable.icon_menu_waffle_selected, R.drawable.icon_menu_waffle_nomal, "넌 어쩜 이름도\n와플일까?"),
    GUKWAPPANG(
        "GUKWAPPANG",
        R.string.gukwappang,
        R.drawable.icon_menu_gughwappang_selected,
        R.drawable.icon_menu_gughwappang_nomal,
        "사계절 너가 생각나\n국화빵"
    ),
    SUNDAE("SUNDAE", R.string.sundae, R.drawable.icon_menu_sundae_selected, R.drawable.icon_menu_sundae_nomal, "제발 순대\n간 허파 많이 주세요");

    fun getName() = StringUtils.getString(displayNameId)

    companion object {

        fun of(key: String?): MenuType = values().find { it.key == key } ?: BUNGEOPPANG

        fun getKey(name: String?): String = values().find { StringUtils.getString(it.displayNameId) == name }?.key ?: "BUNGEOPPANG"
    }
}