package com.threedollar.common.utils

import android.content.Context
import com.threedollar.common.R

fun Context.getDistanceText(distanceM: Int): String {
    return when {
        distanceM < 1000 -> {
            "${distanceM}m"
        }
        distanceM < 10_000 -> {
            // 정수 단위로 km 변환
            "${distanceM / 1000}km"
        }
        else -> {
            // 10km 이상일 경우
            getString(R.string.more_10km)
        }
    }
}