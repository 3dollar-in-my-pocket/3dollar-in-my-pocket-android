package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.presentation.databinding.ItemChoiceNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class NeighborHoodsChoiceAdapter(private val choiceClick: (Neighborhoods.Neighborhood.District) -> Unit) :
    ListAdapter<Neighborhoods.Neighborhood.District, NeighborHoodsChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var choiceDistrict: Neighborhoods.Neighborhood.District? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeighborHoodsChoiceViewHolder {
        return NeighborHoodsChoiceViewHolder(ItemChoiceNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NeighborHoodsChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, choiceDistrict?.district == getItem(position).district)
    }

    fun setChoiceNeighborhood(district: Neighborhoods.Neighborhood.District) {
        choiceDistrict = district
        notifyDataSetChanged()
    }

}

class NeighborHoodsChoiceViewHolder(private val binding: ItemChoiceNameBinding) : ViewHolder(binding.root) {
    fun onBind(neighborhood: Neighborhoods.Neighborhood.District, choiceClick: (Neighborhoods.Neighborhood.District) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.twName.text = neighborhood.description
        binding.root.onSingleClick { choiceClick(neighborhood) }
    }
}