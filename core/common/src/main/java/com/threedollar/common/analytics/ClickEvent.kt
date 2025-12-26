package com.threedollar.common.analytics

data class ClickEvent(
    override val screen: ScreenName,
    val objectType: LogObjectType,
    val objectId: LogObjectId,
    private val additionalParams: Map<ParameterName, Any>? = null
) : LogEvent {
    override val name: EventName = EventName.Click

    override val extraParameters: Map<ParameterName, Any>
        get() {
            val params = mutableMapOf<ParameterName, Any>(
                ParameterName.OBJECT_ID to objectId.value,
                ParameterName.OBJECT_TYPE to objectType.value
            )
            additionalParams?.let { additional ->
                additional.forEach { (key, value) ->
                    params[key] = value
                }
            }
            return params
        }
}
