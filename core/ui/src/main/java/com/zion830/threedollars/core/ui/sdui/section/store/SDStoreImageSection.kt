package com.zion830.threedollars.core.ui.sdui.section.store

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray50
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.threedollar.common.R as CommonR
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.element.SDImage
import com.zion830.threedollars.core.ui.sdui.element.SDText
import com.zion830.threedollars.core.ui.sdui.foundation.sdClickable
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

object SDStoreImageSectionDefaults {
    val CardSpacing = 8.dp
    val CardShape = SDSectionDefaults.ImageShape
    val CardTextSpacing = 2.dp
    const val DEFAULT_CARD_SIZE = 96f
}

/**
 * 가게 상세 섹션이 공유하는 빈 상태 뷰(회색 배경 + 아이콘 + 안내 문구).
 */
object SDStoreSectionEmptyDefaults {
    val Height = 78.dp
    val IconSize = 48.dp
    val Shape = RoundedCornerShape(6.dp)
    val Spacing = 2.dp
}

/**
 * IMAGE 섹션: 가게 사진 카드 가로 목록. 카드 크기는 서버 `image.style` 을 따르고,
 * `dimmed` 카드에는 가운데 제목·부제를 얹는다. 사진이 없으면 빈 상태 뷰를 보여준다.
 */
@Composable
fun SDStoreImageSection(
    model: SDStoreImageSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        SDSectionHeader(model = model.header, onAction = onAction)
        val cards = model.cards.orEmpty()
        if (cards.isEmpty()) {
            SDStoreSectionEmptyView(
                text = stringResource(CommonR.string.photo_empty),
                modifier = Modifier.padding(horizontal = SDSectionDefaults.HorizontalPadding)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = SDSectionDefaults.HorizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(SDStoreImageSectionDefaults.CardSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(cards) { card -> ImageCard(card = card, onAction = onAction) }
            }
        }
    }
}

@Composable
private fun ImageCard(
    card: SDStoreImageSectionModel.Card,
    onAction: (SDActionEvent) -> Unit,
) {
    val width = card.image?.style?.width ?: SDStoreImageSectionDefaults.DEFAULT_CARD_SIZE
    val height = card.image?.style?.height ?: SDStoreImageSectionDefaults.DEFAULT_CARD_SIZE
    val event = SDActionEvent(link = card.link, customAction = card.customAction, clickLog = card.clickLog)
    Box(
        modifier = Modifier
            .size(width = width.dp, height = height.dp)
            .sdSurface(style = card.style, shape = SDStoreImageSectionDefaults.CardShape)
            .sdClickable(event = event, onAction = onAction),
        contentAlignment = Alignment.Center
    ) {
        card.image?.let { SDImage(model = it, sizeFromStyle = false, modifier = Modifier.matchParentSize()) }
        if (card.title != null || card.subTitle != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SDStoreImageSectionDefaults.CardTextSpacing)
            ) {
                card.title?.let {
                    SDText(model = it, fontSize = dpToSp(13), fontWeight = FontWeight.W700, textAlign = TextAlign.Center)
                }
                card.subTitle?.let {
                    SDText(model = it, fontSize = dpToSp(11), fontWeight = FontWeight.W500, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

/**
 * 사진·리뷰가 없을 때 보여주는 빈 상태 뷰. 문구만 클라이언트가 정한다.
 */
@Composable
internal fun SDStoreSectionEmptyView(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SDStoreSectionEmptyDefaults.Height)
            .background(color = Gray10, shape = SDStoreSectionEmptyDefaults.Shape),
        horizontalArrangement = Arrangement.spacedBy(SDStoreSectionEmptyDefaults.Spacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(DesignSystemR.drawable.ic_photo_review_empty),
            contentDescription = null,
            modifier = Modifier.size(SDStoreSectionEmptyDefaults.IconSize)
        )
        Text(
            text = text,
            color = Gray50,
            fontSize = dpToSp(12),
            fontWeight = FontWeight.W500,
            fontFamily = PretendardFontFamily
        )
    }
}
