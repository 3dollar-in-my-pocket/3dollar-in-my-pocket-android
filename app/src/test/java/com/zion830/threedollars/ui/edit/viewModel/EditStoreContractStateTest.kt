package com.zion830.threedollars.ui.edit.viewModel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EditStoreContractStateTest {

    @Test
    fun pendingPhotos_existThenStateCountsPhotoChanges() {
        val state = EditStoreContract.State(
            photoCount = 2,
            pendingPhotos = listOf(
                EditStoreContract.PendingPhoto(
                    id = "pending-1",
                    uriString = "content://photo/1",
                    cachedFilePath = "/tmp/photo-1.png",
                    displayName = "photo-1.png",
                )
            )
        )

        assertTrue(state.hasPhotoChanges)
        assertTrue(state.hasAnyChanges)
        assertEquals(1, state.pendingPhotoCount)
        assertEquals(3, state.totalPhotoCount)
    }

    @Test
    fun pendingPhotos_existThenChangedCountIncludesPhotoSection() {
        val state = EditStoreContract.State(
            hasLocationChanges = true,
            pendingPhotos = listOf(
                EditStoreContract.PendingPhoto(
                    id = "pending-1",
                    uriString = "content://photo/1",
                    cachedFilePath = "/tmp/photo-1.png",
                    displayName = "photo-1.png",
                )
            )
        )

        assertEquals(2, state.totalChangedCount)
    }
}
