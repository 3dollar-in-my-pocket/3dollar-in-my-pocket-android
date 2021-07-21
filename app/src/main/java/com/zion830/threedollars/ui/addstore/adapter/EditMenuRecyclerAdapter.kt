package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import com.zion830.threedollars.repository.model.response.Menu
import zion830.com.common.base.BaseViewHolder


class EditMenuRecyclerAdapter : RecyclerView.Adapter<BaseViewHolder<ItemMenuEditBinding, Menu>>() {
    private val items = arrayListOf<Menu>()

    init {
        addNewRow()
    }

    fun addNewRow() {
        items.add(Menu())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemMenuEditBinding, Menu> =
        object : BaseViewHolder<ItemMenuEditBinding, Menu>(R.layout.item_menu_edit, parent) {}

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMenuEditBinding, Menu>, position: Int) {
        holder.bind(items[position], null)
    }

    fun submitList(newItems: MutableList<Menu>?) {
        items.clear()
        items.addAll(if (newItems.isNullOrEmpty()) listOf(Menu("", 0)) else newItems)
        notifyDataSetChanged()
    }
}