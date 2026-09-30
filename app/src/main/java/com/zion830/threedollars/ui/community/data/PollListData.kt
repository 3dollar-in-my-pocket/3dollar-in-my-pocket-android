package com.zion830.threedollars.ui.community.data

import com.threedollar.domain.community.data.AdvertisementModelV2
import com.threedollar.domain.community.data.PollItem

sealed class PollListData {
    class Poll(val pollItem: PollItem) : PollListData()
    class Ad(val advertisementModelV2: AdvertisementModelV2) : PollListData()

    /** 서버 광고가 없을 때 같은 자리에 들어가는 AdMob 배너 카드. 한 목록에 하나뿐이라 동일성으로 재바인딩을 막는다. */
    data object AdMob : PollListData()
}

/** 서버 광고가 없을 때 광고 카드가 들어가는 위치(두 번째 칸). */
private const val DEFAULT_AD_INDEX = 1

/**
 * 투표 목록에 광고 카드를 항상 하나 끼워 넣는다.
 * 서버 광고가 있으면 그 `exposureIndex` 위치에 서버 광고 카드를, 없으면 두 번째 칸에 AdMob 카드를 넣는다.
 * 위치가 범위를 벗어나면 가까운 끝으로 붙이고, 투표가 없으면 광고도 넣지 않는다.
 */
fun List<PollListData.Poll>.withAdCard(advertisement: AdvertisementModelV2?): List<PollListData> {
    if (isEmpty()) return this
    val adCard = advertisement?.let { PollListData.Ad(it) } ?: PollListData.AdMob
    val targetIndex = (advertisement?.let { it.metadata.exposureIndex } ?: DEFAULT_AD_INDEX).coerceIn(0, size)
    return toMutableList<PollListData>().apply { add(targetIndex, adCard) }
}
