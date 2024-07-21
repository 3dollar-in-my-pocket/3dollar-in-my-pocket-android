package com.my.presentation.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.my.presentation.page.screen.MyPageScreen
import com.threedollar.common.base.BaseComposeFragment
import com.threedollar.common.listener.MyFragmentStarter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseComposeFragment<MyPageViewModel>() {
    override val viewModel: MyPageViewModel by viewModels()

    @Inject
    lateinit var myFragmentStarter: MyFragmentStarter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            viewModel.addFragments.collect {
                myFragmentStarter.addMyFragments(requireActivity(), it)
            }
        }
    }

}