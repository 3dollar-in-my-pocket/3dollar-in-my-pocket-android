package com.threedollar.common.utils

fun Int?.toDefaultInt(): Int = this ?: 0
fun Long?.toDefaultLong(): Long = this ?: 0
fun Double?.toDefaultDouble(): Double = this ?: 0.0