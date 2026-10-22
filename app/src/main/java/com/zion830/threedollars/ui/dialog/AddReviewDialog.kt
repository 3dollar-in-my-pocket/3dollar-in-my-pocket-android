package com.zion830.threedollars.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.common.analytics.ScreenName
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class AddReviewDialog : BaseBottomSheetDialogFragment<DialogAddReviewBinding>() {
    private val viewModel: StoreDetailViewModel by activityViewModels()
    private val storeId: Int? by lazy {
        arguments?.takeIf { it.containsKey(ARG_STORE_ID) }?.getInt(ARG_STORE_ID)
    }
    private val reviewId: Long? by lazy {
        arguments?.takeIf { it.containsKey(ARG_REVIEW_ID) }?.getLong(ARG_REVIEW_ID)
    }
    override val screenName: ScreenName = ScreenName.REVIEW_BOTTOM_SHEET

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, DesignSystemR.style.CustomBottomSheetDialogTheme)
    }
    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogAddReviewBinding =
        DialogAddReviewBinding.inflate(inflater, container, false)

    override fun initView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButton()

        if (reviewId != null) {
            binding.rating.rating = arguments?.getFloat(ARG_RATING) ?: 0f
            binding.etContent.setText(arguments?.getString(ARG_CONTENTS))
        }
    }

    private fun initButton() {
        binding.closeImageButton.onSingleClick {
            dismiss()
        }
        binding.btnFinish.onSingleClick {
            if (binding.rating.rating == 0f) {
                showToast(CommonR.string.over_rating_1)
                return@onSingleClick
            }
            arguments?.getString(ARG_RESULT_KEY)?.let { resultKey ->
                val submitStoreId = arguments?.getString(ARG_SUBMIT_STORE_ID) ?: return@onSingleClick
                setFragmentResult(
                    resultKey,
                    bundleOf(
                        RESULT_STORE_ID to submitStoreId,
                        RESULT_CONTENTS to binding.etContent.text.toString(),
                        RESULT_RATING to binding.rating.rating.toInt(),
                    ),
                )
                dismiss()
                return@onSingleClick
            }
            viewModel.sendClickWriteReviewSubmit(binding.rating.rating.toInt())
            val editingReviewId = reviewId
            if (editingReviewId == null) {
                viewModel.postStoreReview(
                    binding.etContent.text.toString(),
                    binding.rating.rating.toInt(),
                    storeId
                )
                dismiss()
            } else {
                viewModel.putStoreReview(editingReviewId, binding.etContent.text.toString(), binding.rating.rating.toInt())
                dismiss()
            }
        }
        binding.etContent.addTextChangedListener {
            binding.btnFinish.isEnabled = binding.etContent.text.isNotBlank()
        }
    }

    companion object {
        const val REVIEW_SUBMITTED_RESULT_KEY = "review_submitted"
        const val RESULT_STORE_ID = "store_id"
        const val RESULT_CONTENTS = "contents"
        const val RESULT_RATING = "rating"

        private const val ARG_STORE_ID = "store_id"
        private const val ARG_REVIEW_ID = "review_id"
        private const val ARG_CONTENTS = "review_contents"
        private const val ARG_RATING = "review_rating"
        private const val ARG_RESULT_KEY = "result_key"
        private const val ARG_SUBMIT_STORE_ID = "submit_store_id"

        fun getInstance(content: ReviewContentModel? = null, storeId: Int? = null) =
            AddReviewDialog().apply {
                arguments = Bundle().apply {
                    storeId?.let { putInt(ARG_STORE_ID, it) }
                    content?.review?.let { review ->
                        putLong(ARG_REVIEW_ID, review.reviewId)
                        putString(ARG_CONTENTS, review.contents)
                        putFloat(ARG_RATING, review.rating)
                    }
                }
            }

        /**
         * 가게 상세 v2는 [REVIEW_SUBMITTED_RESULT_KEY] 결과를 받아 제출한다.
         * 결과 전달 모드도 arguments에 저장해 Fragment 복원 후 레거시 경로로 바뀌지 않는다.
         */
        fun newInstance(storeId: String) = AddReviewDialog().apply {
            arguments = bundleOf(
                ARG_RESULT_KEY to REVIEW_SUBMITTED_RESULT_KEY,
                ARG_SUBMIT_STORE_ID to storeId,
            )
        }
    }
}
