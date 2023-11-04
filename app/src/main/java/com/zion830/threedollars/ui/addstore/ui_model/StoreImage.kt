package com.zion830.threedollars.ui.addstore.ui_model

import android.net.Uri
import com.home.domain.data.store.UserStoreDetailItem

data class StoreImage(
    val index: Int,
    val uri: Uri? = null,
    val url: String? = "",
) : UserStoreDetailItem