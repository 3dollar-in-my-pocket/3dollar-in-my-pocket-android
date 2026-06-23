package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListSectionQueryParamsBuilderTest {

    @Test
    fun build_preservesServerDrivenDynamicParams() {
        val params = HomeListSectionQueryParamsBuilder.build(
            dynamicParams = mapOf("sortType" to "SERVER_DISTANCE"),
        )

        assertEquals("SERVER_DISTANCE", params["sortType"])
    }
}
