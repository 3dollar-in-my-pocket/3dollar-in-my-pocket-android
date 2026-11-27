package com.threedollar.data.screen

import com.threedollar.common.serverdriven.model.BottomNavigationTabModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.network.data.screen.BottomNavigationResponse
import com.threedollar.network.data.screen.BottomNavigationTabResponse

/** id 가 없는 탭은 앱이 고를 수 없으므로 뺀다. 나머지 필터링(모르는 type·id)은 앱이 한다. */
fun BottomNavigationResponse.asTabModels(): List<BottomNavigationTabModel> =
    tabs.orEmpty().mapNotNull { it.asModelOrNull() }

private fun BottomNavigationTabResponse.asModelOrNull(): BottomNavigationTabModel? {
    val tabId = id?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return BottomNavigationTabModel(
        id = tabId,
        label = label.orEmpty(),
        iconUrl = icon?.url?.takeIf { it.isNotBlank() },
        selectedTintColor = selectedTintColor?.takeIf { it.isNotBlank() },
        type = type?.takeIf { it.isNotBlank() },
        link = link?.let { SDLinkModel(type = it.type.orEmpty(), link = it.link.orEmpty()) },
        clickLog = clickLog?.asModel()?.takeIf { it.screenName.isNotBlank() },
    )
}
