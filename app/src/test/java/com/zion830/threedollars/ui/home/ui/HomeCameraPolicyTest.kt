package com.zion830.threedollars.ui.home.ui

import com.threedollar.common.serverdriven.model.SDLocationModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeCameraPolicyTest {
    private val target = SDLocationModel(latitude = 37.5, longitude = 127.0)

    @Test
    fun `initial target waits for map and filter then applies configured zoom once`() {
        val policy = HomeInitialCameraPolicy()

        assertNull(policy.onInitialTarget(target, restored = false))
        assertNull(policy.onMapReady())
        assertEquals(HomeInitialCameraCommand(target, 15.5), policy.onFilterResolved(15.5))
        assertNull(policy.onFilterResolved(14.0))
    }

    @Test
    fun `filter then map ordering also applies configured zoom once`() {
        val policy = HomeInitialCameraPolicy()

        policy.onInitialTarget(target, restored = false)
        assertNull(policy.onFilterResolved(15.5))
        assertEquals(HomeInitialCameraCommand(target, 15.5), policy.onMapReady())
        assertNull(policy.onMapReady())
    }

    @Test
    fun `restored camera never applies initial zoom`() {
        val policy = HomeInitialCameraPolicy()

        policy.onInitialTarget(target, restored = true)
        assertEquals(HomeInitialCameraCommand(target, null), policy.onMapReady())
        assertNull(policy.onFilterResolved(15.5))
    }

    @Test
    fun `filter failure places target without changing zoom`() {
        val policy = HomeInitialCameraPolicy()

        policy.onInitialTarget(target, restored = false)
        policy.onMapReady()

        assertEquals(HomeInitialCameraCommand(target, null), policy.onFilterResolved(null))
    }

    @Test
    fun `gesture before late configuration prevents camera overwrite`() {
        val policy = HomeInitialCameraPolicy()
        policy.onInitialTarget(target, restored = false)
        policy.onMapReady()
        policy.onUserGesture()

        assertNull(policy.onFilterResolved(15.5))
    }

    @Test
    fun `invalid zoom is ignored`() {
        val policy = HomeInitialCameraPolicy()
        policy.onInitialTarget(target, restored = false)
        policy.onMapReady()

        assertEquals(HomeInitialCameraCommand(target, null), policy.onFilterResolved(Double.NaN))
    }

    @Test
    fun `focus bounds are allowed only for first search without restored position`() {
        val policy = HomeSearchFocusPolicy()

        assertEquals(true, policy.shouldRequestFocus(hasSavedPosition = false))
        assertEquals(false, policy.shouldRequestFocus(hasSavedPosition = true))
        assertEquals(false, policy.shouldRequestFocus(hasSavedPosition = false))
    }
}
