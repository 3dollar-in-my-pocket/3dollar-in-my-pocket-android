package com.zion830.threedollars.ui.main

import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 스플래시가 받아 둔 하단 탭 응답. 로그인 화면을 거쳐 메인에 들어와도 같은 결과를 쓰려고 앱 범위에 둔다.
 * 응답이 없으면(실패·타임아웃·프로세스 재시작) 메인은 기본 탭을 쓴다.
 */
@Singleton
class MainTabStore @Inject constructor() {

    @Volatile
    var tabs: List<BottomNavigationTabModel>? = null
        private set

    fun update(tabs: List<BottomNavigationTabModel>) {
        this.tabs = tabs
    }

    companion object {
        /** 스플래시가 메인 진입 직전에 하단 탭 응답을 더 기다리는 최대 시간. */
        const val SPLASH_WAIT_MILLIS = 1_500L
    }
}

/** [timeoutMillis] 안에 끝나면 true. 넘기면 더 기다리지 않고 false 를 돌려준다(작업은 계속 돈다). */
suspend fun Job.joinWithin(timeoutMillis: Long): Boolean =
    withTimeoutOrNull(timeoutMillis) { join() } != null
