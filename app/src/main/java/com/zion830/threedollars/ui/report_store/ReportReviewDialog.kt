package com.zion830.threedollars.ui.report_store

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.request.ReportReviewModelRequest
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.databinding.DialogReportReviewBinding
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportReviewDialog(private val content: ReviewContentModel?, private val storeId: Int?) : BottomSheetDialogFragment() {
    private val viewModel: StoreDetailViewModel by activityViewModels()
    private lateinit var binding: DialogReportReviewBinding

    private var reportReviewModelRequest = ReportReviewModelRequest()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogReportReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButton()

    }

    private fun initButton() {
        binding.closeImageButton.setOnClickListener {
            dismiss()
        }
        binding.reasonRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.reasonButton1 -> {
                    binding.finishButton.isEnabled = true
                    binding.etcEditTextView.isVisible = false
                    binding.etcEditTextView.setText("")
                    reportReviewModelRequest = reportReviewModelRequest.copy(reason = binding.reasonButton1.text.toString(), reasonDetail = "")
                }

                R.id.reasonButton2 -> {
                    binding.finishButton.isEnabled = true
                    binding.etcEditTextView.isVisible = false
                    binding.etcEditTextView.setText("")
                    reportReviewModelRequest = reportReviewModelRequest.copy(reason = binding.reasonButton2.text.toString(), reasonDetail = "")
                }

                R.id.reasonButton3 -> {
                    binding.finishButton.isEnabled = true
                    binding.etcEditTextView.isVisible = false
                    binding.etcEditTextView.setText("")
                    reportReviewModelRequest = reportReviewModelRequest.copy(reason = binding.reasonButton3.text.toString(), reasonDetail = "")
                }

                R.id.reasonButton4 -> {
                    binding.finishButton.isEnabled = true
                    binding.etcEditTextView.isVisible = false
                    binding.etcEditTextView.setText("")
                    reportReviewModelRequest = reportReviewModelRequest.copy(reason = binding.reasonButton4.text.toString(), reasonDetail = "")
                }

                R.id.reasonButton5 -> {
                    binding.etcEditTextView.isVisible = true
                    reportReviewModelRequest = reportReviewModelRequest.copy(reason = binding.reasonButton5.text.toString())
                    binding.etcEditTextView.addTextChangedListener {
                        binding.finishButton.isEnabled = binding.etcEditTextView.text.isNotBlank()
                        reportReviewModelRequest = reportReviewModelRequest.copy(reasonDetail = binding.etcEditTextView.text.toString())
                    }
                }

            }
        }
        binding.finishButton.setOnClickListener {
            viewModel.reportReview(storeId = storeId ?: -1, content?.review?.reviewId ?: -1, reportReviewModelRequest)
        }

    }

    companion object {
        fun getInstance(content: ReviewContentModel? = null, storeId: Int? = null) = ReportReviewDialog(content, storeId)
    }
}