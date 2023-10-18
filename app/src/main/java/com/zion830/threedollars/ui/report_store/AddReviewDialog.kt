package com.zion830.threedollars.ui.report_store

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.databinding.DialogBottomDirectionBinding
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import com.zion830.threedollars.datasource.model.v2.response.my.Review
import com.zion830.threedollars.ui.DirectionBottomDialog
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReviewDialog(
    private val content: Review?,
) : BottomSheetDialogFragment() {
    private val viewModel: StoreDetailViewModel by activityViewModels()
    private lateinit var binding: DialogAddReviewBinding

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
        binding = DialogAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initButton()

        if (content != null) {
            binding.rating.rating = content.rating
            binding.etContent.setText(content.contents)
        }
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
                    viewModel.userStoreDetailModel.value?.store?.storeId
                )
                dismiss()
            } else {
                val newReview = NewReview(binding.etContent.text.toString(), binding.rating.rating)
                viewModel.editReview(content.reviewId, newReview)
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
        fun getInstance(content: Review? = null) = AddReviewDialog(content)
    }
}