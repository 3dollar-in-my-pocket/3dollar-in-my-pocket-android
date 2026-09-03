package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.StoreActionBarModel

internal enum class StoreDetailActionIconRole {
    Copy,
    Zoom,
    Edit,
    Report,
}

internal fun StoreActionBarModel.localIconRoleOrNull(): StoreDetailActionIconRole? =
    when (button.customAction?.actionType) {
        "STORE_MAP_SECTION_COPY_ADDRESS" -> StoreDetailActionIconRole.Copy
        "STORE_MAP_SECTION_MAP_ENLARGE" -> StoreDetailActionIconRole.Zoom
        "STORE_EDIT_SECTION_UPDATE" -> StoreDetailActionIconRole.Edit
        "STORE_EDIT_SECTION_REPORT" -> StoreDetailActionIconRole.Report
        else -> null
    }

internal enum class StoreDetailAdLoadState(val shouldRender: Boolean) {
    Loading(shouldRender = true),
    Loaded(shouldRender = true),
    Failed(shouldRender = false),
}
