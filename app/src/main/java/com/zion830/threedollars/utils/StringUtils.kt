package com.zion830.threedollars.utils

import androidx.annotation.StringRes
import com.zion830.threedollars.GlobalApplication
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object StringUtils {

    @JvmStatic
    fun getString(@StringRes resId: Int) = GlobalApplication.getContext().getString(resId)

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

}
