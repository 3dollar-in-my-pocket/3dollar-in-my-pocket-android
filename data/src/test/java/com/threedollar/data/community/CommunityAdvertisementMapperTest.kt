package com.threedollar.data.community

import com.threedollar.data.community.mapper.asModel
import com.threedollar.network.data.advertisement.AdvertisementResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class CommunityAdvertisementMapperTest {

    // TH-869 TC11
    @Test
    fun `TH869_TC11_exposureIndex가_없으면_두번째칸_1로_매핑한다`() {
        // Given
        val withoutMetadata = AdvertisementResponse.Advertisement(advertisementId = 1, metadata = null)
        val withoutIndex = AdvertisementResponse.Advertisement(
            advertisementId = 2,
            metadata = AdvertisementResponse.Advertisement.MetaData(exposureIndex = null),
        )

        // When
        val models = listOf(withoutMetadata, withoutIndex).map { it.asModel() }

        // Then
        assertEquals(listOf(1, 1), models.map { it.metadata.exposureIndex })
    }

    // TH-869 TC2
    @Test
    fun `TH869_TC2_exposureIndex가_있으면_그대로_매핑한다`() {
        // Given
        val response = AdvertisementResponse.Advertisement(
            advertisementId = 1,
            metadata = AdvertisementResponse.Advertisement.MetaData(exposureIndex = 3),
        )

        // When
        val model = response.asModel()

        // Then
        assertEquals(3, model.metadata.exposureIndex)
    }
}
