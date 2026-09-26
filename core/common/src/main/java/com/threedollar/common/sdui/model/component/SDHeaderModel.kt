package com.threedollar.common.sdui.model.component

import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDTextModel

data class SDHeaderModel(
    val title: SDTextModel?,
    val subTitle: SDTextModel? = null,
    val trailingAction: SDButtonModel? = null
)
