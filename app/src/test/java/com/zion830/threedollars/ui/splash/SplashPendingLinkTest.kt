package com.zion830.threedollars.ui.splash

import com.zion830.threedollars.ui.splash.ui.SplashPendingLink
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SplashPendingLinkTest {

    private val kakaoStoreTypes = setOf("foodTruck", "streetFood")

    // TH-1602 TC1
    @Test
    fun `TH1602_TC1_카카오공유_가게링크는_로그인뒤_열_가게상세_링크로_바뀐다`() {
        // Given
        val storeType = "streetFood"
        val storeId = "527060"

        // When
        val link = SplashPendingLink.resolve(pushLink = null, storeType = storeType, storeId = storeId, kakaoStoreTypes = kakaoStoreTypes)

        // Then
        assertEquals("dollars://stores/527060", link)
        assertEquals("527060", StoreSectionFragment.parseLink(link)?.storeId)
    }

    // TH-1602 TC2
    @Test
    fun `TH1602_TC2_백그라운드푸시_링크는_그대로_넘기고_앱링크가_아니면_버린다`() {
        // When
        val push = SplashPendingLink.resolve(pushLink = "dollars-dev://myCoupons", storeType = null, storeId = null, kakaoStoreTypes = kakaoStoreTypes)
        val notAppLink = SplashPendingLink.resolve(pushLink = "https://example.com", storeType = null, storeId = null, kakaoStoreTypes = kakaoStoreTypes)

        // Then
        assertEquals("dollars-dev://myCoupons", push)
        assertNull(notAppLink)
    }

    // TH-1602 TC3
    @Test
    fun `TH1602_TC3_카카오공유인데_storeId가_없거나_숫자가_아니면_이어갈_링크가_없다`() {
        // When
        val blank = SplashPendingLink.resolve(pushLink = null, storeType = "foodTruck", storeId = "", kakaoStoreTypes = kakaoStoreTypes)
        val invalid = SplashPendingLink.resolve(pushLink = null, storeType = "foodTruck", storeId = "abc", kakaoStoreTypes = kakaoStoreTypes)

        // Then
        assertNull(blank)
        assertNull(invalid)
    }

    // TH-1602 TC4
    @Test
    fun `TH1602_TC4_카카오가게타입이_있으면_tryLogin과_같이_푸시링크보다_먼저다`() {
        // When
        val link = SplashPendingLink.resolve(pushLink = "dollars-dev://myCoupons", storeType = "foodTruck", storeId = "1", kakaoStoreTypes = kakaoStoreTypes)

        // Then
        assertEquals("dollars://stores/1", link)
    }
}
