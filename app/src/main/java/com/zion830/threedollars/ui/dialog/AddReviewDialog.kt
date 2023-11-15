package com.zion830.threedollars.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.home.domain.data.store.ReviewContentModel
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReviewDialog(private val content: ReviewContentModel?, private val storeId: Int?) : BaseBottomSheetDialogFragment<DialogAddReviewBinding>() {
    private val viewModel: StoreDetailViewModel by activityViewModels()

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogAddReviewBinding =
        DialogAddReviewBinding.inflate(inflater, container, false)

    override fun initView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButton()

        if (content != null) {
            binding.rating.rating = content.review.rating.toFloat()
            binding.etContent.setText(content.review.contents)
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent("AddReviewDialog")
    }

    private fun initButton() {
        binding.closeImageButton.setOnClickListener {
            EventTracker.logEvent(Constants.REVIEW_WRITE_CLOSE_BTN_CLICKED)
            dismiss()
        }
        binding.btnFinish.setOnClickListener {
            EventTracker.logEvent(Constants.REVIEW_REGISTER_BTN_CLICKED)
            if (binding.rating.rating == 0f) {
                showToast(R.string.over_rating_1)
                return@setOnClickListener
            }

            if (content == null) {
                viewModel.postStoreReview(
                    binding.etContent.text.toString(),
                    binding.rating.rating.toInt(),
                    storeId
                )
                dismiss()
            } else {
                viewModel.putStoreReview(content.review.reviewId, binding.etContent.text.toString(), binding.rating.rating.toInt())
                dismiss()
            }
        }
        binding.rating.setOnClickListener {
            EventTracker.logEvent(Constants.STAR_BTN_CLICKED)
        }
        binding.etContent.addTextChangedListener {
            binding.btnFinish.isEnabled = binding.etContent.text.isNotBlank()
        }
    }

    companion object {
        fun getInstance(content: ReviewContentModel? = null, storeId: Int? = null) = AddReviewDialog(content, storeId)
    }
}