package com.threedollar.common.analytics

interface LogEvent {
    val screen: ScreenName
    val name: EventName
    val extraParameters: Map<ParameterName, Any>?
}
