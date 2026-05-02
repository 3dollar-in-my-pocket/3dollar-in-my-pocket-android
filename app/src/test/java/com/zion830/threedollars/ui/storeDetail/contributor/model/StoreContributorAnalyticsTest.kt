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

    @Test
    fun createStoreContributorEditClickEvent_returnsExpectedAnalyticsContract() {
        val event = createStoreContributorEditClickEvent()

        assertEquals(ScreenName.STORE_CONTRIBUTORS, event.screen)
        assertEquals(LogObjectType.BUTTON.value, event.extraParameters[ParameterName.OBJECT_TYPE])
        assertEquals(LogObjectId.EDIT.value, event.extraParameters[ParameterName.OBJECT_ID])
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
