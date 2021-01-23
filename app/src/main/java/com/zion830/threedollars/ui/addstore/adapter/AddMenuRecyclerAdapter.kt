package com.zion830.threedollars.ui.addstore.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemMenuEditBinding
import com.zion830.threedollars.repository.model.response.Menu
import zion830.com.common.base.BaseRecyclerView


class AddMenuRecyclerAdapter : BaseRecyclerView<ItemMenuEditBinding, Menu>(R.layout.item_menu_edit) {

    init {
        addNewRow()
    }

    fun addNewRow() {
        submitList((currentList + listOf(Menu("", ""))).toMutableList())
    }

    override fun submitList(list: MutableList<Menu>?) {
        super.submitList(if (list.isNullOrEmpty()) listOf(Menu("", "")) else list)
    }
}