package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import com.zion830.threedollars.repository.model.v2.request.MyMenu
import zion830.com.common.base.BaseViewHolder


class EditMenuRecyclerAdapter : RecyclerView.Adapter<BaseViewHolder<ItemMenuEditBinding, MyMenu>>() {
    private val items = arrayListOf<MyMenu>()

    init {
        addNewRow()
    }

    fun addNewRow() {
        items.add(MyMenu())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemMenuEditBinding, MyMenu> =
        object : BaseViewHolder<ItemMenuEditBinding, MyMenu>(R.layout.item_menu_edit, parent) {}

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemMenuEditBinding, MyMenu>, position: Int) {
        holder.bind(items[position], null)
    }

    fun submitList(newItems: List<MyMenu>?) {
        items.clear()
        items.addAll(if (newItems.isNullOrEmpty()) listOf(MyMenu()) else newItems)
        notifyDataSetChanged()
    }
}