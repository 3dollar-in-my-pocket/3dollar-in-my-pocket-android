package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryAddBinding
import com.zion830.threedollars.databinding.ItemSelectedCategoryBinding
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import com.threedollar.common.listener.OnItemClickListener

class AddCategoryRecyclerAdapter(
    private val showDialog: () -> Unit,
    private val onDeleted: (SelectedCategory) -> Unit
) : ListAdapter<SelectedCategory?, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    init {
        submitList(listOf())
    }

    fun getCategory(position: Int) = currentList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_ADD -> AddCategoryViewHolder(parent)
            else -> DeleteCategoryViewHolder(parent)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddCategoryViewHolder -> {
                holder.bind("", object : OnItemClickListener<String> {
                    override fun onClick(item: String) {
                        showDialog()
                    }
                })
            }
            is DeleteCategoryViewHolder -> {
                holder.bind(
                    getItem(position) ?: SelectedCategory(),
                    object : OnItemClickListener<SelectedCategory> {
                        override fun onClick(item: SelectedCategory) {
                            if (item.isSelected) {
                                onDeleted(item)
                            }
                        }
                    })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD else VIEW_TYPE_DELETE
    }

    override fun submitList(list: List<SelectedCategory?>?) {
        if (list.isNullOrEmpty()) {
            super.submitList(listOf(null))
        } else {
            super.submitList(listOf(null) + list)
        }
    }

    fun clear() {
        submitList(listOf())
    }

    fun getSelectedItems(): List<String> {
        return currentList.filter { it?.isSelected == true }.map { it?.menuType?.category ?: "BUNGEOPPANG" }
    }

    companion object {
        private const val VIEW_TYPE_DELETE = 1
        private const val VIEW_TYPE_ADD = 2
    }
}

class AddCategoryViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemCategoryAddBinding, String>(R.layout.item_category_add, parent) {

}

class DeleteCategoryViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>(
        R.layout.item_selected_category,
        parent
    ) {
}