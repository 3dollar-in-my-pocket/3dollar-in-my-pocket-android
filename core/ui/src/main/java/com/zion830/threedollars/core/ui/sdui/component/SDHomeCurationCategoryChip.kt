package com.zion830.threedollars.core.ui.sdui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import base.compose.dpToSp
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCategoryFilterModel
import com.zion830.threedollars.core.ui.sdui.element.SDChip
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface

@Composable
fun SDHomeCurationCategoryChip(
    model: SDHomeCurationCategoryFilterModel,
    selected: Boolean,
    onCategoryClick: (SDHomeCurationCategoryFilterModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chip = (if (selected) model.selected else model.unselected) ?: return
    SDChip(
        model = chip,
        modifier = modifier
            .sdSurface(chip.style, RoundedCornerShape(percent = 50))
            .selectable(selected = selected, role = Role.Tab, onClick = { onCategoryClick(model) })
            .padding(horizontal = 12.dp, vertical = 8.dp),
        fontSize = dpToSp(14),
        lineHeight = dpToSp(20),
    )
}
