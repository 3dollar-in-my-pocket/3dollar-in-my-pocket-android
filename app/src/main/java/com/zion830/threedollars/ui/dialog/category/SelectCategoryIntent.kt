package com.zion830.threedollars.ui.dialog.category

sealed interface SelectCategoryIntent {
    data object OnInit : SelectCategoryIntent
}
