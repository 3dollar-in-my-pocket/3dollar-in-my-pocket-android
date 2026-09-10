package com.zion830.threedollars.ui.storeDetail.v2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import base.compose.Gray10
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray100
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailDetailRowModel
import com.threedollar.common.serverdriven.model.StoreDetailInformationRowModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.zion830.threedollars.core.ui.serverdriven.SDChipRenderer
import com.zion830.threedollars.core.ui.serverdriven.SDTextRenderer
import com.zion830.threedollars.core.ui.serverdriven.serverDrivenSurface

@Composable
internal fun StoreDetailInfoV1Section(section: StoreDetailSectionModel.InfoV1, onAction: (StoreActionBarModel) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        section.informationCard?.let { card ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                card.rows.forEach { row ->
                    when (row) {
                        is StoreDetailInformationRowModel.ChipGroup -> StoreDetailLabelRow(row.label) {
                            StoreDetailChipRow(row.chips)
                        }
                        is StoreDetailInformationRowModel.InlineOption -> StoreDetailLabelRow(row.label) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                row.items.forEach { item ->
                                    SDTextRenderer(
                                        text = item.text,
                                        color = if (item.isSelected) Gray100 else Gray50,
                                        fontSizeDp = 13,
                                        lineHeightDp = 19,
                                    )
                                }
                            }
                        }
                        is StoreDetailInformationRowModel.TrailingText -> StoreDetailLabelValueRow(row.label, row.value)
                    }
                }
            }
        }
        section.menuCard?.let { card ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                card.groups.forEach { group ->
                    SDChipRenderer(group.header)
                    group.items.forEach { item -> StoreDetailLabelValueRow(item.primaryText, item.secondaryText) }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailInfoV2Section(
    section: StoreDetailSectionModel.InfoV2,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        section.imageGallery?.images?.takeIf { it.isNotEmpty() }?.let { images ->
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                images.forEach { image ->
                    StoreDetailImage(
                        image = image,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        defaultWidthDp = 120.0,
                        defaultHeightDp = 120.0,
                    )
                }
            }
        }
        section.detailCard?.let { card ->
            Column(Modifier.fillMaxWidth().serverDrivenSurface(card.style, RectangleShape, Color.Transparent)) {
                card.rows.forEach { row ->
                    when (row) {
                        is StoreDetailDetailRowModel.Link -> Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable {
                                onAction(
                                    syntheticActionBar(
                                        button = SDButtonModel(text = row.value, link = row.link),
                                        type = "INFO_LINK",
                                    )
                                )
                            }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            SDTextRenderer(row.label, color = Gray50, fontSizeDp = 13, lineHeightDp = 19)
                            SDTextRenderer(row.value, color = Gray100, fontSizeDp = 13, lineHeightDp = 19, modifier = Modifier.weight(1f))
                        }
                        is StoreDetailDetailRowModel.Text -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            SDTextRenderer(row.title, color = Gray100, fontSizeDp = 14, lineHeightDp = 20)
                            SDTextRenderer(row.body, color = Gray70, fontSizeDp = 13, lineHeightDp = 19)
                        }
                    }
                }
            }
        }
        section.accountCards.forEach { card ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SDTextRenderer(card.title, color = Gray50, fontSizeDp = 12, lineHeightDp = 18)
                    SDChipRenderer(card.account)
                }
                StoreDetailTextButton(card.copyButton) {
                    // 복사 전용 값이 없는 계약이므로 예금주를 제외한 account.text만 전달한다.
                    onAction(syntheticActionBar(card.copyButton.copy(text = card.account.text), STORE_DETAIL_ACCOUNT_COPY_ACTION))
                }
            }
        }
        section.menuListCard?.let { card ->
            Column(
                Modifier.fillMaxWidth().serverDrivenSurface(card.style, RectangleShape, Color.Transparent),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                card.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item.image?.let {
                            StoreDetailImage(it, Modifier.clip(RoundedCornerShape(8.dp)), ContentScale.Crop, 72.0, 72.0)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            SDTextRenderer(item.primaryText, color = Gray100, fontSizeDp = 14, lineHeightDp = 20)
                            item.secondaryText?.let { text ->
                                SDTextRenderer(text, color = Gray50, fontSizeDp = 12, lineHeightDp = 18)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoreDetailLabelRow(
    label: SDTextModel,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SDTextRenderer(label, color = Gray50, fontSizeDp = 12, lineHeightDp = 18)
        content()
    }
}

@Composable
private fun StoreDetailLabelValueRow(label: SDTextModel, value: SDTextModel?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SDTextRenderer(label, color = Gray100, fontSizeDp = 14, lineHeightDp = 20, modifier = Modifier.weight(1f))
        value?.let { SDTextRenderer(it, color = Gray70, fontSizeDp = 13, lineHeightDp = 19) }
    }
}
