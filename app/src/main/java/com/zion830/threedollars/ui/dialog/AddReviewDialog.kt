package com.zion830.threedollars.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.home.domain.data.store.ReviewContentModel
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.utils.Constants.CLICK_REVIEW_BOTTOM_BUTTON
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReviewDialog(private val content: ReviewContentModel?, private val storeId: Int?) : BaseBottomSheetDialogFragment<DialogAddReviewBinding>() {
    private val viewModel: StoreDetailViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
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

        if (content != null) {
            binding.rating.rating = content.review.rating.toFloat()
            binding.etContent.setText(content.review.contents)
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "AddReviewDialog", screenName = "review_bottom_sheet")
    }

    private fun initButton() {
        binding.closeImageButton.setOnClickListener {
            dismiss()
        }
        binding.btnFinish.setOnClickListener {
            if (binding.rating.rating == 0f) {
                showToast(R.string.over_rating_1)
                return@setOnClickListener
            }
            val bundle = Bundle().apply {
                putString("screen", "review_bottom_sheet")
                putString("store_id", storeId.toString())
                putString("rating", binding.rating.rating.toString())
            }
            EventTracker.logEvent(CLICK_REVIEW_BOTTOM_BUTTON, bundle)
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
        binding.etContent.addTextChangedListener {
            binding.btnFinish.isEnabled = binding.etContent.text.isNotBlank()
        }
    }

    companion object {
        fun getInstance(content: ReviewContentModel? = null, storeId: Int? = null) = AddReviewDialog(content, storeId)
    }
}