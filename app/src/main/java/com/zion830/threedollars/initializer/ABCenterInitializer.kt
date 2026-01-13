package com.zion830.threedollars.initializer

import android.content.Context
import androidx.startup.Initializer
import com.threedollar.abtest.ABTestCenter
import com.zion830.threedollars.di.InitializerEntryPoint

class ABCenterInitializer : Initializer<ABTestCenter> {

    override fun create(context: Context): ABTestCenter {
        return InitializerEntryPoint.resolve(context).abTestCenter().apply { init() }
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()
}
