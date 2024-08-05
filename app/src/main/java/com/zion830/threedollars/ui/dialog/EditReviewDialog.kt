package com.zion830.threedollars.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReviewDialog(
    private val content: ReviewDetail?,
    private val onComplete: (NewReview) -> Unit,
) : BaseBottomSheetDialogFragment<DialogAddReviewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogAddReviewBinding =
        DialogAddReviewBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "EditReviewDialog",screenName = "review_bottom_sheet")
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.closeImageButton.setOnClickListener {
            dismiss()
        }
        binding.etContent.setText(content?.contents)
        binding.rating.rating = content?.rating ?: 0f
        binding.btnFinish.setOnClickListener {
            when {
                binding.rating.rating == 0f -> {
                    showToast(R.string.over_rating_1)
                }

                binding.etContent.text.isBlank() -> {
                    showToast(R.string.input_content)
                }

                else -> {
                    val newReview =
                        NewReview(binding.etContent.text.toString(), binding.rating.rating)
                    onComplete(newReview)
                    dismiss()
                }
            }
        }
        binding.etContent.addTextChangedListener {
            binding.btnFinish.isEnabled = binding.etContent.text.isNotBlank()
        }
    }
}