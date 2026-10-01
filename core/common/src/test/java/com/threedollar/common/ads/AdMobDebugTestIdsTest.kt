package com.threedollar.common.ads

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** debug 빌드가 실광고 단위를 쓰지 않도록 main 의 광고 단위 ID 가 전부 테스트 ID 로 덮이는지 검사한다. */
class AdMobDebugTestIdsTest {

    private val adUnitPattern = Regex("""<string name="(admob_[a-z_]+)">([^<]*)</string>""")

    private fun adUnits(path: String): Map<String, String> =
        adUnitPattern.findAll(File(path).readText()).associate { it.groupValues[1] to it.groupValues[2] }

    private val mainAdUnits = adUnits("src/main/res/values/strings.xml")
    private val debugAdUnits = adUnits("src/debug/res/values/admob_test_ids.xml")

    @Test
    fun `main 의 광고 단위는 모두 debug 에서 덮어쓴다`() {
        // Given
        val mainNames = mainAdUnits.keys

        // When
        val missing = mainNames - debugAdUnits.keys

        // Then
        assertTrue("main 광고 단위가 있어야 검사가 의미 있다", mainNames.isNotEmpty())
        assertEquals("debug 테스트 ID 가 없는 광고 단위: $missing", emptySet<String>(), missing)
    }

    @Test
    fun `debug 광고 단위는 구글 테스트 퍼블리셔 ID 만 쓴다`() {
        // Given
        val googleTestPublisher = "ca-app-pub-3940256099942544/"

        // When
        val nonTestIds = debugAdUnits.filterValues { !it.startsWith(googleTestPublisher) }

        // Then
        assertEquals("테스트 ID 가 아닌 debug 광고 단위: $nonTestIds", emptyMap<String, String>(), nonTestIds)
    }
}
