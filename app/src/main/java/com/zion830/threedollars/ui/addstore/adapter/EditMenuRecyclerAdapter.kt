package com.zion830.threedollars.ui.addstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.UserStoreMenuModel
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import com.zion830.threedollars.datasource.model.v2.request.MyMenu
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder


class EditMenuRecyclerAdapter : ListAdapter<UserStoreMenuModel, MenuEditViewHolder>((BaseDiffUtilCallback())) {
    private val items = arrayListOf<UserStoreMenuModel>()

    init {
        addNewRow()
    }

    fun addNewRow() {
        items.add(UserStoreMenuModel(name = "", price = ""))
        notifyItemInserted(items.size - 1)
    }

    override fun onBindViewHolder(holder: MenuEditViewHolder, position: Int) {
        holder.bind(items[position])
        holder.binding.etPrice.setOnFocusChangeListener { view, focused ->
            if (focused && position == items.size - 1) {
                addNewRow()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuEditViewHolder =
        MenuEditViewHolder(binding = ItemMenuEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

}

class MenuEditViewHolder(val binding: ItemMenuEditBinding) : ViewHolder(binding.root) {
    fun bind(item: UserStoreMenuModel) {
        binding.etName.setText(item.name)
        binding.etPrice.setText(item.price)
    }
}