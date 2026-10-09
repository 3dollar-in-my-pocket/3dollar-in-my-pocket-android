package com.zion830.threedollars.ui.storeDetail.contributor.model

import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.serverdriven.model.SDLinkModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StoreContributorAnalyticsTest {

    // TH-1435 TC16
    @Test
    fun `TH1435_TC16_서버로그가_없을때_수정버튼_정적클릭로그에_store_id를_담는다`() {
        // Given
        val storeId = "139"

        // When
        val event = createStoreContributorEditClickEvent(storeId)

        // Then
        assertEquals(ScreenName.STORE_CONTRIBUTORS, event.screen)
        assertEquals(LogObjectType.BUTTON.value, event.extraParameters[ParameterName.OBJECT_TYPE])
        assertEquals(LogObjectId.EDIT.value, event.extraParameters[ParameterName.OBJECT_ID])
        assertEquals(storeId, event.extraParameters[ParameterName.STORE_ID])
    }

    @Test
    fun isStoreUpdateAction_returnsTrueOnlyForStoreUpdateAppScheme() {
        assertTrue(
            SDLinkModel(
                type = "APP_SCHEME",
                link = "threedollars://store/storeUpdate?storeId=1",
            ).isStoreUpdateAction()
        )
        assertFalse(
            SDLinkModel(
                type = "WEB",
                link = "https://example.com/storeUpdate?storeId=1",
            ).isStoreUpdateAction()
        )
        assertFalse(
            SDLinkModel(
                type = "APP_SCHEME",
                link = "threedollars://store/otherPath?storeId=1",
            ).isStoreUpdateAction()
        )
    }
}
