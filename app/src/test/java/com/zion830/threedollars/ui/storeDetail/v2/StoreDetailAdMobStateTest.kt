package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.StoreDetailAdMobCardModel
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailAdMobStateTest {
    @Test
    fun loadedAdRemainsVisibleAfterRefreshFailure() {
        val state = StoreDetailAdMobState(card("one")) { _, _ -> }
        state.onLoadFailed()
        assertEquals(StoreDetailAdLoadState.Failed, state.loadState)
        state.onLoaded()
        state.onLoadFailed()
        assertEquals(StoreDetailAdLoadState.Loaded, state.loadState)
    }

    @Test
    fun eachCardUsesItsUpdatedLogsAndStopsCallbacksAfterDestroy() {
        val clicks = mutableListOf<String>()
        val impressions = mutableListOf<String>()
        val first = StoreDetailAdMobState(card("one")) { _, log -> impressions += log.objectId }
        val second = StoreDetailAdMobState(card("two")) { _, log -> impressions += log.objectId }
        first.update(card("one", "updated"), { _, log -> impressions += log.objectId }, { clicks += it.objectId })
        second.update(card("two"), { _, log -> impressions += log.objectId }, { clicks += it.objectId })
        first.onClicked()
        second.onClicked()
        first.onImpressed()
        assertEquals(listOf("updated", "two"), clicks)
        assertEquals(listOf("updated"), impressions)
        first.destroy()
        first.onClicked()
        first.onImpressed()
        assertEquals(listOf("updated", "two"), clicks)
        assertEquals(listOf("updated"), impressions)
        second.destroy()
    }

    private fun card(id: String, logId: String = id) = StoreDetailAdMobCardModel(
        id,
        SDClickLogModel(screenName = "test", objectType = "ad", objectId = logId),
        SDImpressionLogModel("IMPRESSION", "test", "ad", logId),
    )

}
