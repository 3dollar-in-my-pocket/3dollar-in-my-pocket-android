package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel

object StoreDetailSectionImpressions {
    fun logs(key: String, section: SDSectionModel): List<Pair<String, SDLogModel>> = buildList {
        section.impressionLog?.let { add(key to it) }
        if (section is SDStoreAdmobSectionModel) {
            section.cards?.firstOrNull()?.impressionLog?.let { add("$key-admob-card" to it) }
        }
    }
}
