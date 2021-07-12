package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import com.zion830.threedollars.repository.model.response.Category
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class EditCategoryMenuRecyclerAdapter(
    private val onCategoryDeleted: OnItemClickListener<Category>
) : RecyclerView.Adapter<BaseViewHolder<ItemEditCategoryMenuBinding, Category>>() {
    private val items = arrayListOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemEditCategoryMenuBinding, Category>(R.layout.item_edit_category_menu, parent) {
            override fun bind(item: Category, listener: OnItemClickListener<Category>?) {
                super.bind(item, listener)
                binding.rvMenu.adapter = EditMenuRecyclerAdapter()
            }
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemEditCategoryMenuBinding, Category>, position: Int) {
        holder.bind(items[position], onCategoryDeleted)
    }
}