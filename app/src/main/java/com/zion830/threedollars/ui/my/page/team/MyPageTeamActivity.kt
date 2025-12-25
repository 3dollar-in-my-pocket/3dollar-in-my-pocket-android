package com.zion830.threedollars.ui.my.page.team

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.zion830.threedollars.ui.my.page.screen.MyPageTeamScreen
import com.threedollar.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageTeamActivity : BaseComposeActivity<MyPageTeamViewModel>() {
    override val viewModel: MyPageTeamViewModel by viewModels()
    private var rewardedAd: RewardedAd? = null

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyPageTeamActivity", screenName = null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inItAd()
        setContent {
            MaterialTheme {
                MyPageTeamScreen(
                    clickBack = { finish() },
                    clickAd = {
                        rewardedAd?.let { ad ->
                            ad.show(this) { rewardItem ->
                                val rewardAmount = rewardItem.amount
                                val rewardType = rewardItem.type
                            }
                        }
                    },
                    clickTeam = {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/3dollar_in_my_pocket"))
                        startActivity(browserIntent)
                    })
            }
        }
    }

    private fun inItAd() {
        RewardedAd.load(this, "ca-app-pub-5385646520024289/4616671581", AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
            }
        })
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {}

            override fun onAdDismissedFullScreenContent() {

            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)

            }

            override fun onAdImpression() {

            }

            override fun onAdShowedFullScreenContent() {

            }
        }
    }

}