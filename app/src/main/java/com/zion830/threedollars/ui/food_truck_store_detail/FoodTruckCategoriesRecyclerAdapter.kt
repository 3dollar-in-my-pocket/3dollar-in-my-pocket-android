package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckCategoryBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossStoreDetailModel
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import com.threedollar.common.listener.OnItemClickListener

class FoodTruckCategoriesRecyclerAdapter : ListAdapter<BossStoreDetailModel.Category, FoodTruckCategoriesViewHolder>(
    BaseDiffUtilCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FoodTruckCategoriesViewHolder(parent)

    override fun onBindViewHolder(holder: FoodTruckCategoriesViewHolder, position: Int) {
        holder.bind(getItem(position), listener = null)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class FoodTruckCategoriesViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemFoodTruckCategoryBinding, BossStoreDetailModel.Category>(
        R.layout.item_food_truck_category,
        parent
    ) {
    @SuppressLint("Range")
    override fun bind(
        item: BossStoreDetailModel.Category,
        listener: OnItemClickListener<BossStoreDetailModel.Category>?
    ) {
        super.bind(item, listener)
    }
}