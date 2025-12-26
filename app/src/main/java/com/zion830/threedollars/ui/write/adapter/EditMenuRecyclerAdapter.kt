package com.zion830.threedollars.ui.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import zion830.com.common.base.BaseDiffUtilCallback


class EditMenuRecyclerAdapter : ListAdapter<UserStoreMenuModel, MenuEditViewHolder>((BaseDiffUtilCallback())) {

    fun addNewRow() {
        submitList(currentList + UserStoreMenuModel(name = "", price = ""))
    }

    override fun onBindViewHolder(holder: MenuEditViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.etPrice.setOnFocusChangeListener { view, focused ->
            if (focused && position == currentList.size - 1) {
                addNewRow()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuEditViewHolder =
        MenuEditViewHolder(binding = ItemMenuEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

}

class MenuEditViewHolder(val binding: ItemMenuEditBinding) : ViewHolder(binding.root) {
    fun bind(item: UserStoreMenuModel) {
        binding.etName.setText(item.name)
        binding.etPrice.setText(item.price)
    }
}