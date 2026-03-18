package com.zion830.threedollars.ui.storeDetail.contributor.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.zion830.threedollars.DynamicLinkActivity
import javax.inject.Inject

class StoreContributorActionHandler @Inject constructor() {
    private var context: Context? = null

    fun attach(context: Context) {
        this.context = context
    }

    fun detach() {
        context = null
    }

    fun onAction(action: SDLinkModel) {
        val currentContext = context ?: return
        when (action.type.uppercase()) {
            "APP_SCHEME" -> {
                currentContext.startActivity(
                    Intent(currentContext, DynamicLinkActivity::class.java).apply {
                        putExtra("link", action.link)
                    }
                )
            }

            "WEB" -> {
                currentContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.link)))
            }
        }
    }
}
