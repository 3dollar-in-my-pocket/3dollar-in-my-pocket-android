package com.zion830.threedollars.ui.main

import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
import com.threedollar.domain.screen.repository.ScreenRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 하단 탭 응답을 앱 범위에 들고 있는다. 보통 스플래시가 받아 두고, 로그인 화면을 거쳐 메인에 들어와도 같은 결과를 쓴다.
 * 스플래시를 거치지 않은 진입(외부 링크로 콜드 스타트, 프로세스 재시작 복원)은 메인이 직접 [load]한다.
 */
@Singleton
class MainTabStore @Inject constructor(
    private val screenRepository: ScreenRepository,
) {

    @Volatile
    var tabs: List<BottomNavigationTabModel>? = null
        private set

    /** 응답을 받아 저장한다. 실패하면 이전 값을 그대로 두고 false. */
    suspend fun load(): Boolean {
        val response = runCatching { screenRepository.getBottomNavigation().first() }.getOrNull()
        val loaded = response?.takeIf { it.ok }?.data ?: return false
        tabs = loaded
        return true
    }

    companion object {
        /** 스플래시가 메인 진입 직전에 하단 탭 응답을 더 기다리는 최대 시간. */
        const val SPLASH_WAIT_MILLIS = 1_500L
    }
}

/** [timeoutMillis] 안에 끝나면 true. 넘기면 더 기다리지 않고 false 를 돌려준다(작업은 계속 돈다). */
suspend fun Job.joinWithin(timeoutMillis: Long): Boolean =
    withTimeoutOrNull(timeoutMillis) { join() } != null
