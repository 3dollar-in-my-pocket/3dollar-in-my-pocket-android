package com.zion830.threedollars.utils

import android.app.Activity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.R

object VersionChecker {
    private const val KEY_VERSION = "minimum_version_android"

    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = Constants.FIREBASE_INTERVAL
    }

    fun getMinimumVersionCode(activity: Activity, checkVersion: (String) -> Unit) {
        val remoteConfig = Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }

        remoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                checkVersion(remoteConfig[KEY_VERSION].asString())
            }
        }
    }

    fun checkForceUpdateAvailable(activity: Activity, needUpdate: (String, String) -> Unit, alreadyUpdate: () -> Unit) {
        getMinimumVersionCode(activity) { minimumVersion ->
            val packageName = activity.applicationContext.packageName
            val currentVersion = activity.applicationContext.packageManager.getPackageInfo(packageName, 0).versionName

            when {
                currentVersion == minimumVersion -> {
                    alreadyUpdate()
                }
                compareVersion(currentVersion, minimumVersion) == minimumVersion -> {
                    needUpdate(minimumVersion, currentVersion)
                }
                else -> {
                    alreadyUpdate()
                }
            }
        }
    }

    // 더 높은 버전을 반환
    private fun compareVersion(v1: String, v2: String, index: Int = 0): String = try {
        val code1 = v1.split(".")[index]
        val code2 = v2.split(".")[index]

        val nextIndex = index + 1
        when {
            code1 < code2 -> v2
            code1 == code2 -> {
                compareVersion(v1, v2, nextIndex)
            }
            else -> v1
        }
    } catch (e: IndexOutOfBoundsException) {
        v1
    }
}