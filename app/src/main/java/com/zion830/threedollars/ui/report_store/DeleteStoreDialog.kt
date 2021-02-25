package com.zion830.threedollars.ui.report_store

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogDeleteBinding
import com.zion830.threedollars.ui.report_store.vm.StoreEditViewModel
import com.zion830.threedollars.utils.SharedPrefUtils


class DeleteStoreDialog : BottomSheetDialogFragment() {
    private val viewModel: StoreEditViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DialogDeleteBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.ibClose.setOnClickListener {
            dismiss()
        }
        binding.btnFinish.setOnClickListener {
            viewModel.deleteStore(SharedPrefUtils.getUserId())
            dismiss()
        }
        binding.rgReason.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.btn_reason1 -> viewModel.changeDeleteType(DeleteType.NOSTORE)
                R.id.btn_reason2 -> viewModel.changeDeleteType(DeleteType.WRONGNOPOSITION)
                R.id.btn_reason3 -> viewModel.changeDeleteType(DeleteType.OVERLAPSTORE)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    companion object {

        fun getInstance() = DeleteStoreDialog()
    }
}