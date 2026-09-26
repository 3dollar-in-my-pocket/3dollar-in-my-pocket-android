package com.zion830.threedollars.core.ui.sdui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer
import com.zion830.threedollars.core.ui.sdui.component.SDHeader
import com.zion830.threedollars.core.ui.sdui.component.card.SDImagePreviewCard

@Composable
fun SDRelatedStoresSection(
    model: SDRelatedStoresSectionModel,
    onCardPressed: (ImagePreviewCardModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        model.header?.let {
            SDHeader(
                it,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .wrapContentSize()
            )
        }

        VerticalSpacer(12)

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(model.cards) {
                when (it) {
                    is ImagePreviewCardModel -> {
                        SDImagePreviewCard(it) { pressed ->
                            onCardPressed.invoke(pressed)
                        }
                    }
                }
            }
        }
    }
}
