package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryAddBinding
import com.zion830.threedollars.databinding.ItemSelectedCategoryBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class AddCategoryRecyclerAdapter(
    private val showDialog: () -> Unit
) : ListAdapter<String?, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {

    init {
        submitList(null)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_ADD -> AddCategoryViewHolder(parent)
        else -> DeleteCategoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddCategoryViewHolder -> {
                holder.bind(getItem(position), object : OnItemClickListener<String?> {
                    override fun onClick(item: String?) {
                        showDialog()
                    }
                })
            }
            is DeleteCategoryViewHolder -> {
                holder.bind(getItem(position), object : OnItemClickListener<String?> {
                    override fun onClick(item: String?) {
                        submitList(currentList - listOf(item))
                    }
                })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_ADD else VIEW_TYPE_DELETE
    }

    override fun submitList(items: List<String?>?) {
        if (items == null) {
            super.submitList(listOf(null))
        } else {
            super.submitList(items)
        }
    }

    companion object {
        private const val VIEW_TYPE_DELETE = 1
        private const val VIEW_TYPE_ADD = 2
    }
}

class AddCategoryViewHolder(parent: ViewGroup) : BaseViewHolder<ItemCategoryAddBinding, String?>(R.layout.item_category_add, parent) {

}

class DeleteCategoryViewHolder(parent: ViewGroup) : BaseViewHolder<ItemSelectedCategoryBinding, String?>(R.layout.item_selected_category, parent) {

}