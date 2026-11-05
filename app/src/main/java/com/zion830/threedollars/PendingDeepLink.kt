package com.zion830.threedollars

import android.os.SystemClock

/**
 * TH-1070: 앱 밖(푸시·외부 링크)에서 연 링크를 세션 만료(401) 뒤 로그인 화면까지 이어 주는 프로세스 단위 저장소.
 *
 * 링크로 연 화면이 곧바로 401 을 받으면 [GlobalApplication] 이 [takeIfRecent] 로 링크를 꺼내 로그인 화면에 넘기고,
 * 로그인 성공 뒤 같은 링크를 다시 연다. 사용자가 한참 다른 화면을 쓰다 만료된 경우까지 되돌리지 않도록
 * [VALID_MILLIS] 안에 만료된 경우만 이어 준다.
 */
object PendingDeepLink {
    const val VALID_MILLIS = 60_000L

    private var link: String? = null
    private var openedAt: Long = 0L

    @Synchronized
    fun record(link: String, now: Long = SystemClock.elapsedRealtime()) {
        this.link = link
        this.openedAt = now
    }

    /** 최근 [VALID_MILLIS] 안에 연 링크를 돌려주고 비운다. 오래됐거나 없으면 null. */
    @Synchronized
    fun takeIfRecent(now: Long = SystemClock.elapsedRealtime()): String? {
        val recent = link
        val elapsed = now - openedAt
        clear()
        return recent?.takeIf { elapsed in 0..VALID_MILLIS }
    }

    @Synchronized
    fun clear() {
        link = null
        openedAt = 0L
    }
}
