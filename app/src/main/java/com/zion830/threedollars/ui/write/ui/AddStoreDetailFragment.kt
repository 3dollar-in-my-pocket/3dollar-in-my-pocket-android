package com.zion830.threedollars.ui.write.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.write.ui.compose.AddStoreFlowScreen
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class AddStoreDetailFragment : Fragment() {

    private val viewModel: AddStoreViewModel by activityViewModels()
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        initNavigationBar()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    AddStoreFlowScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() },
                        onCloseClick = { navigateToHome() },
                        onComplete = { navigateToStoreDetail() },
                        onLocationChangeClick = { navigateToLocationChange() }
                    )
                }
            }
        }
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }

    private fun navigateBack() {
        viewModel.processIntent(AddStoreContract.Intent.ResetState)
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
    }

    private fun navigateToLocationChange() {
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_navigation_write)
    }

    private fun navigateToHome() {
        viewModel.processIntent(AddStoreContract.Intent.ResetState)
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_home)
    }

    private fun navigateToStoreDetail() {
        val storeId = viewModel.state.value.createdStoreId
        viewModel.processIntent(AddStoreContract.Intent.ResetState)

        val bundle = Bundle().apply {
            storeId?.let { putInt(NAVIGATE_STORE_ID, it) }
        }
        findNavController().navigateSafe(R.id.action_navigation_write_detail_to_home, bundle)
    }

    companion object {
        const val NAVIGATE_STORE_ID = "navigateStoreId"
    }
}
