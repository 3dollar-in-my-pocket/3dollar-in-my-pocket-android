package com.zion830.threedollars.ui.home.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import base.compose.ColorWhite
import base.compose.Gray30
import base.compose.Gray70
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.threedollar.common.compose.utils.toColor
import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.zion830.threedollars.ui.home.data.ChipAction
import com.zion830.threedollars.ui.home.data.HomeFilterCellType
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@Composable
fun HomeFilterChipsRow(
    cells: List<HomeFilterCellType>,
    onCategoryClick: () -> Unit,
    onRadioClick: (paramKey: String, optionIndex: Int) -> Unit,
    onActionClick: (SDLinkModel) -> Unit,
    onCloseSelectedCategoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = contentPadding,
    ) {
        items(
            items = cells,
            key = { it.stableHomeFilterKey() },
            contentType = { it.homeFilterContentType() },
        ) { cell ->
            HomeFilterCell(
                cell = cell,
                onCategoryClick = onCategoryClick,
                onRadioClick = onRadioClick,
                onActionClick = onActionClick,
                onCloseSelectedCategoryClick = onCloseSelectedCategoryClick,
            )
        }
    }
}

@Composable
private fun HomeFilterCell(
    cell: HomeFilterCellType,
    onCategoryClick: () -> Unit,
    onRadioClick: (paramKey: String, optionIndex: Int) -> Unit,
    onActionClick: (SDLinkModel) -> Unit,
    onCloseSelectedCategoryClick: () -> Unit,
) {
    when (cell) {
        is HomeFilterCellType.Chip -> HomeFilterActionChip(
            chip = cell.chip,
            onClick = {
                when (val action = cell.action) {
                    ChipAction.OpenCategoryFilter -> onCategoryClick()
                    is ChipAction.SelectRadio -> onRadioClick(action.paramKey, action.optionIndex)
                    is ChipAction.DeepLink -> onActionClick(action.link)
                }
            },
        )

        is HomeFilterCellType.SelectedCategoryChip -> HomeFilterSelectedCategoryChip(
            chip = cell.chip,
            current = cell.current,
            onCloseClick = onCloseSelectedCategoryClick,
        )

        is HomeFilterCellType.Button -> HomeFilterButtonChip(
            button = cell.button,
            onClick = {
                cell.button.link?.let(onActionClick)
            },
        )
    }
}

@Composable
private fun HomeFilterActionChip(
    chip: SDChipModel,
    onClick: () -> Unit,
) {
    HomeFilterChipSurface(
        style = chip.style,
        contentDescription = chip.accessibilityText(),
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    ) {
        HomeFilterChipContent(chip = chip)
    }
}

@Composable
private fun HomeFilterSelectedCategoryChip(
    chip: SDChipModel,
    current: HomeFilterCurrentCategory?,
    onCloseClick: () -> Unit,
) {
    val textColor = chip.text.fontColor.toColor(fallback = Pink)
    HomeFilterChipSurface(
        style = current?.style ?: chip.style ?: DEFAULT_SELECTED_CATEGORY_STYLE,
        contentDescription = chip.accessibilityText(),
    ) {
        HomeFilterChipContent(chip = chip)
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClick = onCloseClick)
                .semantics {
                    contentDescription = "카테고리 필터 해제"
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = DesignSystemR.drawable.ic_close_chip),
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun HomeFilterButtonChip(
    button: SDButtonModel,
    onClick: () -> Unit,
) {
    HomeFilterChipSurface(
        style = button.style,
        contentDescription = button.text.displayText(),
        modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
    ) {
        button.image?.let { image ->
            HomeFilterImage(image = image)
            Spacer(modifier = Modifier.width(6.dp))
        }
        HomeFilterText(text = button.text)
    }
}

@Composable
private fun HomeFilterChipSurface(
    style: SDSurfaceStyleModel?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    val backgroundColor = style?.backgroundColor.toColor(fallback = ColorWhite)
    val border = style?.border
    val borderWidth = (border?.width ?: 1.0).dp
    val borderColor = border?.color.toColor(fallback = Gray30)
    val borderModifier = if (borderWidth.value > 0f) {
        Modifier.border(BorderStroke(borderWidth, borderColor), shape)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(shape)
            .background(backgroundColor)
            .then(borderModifier)
            .then(modifier)
            .padding(horizontal = 10.dp)
            .semantics {
                this.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun HomeFilterChipContent(chip: SDChipModel) {
    chip.image?.let { image ->
        HomeFilterImage(image = image)
        Spacer(modifier = Modifier.width(6.dp))
    }
    HomeFilterText(text = chip.text)
    chip.additionalText?.let { additionalText ->
        Spacer(modifier = Modifier.width(4.dp))
        HomeFilterText(text = additionalText)
    }
}

@Composable
private fun HomeFilterImage(image: SDImageModel) {
    AsyncImage(
        model = image.url,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(
                width = (image.style?.width ?: 20.0).dp,
                height = (image.style?.height ?: 20.0).dp,
            ),
    )
}

@Composable
private fun HomeFilterText(text: SDTextModel) {
    Text(
        text = text.displayText(),
        color = text.fontColor.toColor(fallback = Gray70),
        fontFamily = PretendardFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = dpToSp(12),
        lineHeight = dpToSp(18),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun SDTextModel.displayText(): String = remember(text, isHtml) {
    if (isHtml) {
        HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    } else {
        text
    }
}

@Composable
private fun SDChipModel.accessibilityText(): String {
    val mainText = text.displayText()
    val additional = additionalText?.displayText()
    return listOfNotNull(mainText.takeIf { it.isNotBlank() }, additional?.takeIf { it.isNotBlank() })
        .joinToString(" ")
}

private fun HomeFilterCellType.stableHomeFilterKey(): String = when (this) {
    is HomeFilterCellType.Chip -> when (val action = action) {
        ChipAction.OpenCategoryFilter -> "category:${chip.text.text}"
        is ChipAction.SelectRadio -> "radio:${action.paramKey}:${action.optionIndex}:${chip.text.text}"
        is ChipAction.DeepLink -> "link:${action.link.type}:${action.link.link}:${chip.text.text}"
    }

    is HomeFilterCellType.SelectedCategoryChip -> "selected-category:${chip.text.text}:${chip.image?.url}"
    is HomeFilterCellType.Button -> "button:${button.link?.type}:${button.link?.link}:${button.text.text}"
}

private fun HomeFilterCellType.homeFilterContentType(): String = when (this) {
    is HomeFilterCellType.Chip -> "chip"
    is HomeFilterCellType.SelectedCategoryChip -> "selected-category-chip"
    is HomeFilterCellType.Button -> "button"
}

private val DEFAULT_SELECTED_CATEGORY_STYLE = SDSurfaceStyleModel(
    backgroundColor = "#FFF3F4",
    border = com.threedollar.common.serverdriven.model.SDBorderModel(color = "#FF858F", width = 1.0),
)
