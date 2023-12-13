package com.threedollar.common.listener

import android.app.Activity
import android.content.Context

/**
 * 한 동안 모듈이 전체 분리될때까지 APP모듈에 있는 특정 엑티비티에 접근하기 위한 인터페이스입니다.
 */
interface ActivityStarter {
    fun startStoreDetailActivity(
        context: Context,
        storeId: Int? = null,
        startCertification: Boolean = false,
        deepLinkStoreId: String? = null
    )

    fun startBossDetailActivity(context: Context, storeId: String? = null, deepLinkStoreId: String? = null)

    fun activityNavigateToMainActivityOnCloseIfNeeded(activity: Activity)
}