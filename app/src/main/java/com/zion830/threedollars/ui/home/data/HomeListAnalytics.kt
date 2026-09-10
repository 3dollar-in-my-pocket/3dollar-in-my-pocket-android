package com.zion830.threedollars.ui.home.data

import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel

internal fun List<HomeListCardModel>.homeImpressionLogs(): List<SDImpressionLogModel> = mapNotNull { card ->
    when (card) {
        is HomeListCardModel.BasicCard -> card.impressionLog
        is HomeListCardModel.EmptyCard -> card.impressionLog
        is HomeListCardModel.AdMobCard -> card.impressionLog
    }
}
