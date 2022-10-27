package com.zion830.threedollars.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.repository.model.MenuType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

object StringUtils {

    @JvmStatic
    fun getString(@StringRes resId: Int) = GlobalApplication.getContext().getString(resId)

    @JvmStatic
    fun getStringArray(resId: Int) = GlobalApplication.getContext().resources.getStringArray(resId)

    @JvmStatic
    fun toReadableString(value: Float) = if (value.toInt().toFloat() == value) {
        value.toInt().toString()
    } else {
        value.toString()
    }

    @JvmStatic
    fun getBearerTokenString(accessToken: String) = "Bearer $accessToken"

    @JvmStatic
    fun getReadableCategory(category: String?): String {
        if (category == null) {
            return getString(R.string.none)
        }

        val categories = SharedPrefUtils.getCategories()
        return categories.find { categoryInfo -> categoryInfo.category == category }?.category ?: ""
    }

    @JvmStatic
    fun getMenuTitle(menuType: MenuType): String {
        val stringNameArray = getStringArray(R.array.menu_name)
        val index = when (menuType) {
            MenuType.BUNGEOPPANG -> 0
            MenuType.TAKOYAKI -> 1
            MenuType.GYERANPPANG -> 2
            else -> 3
        }

        return stringNameArray[index]
    }

    @JvmStatic
    fun getMenuDescription(menuType: MenuType): String {
        val stringDescArray = getStringArray(R.array.menu_desc)
        val index = when (menuType) {
            MenuType.BUNGEOPPANG -> 0
            MenuType.TAKOYAKI -> 1
            MenuType.GYERANPPANG -> 2
            else -> 3
        }

        return stringDescArray[index]
    }

    @JvmStatic
    fun getTimeString(zuluString: String?, pattern: String = "MM월 dd일 HH:mm:ss"): String {
        return try {
            Instant.parse("${zuluString}Z")
                .atZone(ZoneId.of("Etc/UTC"))
                .format(DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREA))
        } catch (e: DateTimeParseException) {
            ""
        }
    }
    fun TextView.textPartTypeface(changeText: String?, @StyleRes style: Int, isLast: Boolean = false) {
        if (changeText == null)
            return
        val index = if (isLast) {
            text.toString().lastIndexOf(changeText)
        } else {
            text.toString().indexOf(changeText)
        }
        if (index != -1) {
            val ssb = SpannableString(text)

            ssb.setSpan(
                StyleSpan(style),
                index,
                index + changeText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text = ssb
        }
    }
}
