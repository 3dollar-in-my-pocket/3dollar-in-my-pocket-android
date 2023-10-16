package com.zion830.threedollars.ui.report_store

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.threedollar.common.ext.textPartColor
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogDeleteBinding
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteStoreDialog : BottomSheetDialogFragment() {
    private val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var binding: DialogDeleteBinding

    private lateinit var deleteType: DeleteType
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
        binding = DialogDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle2.textPartColor("3건 이상", requireContext().getColor(R.color.gray80))

        initButton()
        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun initButton() {
        binding.ibClose.setOnClickListener {
            EventTracker.logEvent(Constants.DELETE_POPUP_CLOSE_BTN_CLICKED)
            dismiss()
        }
        binding.btnFinish.setOnClickListener {
            EventTracker.logEvent(Constants.DELETE_REQUEST_SUBMIT_BTN_CLICKED)
            viewModel.deleteStore(deleteType)
            dismiss()
            activity?.finish()
        }
        binding.rgReason.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_reason1 -> {
                    binding.btnFinish.isEnabled = true
                    binding.btnReason1.setTextColor(requireContext().getColor(R.color.gray100))
                    binding.btnReason2.setTextColor(requireContext().getColor(R.color.gray40))
                    binding.btnReason3.setTextColor(requireContext().getColor(R.color.gray40))
                    deleteType = DeleteType.NOSTORE
                }

                R.id.btn_reason2 -> {
                    binding.btnFinish.isEnabled = true
                    binding.btnReason1.setTextColor(requireContext().getColor(R.color.gray40))
                    binding.btnReason2.setTextColor(requireContext().getColor(R.color.gray100))
                    binding.btnReason3.setTextColor(requireContext().getColor(R.color.gray40))
                    deleteType = DeleteType.WRONGNOPOSITION
                }

                R.id.btn_reason3 -> {
                    binding.btnFinish.isEnabled = true
                    binding.btnReason1.setTextColor(requireContext().getColor(R.color.gray40))
                    binding.btnReason2.setTextColor(requireContext().getColor(R.color.gray40))
                    binding.btnReason3.setTextColor(requireContext().getColor(R.color.gray100))
                    deleteType = DeleteType.OVERLAPSTORE
                }
            }
        }
    }

    companion object {
        fun getInstance() = DeleteStoreDialog()
    }
}