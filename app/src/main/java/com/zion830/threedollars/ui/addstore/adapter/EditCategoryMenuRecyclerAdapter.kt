package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import zion830.com.common.base.BaseViewHolder
import com.threedollar.common.listener.OnItemClickListener

class EditCategoryMenuRecyclerAdapter(
    private val delete: (SelectedCategory) -> Unit
) : ListAdapter<SelectedCategory, BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>>(
    object : DiffUtil.ItemCallback<SelectedCategory>() {
        override fun areItemsTheSame(oldItem: SelectedCategory, newItem: SelectedCategory): Boolean {
            return oldItem.menuType == newItem.menuType
        }

        override fun areContentsTheSame(oldItem: SelectedCategory, newItem: SelectedCategory): Boolean {
            return oldItem.menuType == newItem.menuType
        }
    }
) {
    val items = arrayListOf<SelectedCategory>()

    fun setItems(items: List<SelectedCategory>?) {
        this.items.clear()
        this.items.addAll(items ?: listOf())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>(
            R.layout.item_edit_category_menu,
            parent
        ) {
            override fun bind(
                item: SelectedCategory,
                listener: OnItemClickListener<SelectedCategory>?
            ) {
                super.bind(item, listener)
                binding.rvMenuEdit.adapter = EditMenuRecyclerAdapter().apply {
                    submitList(item.menuDetail)
                }
            }
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>,
        position: Int
    ) {
        holder.bind(
            items[position],
            object : OnItemClickListener<SelectedCategory> {
                override fun onClick(item: SelectedCategory) {
                    items.removeAt(items.indexOf(item))
                    delete(item)
                    notifyDataSetChanged()
                }
            })
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }
}