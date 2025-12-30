package com.zion830.threedollars.ui.community.data

import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.PollItem

sealed class PollListData {
    class Poll(val pollItem: PollItem) : PollListData()
    class Ad(val advertisementModelV2: AdvertisementModelV2) : PollListData()
}
