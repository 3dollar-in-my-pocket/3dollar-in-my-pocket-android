package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.SDViewLogModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomePageViewCoordinatorTest {
    @Test
    fun `resolved page view is emitted once for every home entry`() {
        val coordinator = HomePageViewCoordinator()
        val server = HomePageViewEvent(SDViewLogModel(screenName = "HOME_SERVER"))

        assertNull(coordinator.onEntry())
        assertEquals(listOf(server), coordinator.resolve(server))
        assertEquals(server, coordinator.onEntry())
        assertEquals(server, coordinator.onEntry())
    }

    @Test
    fun `late recovery updates future entry without duplicating current entry`() {
        val coordinator = HomePageViewCoordinator()
        val legacy = HomePageViewEvent(serverLog = null)
        val recovered = HomePageViewEvent(SDViewLogModel(screenName = "HOME_SERVER"))

        assertNull(coordinator.onEntry())
        assertEquals(listOf(legacy), coordinator.resolve(legacy))
        assertEquals(emptyList<HomePageViewEvent>(), coordinator.resolve(recovered))
        assertEquals(recovered, coordinator.onEntry())
    }
}
