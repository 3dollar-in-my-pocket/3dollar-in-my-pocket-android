package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStoreThreadPostSectionModel

object StoreDetailSectionImpressions {
    fun logs(key: String, section: SDSectionModel): List<Pair<String, SDLogModel>> = buildList {
        if (section is SDStoreThreadPostSectionModel) return@buildList
        if (section is SDStoreAdSectionModel && section.firstAdCard == null) return@buildList
        val logKey = section.sectionId?.takeIf { it.isNotBlank() } ?: key
        section.impressionLog?.let { add(logKey to it) }
        if (section is SDStoreAdmobSectionModel) {
            section.cards?.firstOrNull()?.impressionLog?.let { add("$logKey-admob-card" to it) }
        }
        if (section is SDStoreAdSectionModel) {
            section.firstAdCard?.impressionLog?.let { add("$logKey-ad-card" to it) }
        }
    }
}
