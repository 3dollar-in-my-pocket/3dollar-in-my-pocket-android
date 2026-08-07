package com.zion830.threedollars.ui.storeDetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import base.compose.Gray0
import com.threedollar.network.sdui.model.component.ImagePreviewCardModel
import com.threedollar.network.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.network.sdui.ui.section.SDRelatedStoresSection
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer

@Composable
fun StoreDetailRelatedStoresSection(
    section: SDRelatedStoresSectionModel,
    onCardPressed: (ImagePreviewCardModel, SDRelatedStoresSectionModel.Reference?) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Gray0)
        )

        VerticalSpacer(16)

        SDRelatedStoresSection(
            model = section,
            onCardPressed = { card ->
                onCardPressed.invoke(card, section.reference?.firstOrNull())
            }
        )
    }
}
