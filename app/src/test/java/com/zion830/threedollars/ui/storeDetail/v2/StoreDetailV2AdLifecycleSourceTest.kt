package com.zion830.threedollars.ui.storeDetail.v2

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailV2AdLifecycleSourceTest {

    @Test
    fun `lazy list reuses ad view and destroys it only on permanent release`() {
        val engagementSource = source(
            "app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2EngagementSections.kt",
        )
        val contentSource = source(
            "app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Content.kt",
        )

        assertTrue(contentSource.contains("rememberStoreDetailAdMobStates("))
        assertTrue(engagementSource.contains("remember(context, storeId, cardIds)"))
        assertTrue(engagementSource.contains("onReset = { reusableAdView ->"))
        assertTrue(engagementSource.contains("reusableAdView.resume()"))
        assertTrue(engagementSource.contains("onRelease = { releasedAdView ->"))
        assertTrue(engagementSource.contains("releasedAdView.pause()"))
        assertFalse(engagementSource.contains("releasedAdView.destroy()"))
        assertTrue(engagementSource.contains("adStates.values.forEach { it.destroy() }"))
    }

    private fun source(repoRelativePath: String): String {
        val fromRepository = File(repoRelativePath)
        val fromAppModule = File(repoRelativePath.removePrefix("app/"))
        return sequenceOf(fromRepository, fromAppModule)
            .firstOrNull(File::isFile)
            ?.readText()
            ?: error("Cannot find source file: $repoRelativePath from ${File(".").absolutePath}")
    }
}
