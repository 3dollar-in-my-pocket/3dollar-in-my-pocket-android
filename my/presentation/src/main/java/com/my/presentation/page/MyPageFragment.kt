package com.my.presentation.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.my.presentation.page.screen.MyPageScreen
import com.threedollar.common.base.BaseComposeFragment
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.MyFragmentStarter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : BaseComposeFragment<MyPageViewModel>() {
    override val viewModel: MyPageViewModel by activityViewModels()

    @Inject
    lateinit var myFragmentStarter: MyFragmentStarter

    @Inject
    lateinit var activityStarter: ActivityStarter

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyPageFragment", screenName = null)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyFavoriteStores()
        viewModel.getUserInfo()
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addFragments.collect {
                    myFragmentStarter.addMyFragments(requireActivity(), it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.storeClick.collect {
                    if (it.storeType == "BOSS_STORE") {
                        activityStarter.startBossDetailActivity(requireContext(), it.storeId)
                    } else {
                        activityStarter.startStoreDetailActivity(requireContext(), it.storeId.toInt())
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteClick.collect {
                    activityStarter.startFavoriteActivity(requireActivity())
                }
            }
        }
    }

}