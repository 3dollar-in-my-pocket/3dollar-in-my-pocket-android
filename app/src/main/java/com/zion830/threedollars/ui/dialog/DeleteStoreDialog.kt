package com.zion830.threedollars.ui.dialog

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
import com.home.domain.data.store.DeleteType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.threedollar.common.ext.textPartColor
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_REPORT
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogDeleteBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class DeleteStoreDialog : BaseBottomSheetDialogFragment<DialogDeleteBinding>() {
    private val viewModel: StoreDetailViewModel by activityViewModels()
    override val screenName: ScreenName = ScreenName.REPORT_STORE

    private lateinit var deleteType: DeleteType
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogDeleteBinding =
        DialogDeleteBinding.inflate(inflater, container, false)

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.maxHeight = 100
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun initView() {
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
        binding.ibClose.onSingleClick {
            dismiss()
        }
        binding.btnFinish.onSingleClick {
            viewModel.sendClickReportStore(deleteType.key)
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