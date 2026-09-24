package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.analytics.SDLogSender
import com.threedollar.common.sdui.model.element.SDLogModel
import javax.inject.Inject

/**
 * 가게 상세의 서버 로그 전송. 테스트에서 대체할 수 있도록 인터페이스로 둔다.
 */
interface StoreDetailSduiLogger {
    fun click(log: SDLogModel?)
    fun impression(log: SDLogModel?)
    fun pageView(log: SDLogModel?)
}

class DefaultStoreDetailSduiLogger @Inject constructor() : StoreDetailSduiLogger {
    override fun click(log: SDLogModel?) = SDLogSender.sendClick(log)
    override fun impression(log: SDLogModel?) = SDLogSender.sendImpression(log)
    override fun pageView(log: SDLogModel?) = SDLogSender.sendPageView(log)
}
