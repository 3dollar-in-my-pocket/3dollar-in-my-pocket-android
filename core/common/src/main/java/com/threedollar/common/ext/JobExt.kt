package com.threedollar.common.ext

import kotlinx.coroutines.Job

val Job?.isRunning: Boolean
    get() = this?.isActive == true