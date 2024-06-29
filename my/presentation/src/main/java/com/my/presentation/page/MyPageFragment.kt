package com.my.presentation.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import com.my.presentation.page.screen.MyPageScreen
import com.threedollar.common.base.BaseComposeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : BaseComposeFragment<MyPageViewModel>() {
    override val viewModel: MyPageViewModel by viewModels()

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyPageFragment", screenName = null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    MyPageScreen(viewModel)
                }
            }
        }
    }

}