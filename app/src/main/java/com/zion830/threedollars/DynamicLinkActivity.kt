package com.zion830.threedollars

import android.animation.Animator
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.zion830.threedollars.databinding.ActivityDynamiclinkBinding
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
        val binding = DataBindingUtil.setContentView<ActivityDynamiclinkBinding>(this, R.layout.activity_dynamiclink)
        fun playLottie() {
            binding.lottieView.playAnimation()
            binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    handleDeepLink()
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        fun settingDeppLink(className: String) {
            if (className.contains("DynamicLinkActivity")) {
                playLottie()
            } else {
                handleDeepLink()
            }
        }

        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.appTasks.forEach { task ->
                    settingDeppLink(task.taskInfo.baseActivity?.className.toString())
                }
            } else {
                it.getRunningTasks(10).forEach { task ->
                    settingDeppLink(task.baseActivity?.className.toString())
                }
            }
        }
    }

    private fun handleDeepLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deeplink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deeplink = pendingDynamicLinkData.link
                }
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
                    finish()
                }
            }
            .addOnFailureListener(this) { finish() }
    }
}