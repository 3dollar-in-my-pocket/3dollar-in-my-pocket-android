package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardHeaderModel
import com.threedollar.common.serverdriven.model.HomeListCardMetadataModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListAnalyticsTest {
    @Test
    fun `impression resolver includes basic empty and AdMob logs in response order`() {
        val basic = log("basic")
        val empty = log("empty")
        val ad = log("ad")
        val cards = listOf(
            HomeListCardModel.BasicCard("BASIC_CARD", "basic", HomeListCardHeaderModel(), HomeListCardMetadataModel(), impressionLog = basic),
            HomeListCardModel.EmptyCard("EMPTY_CARD", "empty", impressionLog = empty),
            HomeListCardModel.AdMobCard("ADMOB_CARD", "ad", impressionLog = ad),
        )

        assertEquals(listOf(basic, empty, ad), cards.homeImpressionLogs())
    }

    private fun log(id: String) = SDImpressionLogModel(
        eventType = "IMPRESSION",
        screenName = "HOME",
        objectType = "CARD",
        objectId = id,
    )
}
