package com.zion830.threedollars

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.zion830.threedollars.ui.favorite.viewer.FavoriteViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.ext.toStringDefault

@AndroidEntryPoint
class DynamicLinkActivity : AppCompatActivity() {

    companion object {
        const val BOOKMARK = "bookmark"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink()
    }

    private fun handleDeepLink() {
        Log.e(localClassName, "handleDeepLink")
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deeplink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deeplink = pendingDynamicLinkData.link
                }
                Log.e(localClassName, "addOnSuccessListener")
                if (deeplink != null) {
                    when (deeplink.lastPathSegment.toStringDefault()) {
                        BOOKMARK -> {
                            val id = deeplink.getQueryParameter("folderId").toStringDefault()
                            startActivity(Intent(this, FavoriteViewerActivity::class.java).apply {
                                putExtra("favoriteId", id)
                            })
                            finish()
                        }
                    }
                } else {
                    Log.d(localClassName, "getDynamicLink: no link found")
                }
            }
            .addOnFailureListener(this) { e -> Log.e(localClassName, "getDynamicLink:onFailure", e) }
    }
}