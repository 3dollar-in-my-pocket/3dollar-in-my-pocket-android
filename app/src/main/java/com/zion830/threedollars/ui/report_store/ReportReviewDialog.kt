package com.zion830.threedollars.ui.report_store

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.home.domain.data.store.ReasonModel
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
        initView()
        initButton()
    }

    private fun initView() {
        viewModel.reportReasons.value?.let { reasons ->
            val radioButtonList = listOf(
                binding.reasonButton1,
                binding.reasonButton2,
                binding.reasonButton3,
                binding.reasonButton4,
                binding.reasonButton5,
                binding.reasonButton6,
                binding.reasonButton7
            )
            reasons.forEachIndexed { index, reasonModel ->
                radioButtonList[index].text = reasonModel.description
                radioButtonList[index].isVisible = true
            }
            binding.reasonRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.reasonButton1 -> {
                        initRadio(reasons[0])
                    }

                    R.id.reasonButton2 -> {
                        initRadio(reasons[1])
                    }

                    R.id.reasonButton3 -> {
                        initRadio(reasons[2])
                    }

                    R.id.reasonButton4 -> {
                        initRadio(reasons[3])
                    }

                    R.id.reasonButton5 -> {
                        initRadio(reasons[4])
                    }

                    R.id.reasonButton6 -> {
                        initRadio(reasons[5])
                    }

                    R.id.reasonButton7 -> {
                        initRadio(reasons[6])
                    }
                }
            }
        }
    }

    private fun initRadio(reason: ReasonModel) {
        if (reason.hasReasonDetail) {
            binding.etcEditTextView.isVisible = true
            reportReviewModelRequest = reportReviewModelRequest.copy(reason = reason.type)
            binding.etcEditTextView.addTextChangedListener {
                binding.finishButton.isEnabled = binding.etcEditTextView.text.isNotBlank()
                reportReviewModelRequest = reportReviewModelRequest.copy(reasonDetail = binding.etcEditTextView.text.toString())
            }
        } else {
            binding.finishButton.isEnabled = true
            binding.etcEditTextView.isVisible = false
            binding.etcEditTextView.setText("")
            reportReviewModelRequest = reportReviewModelRequest.copy(reason = reason.type, reasonDetail = null)
        }
    }

    private fun initButton() {
        binding.closeImageButton.setOnClickListener {
            dismiss()
        }

        binding.finishButton.setOnClickListener {
            viewModel.reportReview(storeId = storeId ?: -1, content?.review?.reviewId ?: -1, reportReviewModelRequest)
            dismiss()
        }
    }

    companion object {
        fun getInstance(content: ReviewContentModel? = null, storeId: Int? = null) = ReportReviewDialog(content, storeId)
    }
}