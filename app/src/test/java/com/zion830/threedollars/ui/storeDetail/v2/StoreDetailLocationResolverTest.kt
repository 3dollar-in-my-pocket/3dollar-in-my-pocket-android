package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDLocationModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailMapModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StoreDetailLocationResolverTest {
    private val footer = StoreActionBarModel("ACTION_BAR", SDButtonModel(SDTextModel("지도", false)))
    private val editLocation = SDLocationModel(37.5, 127.0)
    private val legacyLocation = SDLocationModel(38.0, 128.0)
    private val screen = StoreDetailScreenModel(
        listOf(
            StoreDetailSectionModel.Map("MAP", legacyLocation, null, footer),
            StoreDetailSectionModel.Edit("EDIT", emptyList(), StoreDetailMapModel(editLocation, null, footer)),
        ),
        SDViewLogModel("store_detail"),
    )

    @Test
    fun editLocationIsUsedBeforeLegacyMapEvenWhenLegacyAppearsFirst() {
        assertEquals(editLocation, screen.resolveStoreDetailLocation())
    }

    @Test
    fun explicitCoordinatesMustBeACompleteValidPair() {
        assertEquals(SDLocationModel(33.4, 126.9), screen.resolveStoreDetailLocation(action(33.4, 126.9)))
        assertEquals(editLocation, screen.resolveStoreDetailLocation(action(33.4, null)))
        assertEquals(editLocation, screen.resolveStoreDetailLocation(action(null, 126.9)))
        assertEquals(editLocation, screen.resolveStoreDetailLocation(action(Double.NaN, 126.9)))
        assertEquals(editLocation, screen.resolveStoreDetailLocation(action(91.0, 126.9)))
    }

    @Test
    fun homeMarkerRequiresTheSameSelectedStoreAndNeverCreatesZeroCoordinates() {
        val empty = StoreDetailScreenModel(emptyList(), SDViewLogModel("store_detail"))
        assertEquals(editLocation, empty.resolveStoreDetailLocation(storeId = 1, markerStoreId = 1, markerLocation = editLocation))
        assertNull(empty.resolveStoreDetailLocation(storeId = 1, markerStoreId = 2, markerLocation = editLocation))
        assertNull(empty.resolveStoreDetailLocation(markerStoreId = 1, markerLocation = editLocation))
        assertNull(empty.resolveStoreDetailLocation(action(37.0, null)))
    }

    @Test
    fun legacyMapRemainsSupportedWhenEditMapIsAbsent() {
        val legacy = screen.copy(sections = screen.sections.filterIsInstance<StoreDetailSectionModel.Map>())
        assertEquals(legacyLocation, legacy.resolveStoreDetailLocation())
    }

    private fun action(latitude: Double?, longitude: Double?) = SDCustomActionModel(
        "STORE_EDIT_SECTION_MAP_ENLARGE",
        buildMap {
            latitude?.let { put("LATITUDE", SDClickLogValue.DoubleValue(it)) }
            longitude?.let { put("LONGITUDE", SDClickLogValue.DoubleValue(it)) }
        },
    )
}
