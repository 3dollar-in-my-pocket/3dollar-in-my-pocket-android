package com.threedollar.common.analytics

data class CustomEvent(
    override val screen: ScreenName,
    override val name: EventName,
    override val extraParameters: Map<ParameterName, Any>? = null
) : LogEvent
