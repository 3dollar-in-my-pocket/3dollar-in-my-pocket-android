package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class EditCategoryMenuRecyclerAdapter(
    private val delete: (SelectedCategory) -> Unit
) : RecyclerView.Adapter<BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>>() {
    private val items = arrayListOf<SelectedCategory>()

    fun setItems(items: List<SelectedCategory>?) {
        if (items.isNullOrEmpty()) {
            return
        }

        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun remove(item: SelectedCategory) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>(R.layout.item_edit_category_menu, parent) {
            override fun bind(item: SelectedCategory, listener: OnItemClickListener<SelectedCategory>?) {
                super.bind(item, listener)
                binding.rvMenuEdit.adapter = EditMenuRecyclerAdapter()
            }
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>, position: Int) {
        holder.bind(
            items[position],
            object : OnItemClickListener<SelectedCategory> {
                override fun onClick(item: SelectedCategory) {
                    items.removeAt(items.indexOf(item))
                    delete(item)
                    notifyItemChanged(position)
                }
            })
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }
}