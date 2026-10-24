package com.threedollar.common.sdui.model.element

import com.threedollar.common.sdui.model.component.SDAdCardModel

/**
 * 렌더러가 화면에 올려보내는 탭 이벤트. 링크와 커스텀 액션 중 하나 이상을 가진다.
 */
data class SDActionEvent(
    val link: SDLink? = null,
    val customAction: SDCustomActionModel? = null,
    val clickLog: SDLogModel? = null
)

fun SDAdCardModel.toActionEvent(): SDActionEvent = SDActionEvent(link = link, clickLog = clickLog)

fun SDButtonModel.toActionEvent(fallbackLog: SDLogModel? = null): SDActionEvent =
    SDActionEvent(link = link, customAction = customAction, clickLog = clickLog ?: fallbackLog)

fun SDActionBarModel.toActionEvent(): SDActionEvent? =
    button?.toActionEvent(fallbackLog = clickLog)
