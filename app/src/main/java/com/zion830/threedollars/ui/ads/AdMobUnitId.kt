package com.zion830.threedollars.ui.ads

/**
 * 애드몹 광고 단위 ID 를 고른다.
 *
 * 서버(SDUI)가 내려준 [serverUnitId] 가 있으면 그 값을, 없거나 비어 있으면 앱 리소스의 [fallbackUnitId] 를 쓴다.
 * debug 빌드는 dev 서버도 운영 단위를 내려주므로 서버 값을 무시하고 테스트 단위로 덮어쓴 [fallbackUnitId] 를 쓴다.
 */
object AdMobUnitId {

    fun resolve(serverUnitId: String?, fallbackUnitId: String, isDebug: Boolean): String {
        if (isDebug) return fallbackUnitId
        return serverUnitId?.takeIf { it.isNotBlank() } ?: fallbackUnitId
    }

    /** 서버 단위로 불러오다 실패하면 [fallbackUnitId] 로 한 번 더 요청한다. 기본 단위까지 실패하면 null. */
    fun retryUnitId(failedUnitId: String?, fallbackUnitId: String): String? =
        fallbackUnitId.takeIf { failedUnitId != it }
}
