package com.threedollar.common.ext

fun <T> Result<T>.toEmpty(): Result<Unit> = map { Unit }
