package com.threedollar.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.home.domain.data.store.ReasonModel
import com.threedollar.presentation.databinding.DialogReportChoiceBinding
import zion830.com.common.base.onSingleClick

class ReportChoiceDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogReportChoiceBinding
    private val reportList = mutableListOf<ReasonModel>()
    private var reportCallBack: (ReasonModel, String?) -> Unit = { _, _ -> }
    private lateinit var choiceReasonModel: ReasonModel
    private val adapter by lazy {
        ReportChoiceAdapter(choiceClick)
    }
    private val choiceClick: (ReasonModel) -> Unit = {
        choiceReasonModel = it
        binding.etReport.isVisible = it.hasReasonDetail
        adapter.setChoiceReasonModel(choiceReasonModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogReportChoiceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setChoiceReasonModel(choiceReasonModel)
        binding.recyclerNeighbor.adapter = adapter
        adapter.submitList(reportList)
        binding.imgClose.onSingleClick { dismiss() }
        binding.twReport.onSingleClick {
            reportCallBack(choiceReasonModel, binding.etReport.text.toString())
            dismiss()
        }
    }

    fun setReportCallback(callBack: (ReasonModel, String?) -> Unit): ReportChoiceDialog {
        reportCallBack = callBack
        return this
    }

    fun setReportList(list: List<ReasonModel>): ReportChoiceDialog {
        reportList.addAll(list)
        choiceReasonModel = reportList.first()
        return this
    }
}