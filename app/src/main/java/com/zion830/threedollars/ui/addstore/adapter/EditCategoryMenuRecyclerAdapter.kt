package com.zion830.threedollars.ui.addstore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.CategoryModel
import com.home.domain.data.store.SelectCategoryModel
import com.home.domain.data.store.UserStoreMenuModel
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import com.zion830.threedollars.generated.callback.OnClickListener
import com.zion830.threedollars.ui.addstore.ui_model.SelectedCategory
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class EditCategoryMenuRecyclerAdapter(private val delete: (CategoryModel) -> Unit) :
    ListAdapter<SelectCategoryModel, EditCategoryMenuViewHolder>((BaseDiffUtilCallback())) {
    val items = arrayListOf<SelectCategoryModel>()

    fun setItems(items: List<SelectCategoryModel>?) {
        this.items.clear()
        this.items.addAll(items ?: listOf())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EditCategoryMenuViewHolder(
            binding = ItemEditCategoryMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickListener = delete
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EditCategoryMenuViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class EditCategoryMenuViewHolder(private val binding: ItemEditCategoryMenuBinding, private val onClickListener: (CategoryModel) -> Unit) :
    ViewHolder(binding.root) {
    fun bind(item: SelectCategoryModel) {
        binding.ibDeleteCategory.setOnClickListener { onClickListener(item.menuType) }
        binding.ivMenuIcon.loadImage(item.menuType.imageUrl)
        binding.tvMenu.text = item.menuType.name
        binding.rvMenuEdit.adapter = EditMenuRecyclerAdapter().apply {
            submitList(item.menuDetail)
        }
    }
}
