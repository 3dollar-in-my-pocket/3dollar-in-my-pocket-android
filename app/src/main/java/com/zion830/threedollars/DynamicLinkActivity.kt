package com.zion830.threedollars

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.toStringDefault
import com.zion830.threedollars.databinding.ActivityDynamiclinkBinding
import com.zion830.threedollars.ui.favorite.viewer.FavoriteViewerActivity
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DynamicLinkActivity : AppCompatActivity() {
    companion object {
        const val BOOKMARK = "bookmark"
        const val HOME = "home"
        const val MEDAL = "medal"
        const val STORE = "store"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityDynamiclinkBinding>(this, R.layout.activity_dynamiclink)
        fun playLottie() {
            binding.lottieView.playAnimation()
            binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    // do nothing
                }

                override fun onAnimationEnd(animation: Animator) {
                    handleDeepLink()
                }

                override fun onAnimationCancel(animation: Animator) {
                    // do nothing
                }

                override fun onAnimationRepeat(animation: Animator) {
                    // do nothing
                }
            })
        }

        if (!GlobalApplication.isLoggedIn) {
            playLottie()
        } else {
            handleDeepLink()
        }
    }

    private fun handleDeepLink() {
        val pushLink = intent.getStringExtra("link")
        if (pushLink.isNotNullOrEmpty()) {
            val deeplink = Uri.parse(pushLink)
            handleDeepLinkNavigation(deeplink)
        } else if (intent.data?.scheme.toStringDefault().contains("dollars")) {
            intent.data?.let {
                handleDeepLinkNavigation(it)
            }
        } else {
            Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    var deeplink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deeplink = pendingDynamicLinkData.link
                    }
                    if (deeplink != null) handleDeepLinkNavigation(deeplink)
                    else finish()
                }
                .addOnFailureListener(this) { finish() }
        }
    }

    private fun handleDeepLinkNavigation(deeplink: Uri) {
        when (deeplink.lastPathSegment ?: deeplink.host ?: "") {
            BOOKMARK -> {
                val id = deeplink.getQueryParameter("folderId").toStringDefault()
                startActivity(Intent(this, FavoriteViewerActivity::class.java).apply {
                    putExtra("favoriteId", id)
                })
            }
            HOME -> {
                startActivity(MainActivity.getIntent(this))
            }
            MEDAL -> {
                startActivity(MainActivity.getIntent(this).apply {
                    putExtra(MEDAL, MEDAL)
                })
            }
            STORE -> {
                val id = deeplink.getQueryParameter("storeId").toStringDefault()
                val type = deeplink.getQueryParameter("storeType").toStringDefault()
                if (type == "BOSS_STORE") {
                    startActivity(
                        FoodTruckStoreDetailActivity.getIntent(
                            this,
                            deepLinkStoreId = id
                        )
                    )
                } else {
                    startActivity(
                        StoreDetailActivity.getIntent(
                            this,
                            deepLinkStoreId = id
                        )
                    )
                }
            }
        }
        finish()
    }

}