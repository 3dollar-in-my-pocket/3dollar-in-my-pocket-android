package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertTrue
import org.junit.Test

class HomeUIStateTest {

    @Test
    fun defaultFilterConditionsAreOff() {
        assertTrue(HomeUIState().filterConditionsType.isEmpty())
    }
}
