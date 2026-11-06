package com.threedollar.network.util

import android.os.Build
import com.threedollar.network.BuildConfig

object AppUserAgent {
    val value: String
        get() = BuildConfig.VERSION_NAME +
            " (${if (BuildConfig.BUILD_TYPE == "debug") "com.zion830.threedollars.dev" else "com.zion830.threedollars"}); " +
            Build.VERSION.SDK_INT
}
