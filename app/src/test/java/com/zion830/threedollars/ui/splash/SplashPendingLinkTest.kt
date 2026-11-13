package com.zion830.threedollars.ui.splash

import com.zion830.threedollars.ui.splash.ui.SplashPendingLink
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SplashPendingLinkTest {

    // TH-1602 TC1
    @Test
    fun `TH1602_TC1_카카오공유_가게링크는_storeType이_없어도_로그인뒤_열_가게상세_링크로_바뀐다`() {
        // Given (iOS·현재 공유 링크는 storeId 만 보낸다)
        val kakaoStoreId = "527060"

        // When
        val link = SplashPendingLink.resolve(pushLink = null, kakaoStoreId = kakaoStoreId)

        // Then
        assertEquals("dollars://stores/527060", link)
        assertEquals("527060", StoreSectionFragment.parseLink(link)?.storeId)
    }

    // TH-1602 TC2
    @Test
    fun `TH1602_TC2_백그라운드푸시_링크는_그대로_넘기고_앱링크가_아니면_버린다`() {
        // When
        val push = SplashPendingLink.resolve(pushLink = "dollars-dev://myCoupons", kakaoStoreId = null)
        val notAppLink = SplashPendingLink.resolve(pushLink = "https://example.com", kakaoStoreId = null)

        // Then
        assertEquals("dollars-dev://myCoupons", push)
        assertNull(notAppLink)
    }

    // TH-1602 TC3
    @Test
    fun `TH1602_TC3_카카오공유인데_storeId가_비었거나_숫자가_아니면_이어갈_링크가_없다`() {
        // When
        val blank = SplashPendingLink.resolve(pushLink = null, kakaoStoreId = "")
        val invalid = SplashPendingLink.resolve(pushLink = null, kakaoStoreId = "abc")

        // Then
        assertNull(blank)
        assertNull(invalid)
    }

    // TH-1602 TC4
    @Test
    fun `TH1602_TC4_카카오진입이면_tryLogin과_같이_푸시링크보다_먼저다`() {
        // When
        val link = SplashPendingLink.resolve(pushLink = "dollars-dev://myCoupons", kakaoStoreId = "1")

        // Then
        assertEquals("dollars://stores/1", link)
    }
}
