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
    private val _items = arrayListOf<SelectedCategory>()
    val items = _items.toList()

    fun setItems(items: List<SelectedCategory>?) {
        this._items.clear()
        this._items.addAll(items ?: listOf())
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
                    submitList(item.menuDetail?.toMutableList())
                }
            }
        }

    override fun getItemCount(): Int = _items.size

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemEditCategoryMenuBinding, SelectedCategory>,
        position: Int
    ) {
        holder.bind(
            _items[position],
            object : OnItemClickListener<SelectedCategory> {
                override fun onClick(item: SelectedCategory) {
                    _items.removeAt(_items.indexOf(item))
                    delete(item)
                    notifyDataSetChanged()
                }
            })
    }

    fun clear() {
        _items.clear()
        notifyDataSetChanged()
    }
}