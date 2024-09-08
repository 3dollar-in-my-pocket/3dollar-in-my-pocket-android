package com.my.presentation.page.team

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import com.my.presentation.page.screen.MyPageTeamScreen
import com.threedollar.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageTeamActivity : BaseComposeActivity<MyPageTeamViewModel>() {
    override val viewModel: MyPageTeamViewModel by viewModels()

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyPageTeamActivity", screenName = null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyPageTeamScreen {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/3dollar_in_my_pocket"))
                    startActivity(browserIntent)
                }
            }
        }
    }

}