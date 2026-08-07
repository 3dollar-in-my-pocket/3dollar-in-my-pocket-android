package com.zion830.threedollars.ui.edit.ui

import android.app.AlertDialog
import android.net.Uri
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
import com.zion830.threedollars.R
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.ui.edit.ui.compose.EditMenuScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreInfoScreen
import com.zion830.threedollars.ui.edit.ui.compose.EditStoreScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract
import com.zion830.threedollars.ui.edit.viewModel.EditStoreContract.EditScreen
import com.zion830.threedollars.ui.edit.viewModel.EditStoreViewModel
import com.zion830.threedollars.utils.FileUtils
import com.zion830.threedollars.utils.goToPermissionSetting
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import java.util.UUID
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditStoreFragment : Fragment() {

    private val editStoreViewModel: EditStoreViewModel by viewModels()
    private var progressDialog: AlertDialog? = null

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
        observeState()
        observeEffects()
    }

    override fun onDestroyView() {
        hideUploadProgress()
        progressDialog = null
        super.onDestroyView()
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
                        is EditStoreContract.Effect.LaunchPhotoPicker -> {
                            launchPhotoPicker()
                        }
                        is EditStoreContract.Effect.CloseScreen -> {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editStoreViewModel.state.collectLatest { state ->
                    if (state.isLoading && state.isPhotoUploading) {
                        showUploadProgress()
                    } else {
                        hideUploadProgress()
                    }
                }
            }
        }
    }

    private fun launchPhotoPicker() {
        TedImagePicker.with(requireContext())
            .zoomIndicator(false)
            .errorListener { throwable ->
                if (throwable.message?.startsWith("permission") == true) {
                    AlertDialog.Builder(requireContext())
                        .setPositiveButton(CommonR.string.request_permission_ok) { _, _ ->
                            requireContext().goToPermissionSetting()
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .setTitle(getString(CommonR.string.request_permission))
                        .setMessage(getString(CommonR.string.request_permission_msg))
                        .create()
                        .show()
                } else {
                    showToast(getString(CommonR.string.boss_review_image_selection_error))
                }
            }
            .startMultiImage { uriData ->
                val pendingPhotos = buildPendingPhotos(uriData.filterNotNull()) ?: return@startMultiImage
                if (pendingPhotos.isNotEmpty()) {
                    editStoreViewModel.processIntent(
                        EditStoreContract.Intent.AddPendingPhotos(pendingPhotos)
                    )
                }
            }
    }

    private fun buildPendingPhotos(uris: List<Uri>): List<EditStoreContract.PendingPhoto>? {
        val pendingPhotos = mutableListOf<EditStoreContract.PendingPhoto>()
        uris.forEach { uri ->
            if (!FileUtils.isAvailable(uri)) {
                showToast(CommonR.string.error_file_size)
                return null
            }

            val cachedFile = FileUtils.uriToFile(uri)
            if (cachedFile == null) {
                showToast(getString(CommonR.string.boss_review_image_selection_error))
                return null
            }

            pendingPhotos.add(
                EditStoreContract.PendingPhoto(
                    id = "pending-${UUID.randomUUID()}",
                    uriString = uri.toString(),
                    cachedFilePath = cachedFile.absolutePath,
                    displayName = cachedFile.name,
                )
            )
        }
        return pendingPhotos
    }

    private fun showUploadProgress() {
        if (progressDialog == null) {
            progressDialog = AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setView(R.layout.layout_image_upload_progress)
                .create()
        }
        progressDialog?.show()
    }

    private fun hideUploadProgress() {
        progressDialog?.dismiss()
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
