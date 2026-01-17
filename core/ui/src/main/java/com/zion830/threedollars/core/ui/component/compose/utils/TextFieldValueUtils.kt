package com.zion830.threedollars.core.ui.component.compose.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.take(n: Int): TextFieldValue {
    val text = text.take(n)
    return copy(
        text = text,
        selection = TextRange(text.length)
    )
}