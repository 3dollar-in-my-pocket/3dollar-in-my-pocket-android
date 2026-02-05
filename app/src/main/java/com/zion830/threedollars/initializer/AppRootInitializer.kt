package com.zion830.threedollars.initializer

import android.content.Context
import androidx.startup.Initializer

class AppRootInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        /**
         * start-up 그래프를 위한 최상위 Initializer
         * 필요한 경우 구현합니다.
         */
        return Unit
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = listOf(
        ABCenterInitializer::class.java
    )
}
