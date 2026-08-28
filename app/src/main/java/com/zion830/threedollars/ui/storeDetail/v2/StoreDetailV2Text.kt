package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.SDTextModel

internal fun SDTextModel.withoutMissingContributorName(): SDTextModel {
    val plainText = displayText()
    if (!plainText.startsWith("null님이 ")) return this
    return copy(
        text = plainText.removePrefix("null님이 "),
        isHtml = false,
    )
}
