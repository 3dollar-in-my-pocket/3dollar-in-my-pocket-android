package com.zion830.threedollars.ui.category.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.home.domain.data.store.CategoryModel
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryBtnBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg

class SelectCategoryRecyclerAdapter(private val onClickListener: (CategoryModel) -> Unit) :
    ListAdapter<CategoryModel, CategoryViewHolder>(BaseDiffUtilCallback()) {
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) = holder.bind(getItem(position), listener = null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(parent, onClickListener)
}

class CategoryViewHolder(parent: ViewGroup, private val onClickListener: (CategoryModel) -> Unit) :
    BaseViewHolder<ItemCategoryBtnBinding, CategoryModel>(R.layout.item_category_btn, parent) {

    override fun bind(item: CategoryModel, listener: OnItemClickListener<CategoryModel>?) {
        binding.run {
            root.setOnClickListener { onClickListener(item.copy(isSelected = !item.isSelected)) }
            categoryImageView.loadUrlImg(item.imageUrl)
            categoryNameTextView.text = item.name
            newImageView.isVisible = item.isNew
            selectCircleImageView.isVisible = item.isSelected
            if (item.isSelected) {
                categoryNameTextView.setTextColor(GlobalApplication.getContext().getColor(R.color.pink))
            } else {
                categoryNameTextView.setTextColor(GlobalApplication.getContext().getColor(R.color.gray70))
            }
        }
    }
}