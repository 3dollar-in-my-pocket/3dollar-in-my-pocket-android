package com.zion830.threedollars.ui.community.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.threedollar.domain.community.model.ReportReason
import com.threedollar.common.base.BaseBottomSheetDialogFragment
import com.zion830.threedollars.databinding.DialogReportChoiceBinding
import zion830.com.common.base.onSingleClick

class ReportChoiceDialog : BaseBottomSheetDialogFragment<DialogReportChoiceBinding>() {
    private val reportList = mutableListOf<ReportReason>()
    private var reportCallBack: (ReportReason, String?) -> Unit = { _, _ -> }
    private lateinit var choiceReasonModel: ReportReason
    private var type = Type.POLL
    private val adapter by lazy {
        ReportChoiceAdapter(choiceClick)
    }
    private val choiceClick: (ReportReason) -> Unit = {
        choiceReasonModel = it
        binding.etReport.isVisible = it.hasDetail
        adapter.setChoiceReasonModel(choiceReasonModel)
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogReportChoiceBinding =
        DialogReportChoiceBinding.inflate(inflater, container, false)


    override fun initView() {
        initAdapter()
        initButton()
    }

    private fun initAdapter() {
        adapter.setChoiceReasonModel(choiceReasonModel)
        binding.recyclerNeighbor.adapter = adapter
        adapter.submitList(reportList)
    }

    private fun initButton() {
        binding.imgClose.onSingleClick { dismiss() }
        binding.twReport.onSingleClick {
            reportCallBack(choiceReasonModel, binding.etReport.text.toString())
            dismiss()
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(
            className = "ReportChoiceDialog", screenName = when (type) {
                Type.POLL -> "report_poll"
                Type.COMMENT -> "report_review"
            }
        )
    }

    override fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun setReportCallback(callBack: (ReportReason, String?) -> Unit): ReportChoiceDialog {
        reportCallBack = callBack
        return this
    }

    fun setType(type: Type): ReportChoiceDialog {
        this.type = type
        return this
    }

    fun setReportList(list: List<ReportReason>): ReportChoiceDialog {
        reportList.addAll(list)
        choiceReasonModel = reportList.first()
        return this
    }

    enum class Type {
        POLL, COMMENT
    }
}