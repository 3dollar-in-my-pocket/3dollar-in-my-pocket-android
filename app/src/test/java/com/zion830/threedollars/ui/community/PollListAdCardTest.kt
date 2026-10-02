package com.zion830.threedollars.ui.community

import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.PollItem
import com.zion830.threedollars.ui.community.data.PollListData
import com.zion830.threedollars.ui.community.data.withAdCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class PollListAdCardTest {

    // TH-869 TC1
    @Test
    fun `TH869_TC1_서버광고가_없으면_두번째칸에_AdMob카드가_들어간다`() {
        // Given
        val polls = polls(3)

        // When
        val result = polls.withAdCard(advertisement = null)

        // Then
        assertEquals(4, result.size)
        assertSame(PollListData.AdMob, result[1])
        assertEquals(1, result.count { it is PollListData.AdMob || it is PollListData.Ad })
    }

    // TH-869 TC2
    @Test
    fun `TH869_TC2_서버광고가_있으면_exposureIndex위치에_서버광고카드만_들어간다`() {
        // Given
        val polls = polls(4)
        val advertisement = advertisement(exposureIndex = 3)

        // When
        val result = polls.withAdCard(advertisement)

        // Then
        assertEquals(5, result.size)
        assertSame(advertisement, (result[3] as PollListData.Ad).advertisementModelV2)
        assertTrue(result.none { it is PollListData.AdMob })
    }

    // TH-869 TC3
    @Test
    fun `TH869_TC3_투표목록이_비어있으면_광고카드를_넣지_않는다`() {
        // Given
        val polls = emptyList<PollListData.Poll>()

        // When
        val withoutServerAd = polls.withAdCard(advertisement = null)
        val withServerAd = polls.withAdCard(advertisement(exposureIndex = 1))

        // Then
        assertTrue(withoutServerAd.isEmpty())
        assertTrue(withServerAd.isEmpty())
    }

    // TH-869 TC6
    @Test
    fun `TH869_TC6_목록을_다시_만들어도_AdMob카드는_같은_항목으로_유지된다`() {
        // Given
        val first = polls(3).withAdCard(advertisement = null)

        // When
        val refreshed = polls(3).withAdCard(advertisement = null)

        // Then
        assertEquals(first.indexOf(PollListData.AdMob), refreshed.indexOf(PollListData.AdMob))
        assertEquals(first[1], refreshed[1])
    }

    // TH-869 TC1
    @Test
    fun `TH869_TC1_투표가_하나뿐이면_AdMob카드를_끝에_붙인다`() {
        // Given
        val polls = polls(1)

        // When
        val result = polls.withAdCard(advertisement = null)

        // Then
        assertEquals(2, result.size)
        assertSame(PollListData.AdMob, result.last())
    }

    // TH-869 TC2
    @Test
    fun `TH869_TC2_exposureIndex가_범위를_벗어나면_가까운_끝에_서버광고를_넣는다`() {
        // Given
        val polls = polls(2)

        // When
        val overflow = polls.withAdCard(advertisement(exposureIndex = 10))
        val negative = polls.withAdCard(advertisement(exposureIndex = -1))

        // Then
        assertTrue(overflow.last() is PollListData.Ad)
        assertTrue(negative.first() is PollListData.Ad)
    }

    private fun polls(count: Int) = (1..count).map { PollListData.Poll(pollItem(pollId = it.toString())) }

    private fun advertisement(exposureIndex: Int) = AdvertisementModelV2(
        advertisementId = 1,
        background = AdvertisementModelV2.Background(color = "#FFFFFF"),
        extra = AdvertisementModelV2.Extra(content = "", fontColor = ""),
        image = AdvertisementModelV2.Image(height = 0, url = "", width = 0),
        link = AdvertisementModelV2.Link(type = "WEB", url = ""),
        metadata = AdvertisementModelV2.MetaData(exposureIndex = exposureIndex),
        subTitle = AdvertisementModelV2.SubTitle(content = "", fontColor = ""),
        title = AdvertisementModelV2.Title(content = "", fontColor = ""),
    )

    private fun pollItem(pollId: String) = PollItem(
        meta = PollItem.Meta(totalCommentsCount = 0, totalParticipantsCount = 0),
        poll = PollItem.Poll(
            category = PollItem.Poll.Category(categoryId = "", content = "", title = ""),
            content = PollItem.Poll.Content(title = ""),
            createdAt = "",
            isOwner = false,
            options = emptyList(),
            period = PollItem.Poll.Period(endDateTime = "", startDateTime = ""),
            pollId = pollId,
            updatedAt = "",
        ),
        pollWriter = PollItem.PollWriter(
            createdAt = "",
            medal = PollItem.PollWriter.Medal(
                acquisition = PollItem.PollWriter.Medal.Acquisition(description = ""),
                createdAt = "",
                disableIconUrl = "",
                iconUrl = "",
                introduction = "",
                medalId = 0,
                name = "",
                updatedAt = "",
            ),
            medalsCount = 0,
            name = "",
            socialType = "",
            updatedAt = "",
            userId = 0,
        ),
    )
}
