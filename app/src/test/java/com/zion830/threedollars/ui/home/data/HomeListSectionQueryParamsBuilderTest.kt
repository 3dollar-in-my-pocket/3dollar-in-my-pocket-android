package com.zion830.threedollars.ui.home.data

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListSectionQueryParamsBuilderTest {

    @Test
    fun build_preservesDistanceAscSortTypeForServerAroundSortEnum() {
        val params = HomeListSectionQueryParamsBuilder.build(
            dynamicParams = mapOf("sortType" to HomeSortType.DISTANCE_ASC.name),
        )

        assertEquals(HomeSortType.DISTANCE_ASC.name, params["sortType"])
    }
}
