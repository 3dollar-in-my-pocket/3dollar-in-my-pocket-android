package com.zion830.threedollars.ui.home.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryBtnBinding
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.loadUrlImg

class SelectCategoryRecyclerAdapter(
    private val onCategoryClickListener: (CategoryModel) -> Unit,
    private val onAdClickListener: (AdvertisementModelV2) -> Unit,
) :
    ListAdapter<AdAndStoreItem, ViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            VIEW_TYPE_MENU ->
                CategoryViewHolder(
                    parent = parent,
                    onClickListener = onCategoryClickListener
                )

            else -> AdCategoryViewHolder(
                parent = parent,
                onClickListener = onAdClickListener
            )
        }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        when (holder) {
            is CategoryViewHolder -> {
                holder.bind(item as CategoryModel, listener = null)
            }

            is AdCategoryViewHolder -> {
                holder.bind(item as AdvertisementModelV2, listener = null)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is CategoryModel -> {
            VIEW_TYPE_MENU
        }

        else -> {
            VIEW_TYPE_AD
        }

    }

    companion object {
        private const val VIEW_TYPE_MENU = 1
        private const val VIEW_TYPE_AD = 2
    }
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

class AdCategoryViewHolder(parent: ViewGroup, private val onClickListener: (AdvertisementModelV2) -> Unit) :
    BaseViewHolder<ItemCategoryBtnBinding, AdvertisementModelV2>(R.layout.item_category_btn, parent) {

    override fun bind(item: AdvertisementModelV2, listener: OnItemClickListener<AdvertisementModelV2>?) {
        binding.run {
            root.setOnClickListener { onClickListener(item) }
            categoryImageView.loadUrlImg(item.image.url)
            categoryNameTextView.text = item.title.content
        }
    }
}