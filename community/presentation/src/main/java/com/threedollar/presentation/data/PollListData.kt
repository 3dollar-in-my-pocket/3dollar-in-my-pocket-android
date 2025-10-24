package com.threedollar.presentation.data

import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.PollItem

sealed class PollListData {
    class Poll(val pollItem: PollItem) : PollListData()
    class Ad(val advertisementModelV2: AdvertisementModelV2) : PollListData()
}
