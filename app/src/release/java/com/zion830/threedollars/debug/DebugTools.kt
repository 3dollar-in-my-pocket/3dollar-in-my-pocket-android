package com.zion830.threedollars.debug

import android.app.Application

/** release 빌드에는 디버그 도구를 붙이지 않는다. 개발 빌드 구현은 `src/debug`. */
object DebugTools {
    fun install(application: Application) = Unit
}
