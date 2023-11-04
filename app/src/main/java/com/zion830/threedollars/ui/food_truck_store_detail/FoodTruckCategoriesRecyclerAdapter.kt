package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.home.domain.data.store.CategoryModel
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemFoodTruckCategoryBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

class FoodTruckCategoriesRecyclerAdapter : ListAdapter<CategoryModel, FoodTruckCategoriesViewHolder>(
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
    BaseViewHolder<ItemFoodTruckCategoryBinding, CategoryModel>(
        R.layout.item_food_truck_category,
        parent
    ) {
    @SuppressLint("Range")
    override fun bind(
        item: CategoryModel,
        listener: OnItemClickListener<CategoryModel>?
    ) {
        super.bind(item, listener)
    }
}