package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.community.data.NeighborhoodModel
import com.threedollar.domain.community.data.Neighborhoods
import com.threedollar.presentation.databinding.ItemChoiceDistrictBinding
import com.threedollar.presentation.databinding.ItemChoiceNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class DistrictChoiceAdapter(private val choiceClick: (Neighborhoods.Neighborhood.District) -> Unit) :
    ListAdapter<Neighborhoods.Neighborhood.District, DistrictChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var selectDistrict = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictChoiceViewHolder {
        return DistrictChoiceViewHolder(ItemChoiceDistrictBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DistrictChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, selectDistrict == getItem(position).district)
    }

    fun setSelectDistrict(district: String) {
        selectDistrict = district
        notifyDataSetChanged()
    }

}

class DistrictChoiceViewHolder(private val binding: ItemChoiceDistrictBinding) : ViewHolder(binding.root) {
    fun onBind(neighborhood: Neighborhoods.Neighborhood.District, choiceClick: (Neighborhoods.Neighborhood.District) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.clChoiceBack.isSelected = isCheck
        binding.twName.isSelected = isCheck

        binding.twName.text = neighborhood.description
        binding.root.onSingleClick { choiceClick(neighborhood) }
    }
}