package com.zion830.threedollars.ui.storeDetail.v2

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailV2StrictSourceContractTest {

    @Test
    fun `detail content does not create favorite divider or sticky UI outside the response`() {
        val content = source("app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Content.kt")
        val primarySections = source(
            "app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2PrimarySections.kt",
        )
        val engagementSections = source(
            "app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2EngagementSections.kt",
        )

        assertFalse(content.contains("StoreDetailSectionDivider"))
        assertFalse(content.contains("StoreDetailStickyActions"))
        assertFalse(content.contains("onFavoriteToggle"))
        assertFalse(primarySections.contains("CommonR.string.save"))
        assertFalse(primarySections.contains("withoutMissingContributorName"))
        assertFalse(primarySections.contains("localIconRoleOrNull"))
        assertFalse(primarySections.contains("imageOverride"))
        assertFalse(engagementSections.contains("height(1.dp).background(Gray20)"))
    }

    @Test
    fun `expanded detail does not add host navigation chrome`() {
        val fullScreen = source(
            "app/src/main/java/com/zion830/threedollars/ui/storeDetail/v2/StoreDetailV2Activity.kt",
        )
        val homeSheet = source(
            "app/src/main/java/com/zion830/threedollars/ui/home/ui/compose/HomeBottomSheetContent.kt",
        ).replace(Regex("\\s+"), " ")
        val homeFragment = source(
            "app/src/main/java/com/zion830/threedollars/ui/home/ui/HomeFragment.kt",
        ).replace(Regex("\\s+"), " ")

        assertFalse(fullScreen.contains("TopAppBar"))
        assertTrue(homeSheet.contains("if (!selectedStoreExpanded &&"))
        assertTrue(homeFragment.contains("showBottomNavigation(!isStoreDetailExpanded)"))
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
