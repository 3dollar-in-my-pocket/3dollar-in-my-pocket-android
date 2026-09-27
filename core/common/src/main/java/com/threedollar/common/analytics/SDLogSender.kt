package com.threedollar.common.analytics

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.SDViewLogModel

/**
 * 가게 상세 SDUI 로그([SDLogModel])를 [SDClickLogger]로 보낸다. 서버 값을 가공하지 않고 그대로 전송한다.
 */
object SDLogSender {

    fun sendClick(log: SDLogModel?) {
        log ?: return
        SDClickLogger.send(
            SDClickLogModel(
                eventType = log.eventType.orEmpty(),
                screenName = log.screenName.orEmpty(),
                objectType = log.objectType.orEmpty(),
                objectId = log.objectId.orEmpty(),
                extraParameters = log.extraParameters.toLogValues()
            )
        )
    }

    fun sendImpression(log: SDLogModel?) {
        log ?: return
        SDClickLogger.send(
            SDImpressionLogModel(
                eventType = log.eventType.orEmpty(),
                screenName = log.screenName.orEmpty(),
                objectType = log.objectType.orEmpty(),
                objectId = log.objectId.orEmpty(),
                extraParameters = log.extraParameters.toLogValues()
            )
        )
    }

    fun sendPageView(log: SDLogModel?) {
        val screenName = log?.screenName?.takeIf { it.isNotBlank() } ?: return
        SDClickLogger.send(
            SDViewLogModel(
                screenName = screenName,
                eventType = log.eventType.orEmpty(),
                objectType = log.objectType.orEmpty(),
                objectId = log.objectId.orEmpty(),
                extraParameters = log.extraParameters.toLogValues()
            )
        )
    }

    private fun Map<String, Any?>?.toLogValues(): Map<String, SDClickLogValue> =
        orEmpty().mapValues { (_, value) ->
            when (value) {
                null -> SDClickLogValue.Null
                is String -> SDClickLogValue.StringValue(value)
                is Boolean -> SDClickLogValue.BoolValue(value)
                is Int -> SDClickLogValue.IntValue(value)
                is Long -> SDClickLogValue.LongValue(value)
                is Number -> SDClickLogValue.DoubleValue(value.toDouble())
                else -> SDClickLogValue.StringValue(value.toString())
            }
        }
}
