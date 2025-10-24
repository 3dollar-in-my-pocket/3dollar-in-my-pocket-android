package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.community.data.NeighborhoodModel
import com.threedollar.presentation.databinding.ItemChoiceNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class DescriptionChoiceAdapter(private val choiceClick: (NeighborhoodModel) -> Unit) :
    ListAdapter<NeighborhoodModel, DescriptionChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var selectDistrict = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionChoiceViewHolder {
        return DescriptionChoiceViewHolder(ItemChoiceNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DescriptionChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, selectDistrict in getItem(position).districts.map { it.district })
    }

    fun setSelectNeighborhood(district: String) {
        selectDistrict = district
        notifyDataSetChanged()
    }

}

class DescriptionChoiceViewHolder(private val binding: ItemChoiceNameBinding) : ViewHolder(binding.root) {
    fun onBind(neighborhood: NeighborhoodModel, choiceClick: (NeighborhoodModel) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.clChoiceBack.isSelected = isCheck
        binding.twName.isSelected = isCheck

        binding.twName.text = neighborhood.description
        binding.root.onSingleClick { choiceClick(neighborhood) }
    }
}