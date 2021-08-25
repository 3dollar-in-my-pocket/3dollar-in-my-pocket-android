package com.zion830.threedollars.ui.store_detail.adapter

import android.view.ViewGroup
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryBinding
import com.zion830.threedollars.repository.model.response.Category
import com.zion830.threedollars.ui.addstore.adapter.MenuRecyclerAdapter
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class CategoryInfoRecyclerAdapter : BaseRecyclerView<ItemCategoryBinding, Category>(R.layout.item_category) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemCategoryBinding, Category> {
        return object : BaseViewHolder<ItemCategoryBinding, Category>(R.layout.item_category, parent) {
            override fun bind(item: Category, listener: OnItemClickListener<Category>?) {
                super.bind(item, listener)
                binding.rvMenu.adapter = MenuRecyclerAdapter().apply {
                    submitList(item.menu)
                }
            }
        }
    }
}