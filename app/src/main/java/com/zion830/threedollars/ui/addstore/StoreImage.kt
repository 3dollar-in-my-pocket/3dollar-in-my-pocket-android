package com.zion830.threedollars.ui.addstore

import android.net.Uri

data class StoreImage(
    val index: Int,
    val uri: Uri? = null,
    val url: String? = "",
)