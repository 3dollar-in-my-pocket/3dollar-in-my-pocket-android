package com.zion830.threedollars.ui.edit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.ui.edit.ui.compose.EditMenuScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreInfoScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract.EditScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditStoreFragment : Fragment() {

    private val editStoreViewModel: EditStoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by editStoreViewModel.state.collectAsStateWithLifecycle()

                when (state.currentScreen) {
                    EditScreen.Selection -> {
                        EditStoreScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.StoreInfo -> {
                        EditStoreInfoScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.Location -> {
                        EditStoreScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                    EditScreen.StoreMenu -> {
                        EditMenuScreen(
                            state = state,
                            onIntent = editStoreViewModel::processIntent
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPressHandler()
        initializeStoreData()
        observeEffects()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    editStoreViewModel.processIntent(EditStoreContract.Intent.NavigateBack)
                }
            }
        )
    }

    private fun initializeStoreData() {
        val storeId = arguments?.getInt(ARG_STORE_ID, -1) ?: -1
        if (storeId != -1) {
            editStoreViewModel.processIntent(
                EditStoreContract.Intent.LoadStoreDetail(storeId)
            )
        }
    }

    private fun observeEffects() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStoreViewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is EditStoreContract.Effect.StoreUpdated -> {
                            setFragmentResult(STORE_EDITED_RESULT_KEY, bundleOf(STORE_UPDATED to true))
                            showToast(CommonR.string.edit_store_success)
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        is EditStoreContract.Effect.ShowError -> {
                            showToast(effect.message)
                        }
                        is EditStoreContract.Effect.ShowToast -> {
                            showToast(effect.message)
                        }
                        is EditStoreContract.Effect.NavigateToLocationEdit -> {
                            EditAddressBottomSheetDialogFragment.newInstance()
                                .show(childFragmentManager, EditAddressBottomSheetDialogFragment.TAG)
                        }
                        is EditStoreContract.Effect.NavigateBack -> {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        is EditStoreContract.Effect.CloseScreen -> {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ARG_STORE_ID = "storeId"
        const val STORE_EDITED_RESULT_KEY = "storeEditedResult"
        const val STORE_UPDATED = "storeUpdated"

        fun newInstance(storeId: Int) = EditStoreFragment().apply {
            arguments = bundleOf(ARG_STORE_ID to storeId)
        }
    }
}
