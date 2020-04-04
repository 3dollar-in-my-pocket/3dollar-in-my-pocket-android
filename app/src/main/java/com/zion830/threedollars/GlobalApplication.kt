package com.zion830.threedollars

import android.app.Application
import android.content.Context

/*
 * Created by yunji on 04/04/2020
 */
class GlobalApplication : Application() {

    companion object {
        private lateinit var APPLICATION_CONTEXT: Context

        @JvmStatic
        fun getContext(): Context {
            return APPLICATION_CONTEXT
        }
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION_CONTEXT = applicationContext
    }
}