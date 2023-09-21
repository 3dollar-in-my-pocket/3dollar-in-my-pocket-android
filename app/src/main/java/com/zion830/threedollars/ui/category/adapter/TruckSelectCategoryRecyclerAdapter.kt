package com.zion830.threedollars.ui.category.adapter

import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemTruckCategoryBtnBinding
import com.zion830.threedollars.datasource.model.v2.response.store.PlatformStoreFoodCategoryResponse
import zion830.com.common.base.BaseRecyclerView
import zion830.com.common.listener.OnItemClickListener

class TruckSelectCategoryRecyclerAdapter(
    private val onClick: OnItemClickListener<PlatformStoreFoodCategoryResponse.Data>
) : BaseRecyclerView<ItemTruckCategoryBtnBinding, PlatformStoreFoodCategoryResponse.Data>(R.layout.item_truck_category_btn) {

    init {
        this.clickListener = onClick
    }
}