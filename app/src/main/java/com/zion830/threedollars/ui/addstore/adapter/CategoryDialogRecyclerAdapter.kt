package com.zion830.threedollars.ui.addstore.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemSelectedCategoryBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.ui.addstore.SelectedCategory
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class CategoryDialogRecyclerAdapter() : RecyclerView.Adapter<BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>>() {
    private val items: ArrayList<SelectedCategory> = ArrayList(MenuType.values().map {
        SelectedCategory(false, it)
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory> {
        return object : BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>(R.layout.item_selected_category, parent) {

        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemSelectedCategoryBinding, SelectedCategory>, position: Int) {
        return holder.bind(items[position], object : OnItemClickListener<SelectedCategory> {
            override fun onClick(item: SelectedCategory) {

            }
        })
    }
}