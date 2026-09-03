package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreDetailV2PresentationPolicyTest {

    @Test
    fun `live map and edit actions select local fallback icon roles`() {
        assertEquals(StoreDetailActionIconRole.Copy, action("STORE_MAP_SECTION_COPY_ADDRESS").localIconRoleOrNull())
        assertEquals(StoreDetailActionIconRole.Zoom, action("STORE_MAP_SECTION_MAP_ENLARGE").localIconRoleOrNull())
        assertEquals(StoreDetailActionIconRole.Edit, action("STORE_EDIT_SECTION_UPDATE").localIconRoleOrNull())
        assertEquals(StoreDetailActionIconRole.Report, action("STORE_EDIT_SECTION_REPORT").localIconRoleOrNull())
        assertNull(action("STORE_REVIEW_SECTION_REVIEW_WRITE").localIconRoleOrNull())
    }

    @Test
    fun `failed ad slot is removed while loading and loaded slots remain`() {
        assertTrue(StoreDetailAdLoadState.Loading.shouldRender)
        assertTrue(StoreDetailAdLoadState.Loaded.shouldRender)
        assertFalse(StoreDetailAdLoadState.Failed.shouldRender)
    }

    private fun action(actionType: String) = StoreActionBarModel(
        type = "ACTION_BAR",
        button = SDButtonModel(
            text = SDTextModel(text = "button", isHtml = false),
            customAction = SDCustomActionModel(actionType = actionType),
        ),
    )
}
