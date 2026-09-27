package com.zion830.threedollars

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.orEmpty
import com.threedollar.common.ext.toStringDefault
import com.zion830.threedollars.ui.community.poll.PollDetailActivity
import com.zion830.threedollars.databinding.ActivityDynamiclinkBinding
import com.zion830.threedollars.ui.favorite.viewer.FavoriteViewerActivity
import com.zion830.threedollars.ui.storeDetail.contributor.ui.StoreContributorActivity
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreSectionFragment
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.MoreImageActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreReviewDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DynamicLinkActivity : AppCompatActivity() {
    companion object {
        const val BOOKMARK = "bookmark"
        const val HOME = "home"
        const val MEDAL = "medal"
        const val STORE = "store"
        const val STORE_PREVIEW = "storePreview"
        const val VISIT = "visit"
        const val POLL = "pollDetail"
        const val COMMUNITY = "community"
        const val REVIEW_LIST = "reviewList"
        const val BROWSER = "browser"
        const val HOME_PRESET = "homePreset"
        const val STORE_CONTRIBUTORS = "store-contributors"
        const val STORE_IMAGES = "images"

        private const val LINK = "link"
        private const val SCHEME_DOLLARS = "dollars"
        private const val FOLDER_ID = "folderId"
        private const val FAVORITE_ID = "favoriteId"
        private const val STORE_ID = "storeId"
        private const val POLL_ID = "pollId"
        private const val ID = "id"
        private const val URL = "url"
        private const val PRESET = "preset"

        fun launch(context: Context, link: String) {
            Intent(context, DynamicLinkActivity::class.java).apply {
                putExtra(LINK, link)
            }.let {
                context.startActivity(it)
            }
        }
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
        val pushLink = intent.getStringExtra(LINK)
        if (pushLink.isNotNullOrEmpty()) {
            handleDeepLinkNavigation(pushLink?.toUri().orEmpty())
            return
        }

        if (intent.data?.scheme.toStringDefault().contains(SCHEME_DOLLARS)) {
            handleDeepLinkNavigation(intent.data.orEmpty())
            return
        }

        finish()
    }

    private fun handleDeepLinkNavigation(deeplink: Uri) {
        StoreSectionFragment.parseLink(deeplink.toString())?.let { link ->
            startActivity(StoreDetailSduiActivity.getIntent(this, link.storeId, link.fragment))
            finish()
            return
        }

        when (deeplink.lastPathSegment ?: deeplink.host ?: "") {
            BOOKMARK -> {
                val id = deeplink.getQueryParameter(FOLDER_ID).toStringDefault()
                startActivity(Intent(this, FavoriteViewerActivity::class.java).apply {
                    putExtra(FAVORITE_ID, id)
                })
            }

            MEDAL -> {
                startActivity(MainActivity.getIntent(this).apply {
                    putExtra(MEDAL, MEDAL)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            STORE -> {
                val id = deeplink.getQueryParameter(STORE_ID)
                if (id.isNullOrBlank()) {
                    startActivity(MainActivity.getIntent(this))
                } else {
                    startActivity(StoreDetailSduiActivity.getIntent(this, id))
                }
            }

            STORE_CONTRIBUTORS -> {
                val id = deeplink.getQueryParameter(STORE_ID)
                if (id.isNullOrBlank()) {
                    startActivity(MainActivity.getIntent(this))
                } else {
                    startActivity(StoreContributorActivity.getIntent(this, id))
                }
            }

            STORE_IMAGES -> {
                if (deeplink.isStorePath()) {
                    val id = deeplink.getQueryParameter(STORE_ID)?.toIntOrNull()
                    startActivity(MoreImageActivity.getIntent(this, id))
                } else {
                    startActivity(MainActivity.getIntent(this))
                }
            }

            STORE_PREVIEW -> {
                val id = deeplink.getQueryParameter(STORE_ID)?.toLongOrNull()
                startActivity(MainActivity.getIntent(this).apply {
                    id?.let { putExtra(STORE_PREVIEW, it) }
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            VISIT -> {
                deeplink.getQueryParameter(STORE_ID)?.toIntOrNull()?.let { id ->
                    startActivity(StoreCertificationActivity.getIntent(this, id))
                }
            }

            POLL -> {
                val id = deeplink.getQueryParameter(POLL_ID).toStringDefault()
                startActivity(Intent(this, PollDetailActivity::class.java).apply {
                    putExtra(ID, id)
                })
            }

            COMMUNITY -> {
                startActivity(MainActivity.getIntent(this).apply {
                    putExtra(COMMUNITY, COMMUNITY)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            HOME -> {
                startActivity(MainActivity.getIntent(this).apply {
                    putExtra(HOME, HOME)
                    deeplink.getQueryParameter(PRESET)?.let { putExtra(HOME_PRESET, it) }
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            REVIEW_LIST -> {
                val storeId = deeplink.getQueryParameter(STORE_ID)

                if (storeId != null) {
                    val stackBuilder = TaskStackBuilder.create(this)

                    // MainActivity 추가 (홈 백스택)
                    stackBuilder.addNextIntent(MainActivity.getIntent(this))

                    // 상점 상세 Activity 추가
                    stackBuilder.addNextIntent(StoreDetailSduiActivity.getIntent(this, storeId))

                    // 리뷰 Activity 추가
                    stackBuilder.addNextIntent(StoreReviewDetailActivity.getInstance(this, storeId.toIntOrNull() ?: 0))

                    // 백스택 시작
                    stackBuilder.startActivities()
                    finish()
                    return
                }
            }
            BROWSER -> {
                val url = deeplink.getQueryParameter(URL).toStringDefault()
                startActivity(MainActivity.getIntent(this).apply {
                    putExtra(BROWSER, url)
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }

            else -> {
                startActivity(MainActivity.getIntent(this))
            }
        }
        finish()
    }

    private fun Uri.isStorePath(): Boolean = host == STORE || pathSegments.contains(STORE)
}
