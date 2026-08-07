package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.AdSize
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeListAdMobConfigTest {

    @Test
    fun `home list AdMob uses banner size`() {
        assertEquals(AdSize.BANNER.width, HomeListAdMobConfig.adSize.width)
        assertEquals(AdSize.BANNER.height, HomeListAdMobConfig.adSize.height)
        assertEquals(50.dp, HomeListAdMobConfig.containerHeight)
    }
}
