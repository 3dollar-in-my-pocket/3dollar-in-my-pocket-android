package com.zion830.threedollars.ui.dialog.category

sealed interface SelectCategoryEffect {
    data object OnInitError : SelectCategoryEffect
}
