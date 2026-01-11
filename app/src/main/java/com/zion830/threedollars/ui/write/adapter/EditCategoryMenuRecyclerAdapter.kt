package com.zion830.threedollars.ui.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.SelectCategoryModel
import com.threedollar.domain.home.data.store.UserStoreMenuModel
import com.threedollar.common.ext.loadImage
import com.zion830.threedollars.databinding.ItemEditCategoryMenuBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick

class EditCategoryMenuRecyclerAdapter(private val delete: (CategoryModel) -> Unit) :
    ListAdapter<SelectCategoryModel, EditCategoryMenuViewHolder>((BaseDiffUtilCallback())) {
    val items = arrayListOf<SelectCategoryModel>()
    private val adapterMap = mutableMapOf<String, EditMenuRecyclerAdapter>()

    fun setItems(items: List<SelectCategoryModel>?) {
        this.items.clear()
        this.items.addAll(items ?: listOf())

        val currentCategoryIds = this.items.map { it.menuType.categoryId }.toSet()
        adapterMap.keys.filter { it !in currentCategoryIds }.forEach { adapterMap.remove(it) }

        notifyDataSetChanged()
    }

    fun getOrCreateAdapter(categoryId: String): EditMenuRecyclerAdapter {
        return adapterMap.getOrPut(categoryId) { EditMenuRecyclerAdapter() }
    }

    fun getMenuDataForCategory(categoryId: String): List<EditMenuRecyclerAdapter.MutableMenuData> {
        return adapterMap[categoryId]?.getMenuData() ?: emptyList()
    }

    fun getAllMenuData(): Map<String, List<EditMenuRecyclerAdapter.MutableMenuData>> {
        return items.associate { it.menuType.categoryId to getMenuDataForCategory(it.menuType.categoryId) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EditCategoryMenuViewHolder(
            binding = ItemEditCategoryMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickListener = delete
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EditCategoryMenuViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item) { categoryId -> getOrCreateAdapter(categoryId) }
    }
}

class EditCategoryMenuViewHolder(
    private val binding: ItemEditCategoryMenuBinding,
    private val onClickListener: (CategoryModel) -> Unit
) : ViewHolder(binding.root) {

    fun bind(item: SelectCategoryModel, adapterProvider: (String) -> EditMenuRecyclerAdapter) {
        binding.ibDeleteCategory.onSingleClick { onClickListener(item.menuType) }
        binding.ivMenuIcon.loadImage(item.menuType.imageUrl)
        binding.tvMenu.text = item.menuType.name

        val adapter = adapterProvider(item.menuType.categoryId)
        binding.rvMenuEdit.adapter = adapter

        val menuItems = if (item.menuDetail.isNullOrEmpty()) {
            listOf(UserStoreMenuModel(name = "", price = ""))
        } else {
            item.menuDetail!!
        }
        adapter.setMenuData(menuItems)
    }
}
