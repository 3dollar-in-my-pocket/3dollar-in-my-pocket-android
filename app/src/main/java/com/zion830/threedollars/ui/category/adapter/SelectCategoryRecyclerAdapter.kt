package com.zion830.threedollars.ui.category.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryBtnBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.v2.response.store.CategoryInfo
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class SelectCategoryRecyclerAdapter(
    private val onClick: OnItemClickListener<CategoryInfo>
) : BaseRecyclerView<ItemCategoryBtnBinding, CategoryInfo>(R.layout.item_category_btn) {

    init {
        this.clickListener = onClick
    }
}