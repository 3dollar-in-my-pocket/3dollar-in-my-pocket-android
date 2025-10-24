package com.threedollar.domain.home.data.store

import android.net.Uri
import com.threedollar.domain.home.data.store.UserStoreDetailItem

data class StoreImage(
    val index: Int,
    val uri: Uri? = null,
    val url: String? = "",
) : UserStoreDetailItem