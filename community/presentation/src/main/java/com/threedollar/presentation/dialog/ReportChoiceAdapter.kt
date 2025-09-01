package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.model.ReportReason
import com.threedollar.presentation.databinding.ItemChoiceNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class ReportChoiceAdapter(private val choiceClick: (ReportReason) -> Unit) :
    ListAdapter<ReportReason, ReportChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var choiceReason: ReportReason? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportChoiceViewHolder {
        return ReportChoiceViewHolder(ItemChoiceNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReportChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, choiceReason?.id == getItem(position).id)
    }

    fun setChoiceReasonModel(reasonModel: ReportReason) {
        choiceReason = reasonModel
        notifyDataSetChanged()
    }

}

class ReportChoiceViewHolder(private val binding: ItemChoiceNameBinding) : ViewHolder(binding.root) {
    fun onBind(reasonModel: ReportReason, choiceClick: (ReportReason) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.twName.text = reasonModel.title
        binding.clChoiceBack.isSelected = isCheck
        binding.root.onSingleClick { choiceClick(reasonModel) }
    }
}