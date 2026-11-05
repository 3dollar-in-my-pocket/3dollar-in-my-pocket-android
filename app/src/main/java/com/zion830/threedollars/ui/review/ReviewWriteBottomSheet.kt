package com.zion830.threedollars.ui.review

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.domain.home.data.store.ReviewModel
import com.threedollar.domain.home.data.store.UploadFileModel
import com.zion830.threedollars.utils.FileUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 제보 가게·사장님 가게 공통 리뷰 작성/수정 시트 (별점 + 사진 + 내용).
 * 등록에 성공하면 [REQUEST_KEY] 로 결과를 보내고 닫힌다. 호출부는 [setOnSubmittedListener] 로 목록을 갱신한다.
 */
@AndroidEntryPoint
class ReviewWriteBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ReviewWriteViewModel by viewModels()

    private val photoPicker = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(ReviewWriteState.MAX_PHOTO_COUNT),
    ) { uris ->
        if (uris.isEmpty()) return@registerForActivityResult
        viewLifecycleOwner.lifecycleScope.launch {
            val available = withContext(Dispatchers.IO) { uris.filter { FileUtils.isAvailable(it) } }
            if (available.size < uris.size) showToast(CommonR.string.error_file_size)
            viewModel.addPhotos(available.map { it.toString() })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, DesignSystemR.style.CustomBottomSheetDialogTheme)
        viewModel.initialize(initialState())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) ?: return@setOnShowListener
            bottomSheet.setBackgroundResource(android.R.color.transparent)
            BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.state.collectAsStateWithLifecycle()
                ReviewWriteScreen(
                    state = state,
                    onClose = ::dismiss,
                    onRatingChange = viewModel::updateRating,
                    onContentsChange = viewModel::updateContents,
                    onAddPhotoClick = ::pickPhotos,
                    onRemovePhoto = viewModel::removePhoto,
                    onSubmit = viewModel::submit,
                )
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect(::handleEffect)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogManager.sendPageView(ScreenName.REVIEW_BOTTOM_SHEET, this::class.java.simpleName)
    }

    private fun pickPhotos() {
        if (!viewModel.state.value.canAddPhoto) return
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleEffect(effect: ReviewWriteEffect) {
        when (effect) {
            is ReviewWriteEffect.Submitted -> {
                if (effect.isEdit) showToast(CommonR.string.success_edit_review)
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_IS_EDIT to effect.isEdit))
                dismiss()
            }

            is ReviewWriteEffect.ShowError -> showToast(effect.message ?: getString(CommonR.string.connection_failed))
        }
    }

    private fun initialState(): ReviewWriteState {
        val args = requireArguments()
        val images = BundleCompat.getParcelableArrayList(args, ARG_IMAGES, ImageModel::class.java).orEmpty()
        return ReviewWriteState(
            storeId = args.getInt(ARG_STORE_ID),
            storeName = args.getString(ARG_STORE_NAME).orEmpty(),
            reviewId = args.getLong(ARG_REVIEW_ID, NO_REVIEW_ID).takeIf { it != NO_REVIEW_ID },
            rating = args.getInt(ARG_RATING),
            contents = args.getString(ARG_CONTENTS).orEmpty(),
            photos = images.map { ReviewPhoto.Uploaded(UploadFileModel(it.imageUrl, it.width, it.height, it.ratio.toDouble())) },
        )
    }

    companion object {
        const val REQUEST_KEY = "review_write_bottom_sheet"
        const val RESULT_IS_EDIT = "is_edit"
        private const val TAG = "ReviewWriteBottomSheet"
        private const val ARG_STORE_ID = "store_id"
        private const val ARG_STORE_NAME = "store_name"
        private const val ARG_REVIEW_ID = "review_id"
        private const val ARG_RATING = "rating"
        private const val ARG_CONTENTS = "contents"
        private const val ARG_IMAGES = "images"
        private const val NO_REVIEW_ID = -1L

        fun showForWrite(fragmentManager: FragmentManager, storeId: Int, storeName: String) {
            show(fragmentManager, bundleOf(ARG_STORE_ID to storeId, ARG_STORE_NAME to storeName))
        }

        fun showForEdit(fragmentManager: FragmentManager, storeId: Int, storeName: String, review: ReviewModel) {
            show(
                fragmentManager,
                bundleOf(
                    ARG_STORE_ID to storeId,
                    ARG_STORE_NAME to storeName,
                    ARG_REVIEW_ID to review.reviewId,
                    ARG_RATING to review.rating.toInt(),
                    ARG_CONTENTS to review.contents.orEmpty(),
                    ARG_IMAGES to ArrayList(review.images),
                ),
            )
        }

        fun setOnSubmittedListener(fragmentManager: FragmentManager, lifecycleOwner: LifecycleOwner, onSubmitted: (isEdit: Boolean) -> Unit) {
            fragmentManager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, result ->
                onSubmitted(result.getBoolean(RESULT_IS_EDIT))
            }
        }

        private fun show(fragmentManager: FragmentManager, args: Bundle) {
            if (fragmentManager.findFragmentByTag(TAG) != null) return
            ReviewWriteBottomSheet().apply { arguments = args }.show(fragmentManager, TAG)
        }
    }
}
