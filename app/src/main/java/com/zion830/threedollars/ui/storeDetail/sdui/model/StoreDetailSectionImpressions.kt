package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel

object StoreDetailSectionImpressions {
    fun logs(key: String, section: SDSectionModel): List<Pair<String, SDLogModel>> = buildList {
        val logKey = section.sectionId?.takeIf { it.isNotBlank() } ?: key
        section.impressionLog?.let { add(logKey to it) }
        if (section is SDStoreAdmobSectionModel) {
            section.cards?.firstOrNull()?.impressionLog?.let { add("$logKey-admob-card" to it) }
        }
    }
}
