package com.zion830.threedollars.ui.storeDetail.v2

internal enum class StoreDetailAdLoadState(val shouldRender: Boolean) {
    Loading(shouldRender = true),
    Loaded(shouldRender = true),
    Failed(shouldRender = false),
}
