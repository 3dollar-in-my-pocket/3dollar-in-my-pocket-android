package com.threedollar.presentation.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.presentation.databinding.ItemNeighborNameBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class NeighborHoodsChoiceAdapter(private val choiceClick: (Neighborhoods.Neighborhood) -> Unit) :
    ListAdapter<Neighborhoods.Neighborhood, NeighborHoodsChoiceViewHolder>(BaseDiffUtilCallback()) {

    private var choiceNeighborhood: Neighborhoods.Neighborhood? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NeighborHoodsChoiceViewHolder {
        return NeighborHoodsChoiceViewHolder(ItemNeighborNameBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NeighborHoodsChoiceViewHolder, position: Int) {
        holder.onBind(getItem(position), choiceClick, choiceNeighborhood?.province == getItem(position).province)
    }

    fun setChoiceNeighborhood(neighborhood: Neighborhoods.Neighborhood) {
        choiceNeighborhood = neighborhood
        notifyDataSetChanged()
    }

}

class NeighborHoodsChoiceViewHolder(private val binding: ItemNeighborNameBinding) : ViewHolder(binding.root) {
    fun onBind(neighborhood: Neighborhoods.Neighborhood, choiceClick: (Neighborhoods.Neighborhood) -> Unit, isCheck: Boolean) {
        binding.imgCheck.isVisible = isCheck
        binding.twName.text = neighborhood.description
        binding.root.onSingleClick { choiceClick(neighborhood) }
    }
}