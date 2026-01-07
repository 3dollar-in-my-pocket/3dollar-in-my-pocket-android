package com.zion830.threedollars.core.ui.component.compose

import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible

fun ComposeView.gone() {
    isVisible = false
    disposeComposition()
}
