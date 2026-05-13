package com.threedollar.data.home

import com.threedollar.network.data.store.AroundStoreResponse
import com.threedollar.network.data.store.Content
import com.threedollar.network.data.store.Marker
import com.threedollar.network.data.store.StoreMarkerImageResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class HomeMapperTest {

    @Test
    fun aroundStoreMapper_mapsContentMarkerImages() {
        val response = AroundStoreResponse(
            contents = listOf(
                Content(
                    marker = Marker(
                        selected = StoreMarkerImageResponse(
                            imageUrl = "https://example.com/marker-selected.png",
                            width = 48,
                            height = 56,
                        ),
                        unSelected = StoreMarkerImageResponse(
                            imageUrl = "https://example.com/marker-unselected.png",
                            width = 36,
                            height = 44,
                        ),
                    ),
                ),
            ),
            cursor = null,
        )

        val markerModel = response.asModel().contentModels.single().markerModel

        assertNotNull(markerModel)
        requireNotNull(markerModel)
        assertEquals("https://example.com/marker-selected.png", markerModel.selected.imageUrl)
        assertEquals(48, markerModel.selected.width)
        assertEquals(56, markerModel.selected.height)
        assertEquals("https://example.com/marker-unselected.png", markerModel.unSelected.imageUrl)
        assertEquals(36, markerModel.unSelected.width)
        assertEquals(44, markerModel.unSelected.height)
    }

    @Test
    fun aroundStoreMapper_keepsMarkerNullWhenContentMarkerIsNull() {
        val response = AroundStoreResponse(
            contents = listOf(Content(marker = null)),
            cursor = null,
        )

        val markerModel = response.asModel().contentModels.single().markerModel

        assertNull(markerModel)
    }
}
