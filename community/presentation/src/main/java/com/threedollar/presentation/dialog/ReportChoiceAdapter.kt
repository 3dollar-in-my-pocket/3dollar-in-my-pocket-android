package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.ReasonModel
import com.threedollar.presentation.databinding.ItemChoiceNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class ReportChoiceAdapter(private val choiceClick: (ReasonModel) -> Unit) :
    ListAdapter<ReasonModel, ReportChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var choiceReason: ReasonModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportChoiceViewHolder {
        return ReportChoiceViewHolder(ItemChoiceNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ReportChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, choiceReason?.type == getItem(position).type)
    }

    fun setChoiceReasonModel(reasonModel: ReasonModel) {
        choiceReason = reasonModel
        notifyDataSetChanged()
    }

}

class ReportChoiceViewHolder(private val binding: ItemChoiceNameBinding) : ViewHolder(binding.root) {
    fun onBind(reasonModel: ReasonModel, choiceClick: (ReasonModel) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.twName.text = reasonModel.description
        binding.root.onSingleClick { choiceClick(reasonModel) }
    }
}