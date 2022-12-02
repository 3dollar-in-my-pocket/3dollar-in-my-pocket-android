package com.zion830.threedollars.ui.category.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemStreetCategoryBtnBinding
import com.zion830.threedollars.datasource.model.v2.response.store.CategoryInfo
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class StreetSelectCategoryRecyclerAdapter(
    private val onClick: OnItemClickListener<CategoryInfo>
) : BaseRecyclerView<ItemStreetCategoryBtnBinding, CategoryInfo>(R.layout.item_street_category_btn) {

    init {
        this.clickListener = onClick
    }
}