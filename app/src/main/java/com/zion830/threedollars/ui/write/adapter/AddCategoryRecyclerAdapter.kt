package com.zion830.threedollars.ui.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.store.AddCategoryModel
import com.home.domain.data.store.CategoryItem
import com.home.domain.data.store.CategoryModel
import com.threedollar.common.ext.loadImage
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemCategoryAddBinding
import com.zion830.threedollars.databinding.ItemSelectedCategoryBinding
import zion830.com.common.base.BaseDiffUtilCallback

class AddCategoryRecyclerAdapter(
    private val showDialog: () -> Unit,
    private val onDeleted: (CategoryModel) -> Unit
) : ListAdapter<CategoryItem?, ViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            VIEW_TYPE_ADD -> AddCategoryViewHolder(
                binding = ItemCategoryAddBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickListener = showDialog
            )
            else -> DeleteCategoryViewHolder(
                binding = ItemSelectedCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                onClickListener = onDeleted
            )
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is AddCategoryViewHolder -> {
                holder.bind(getItem(position) as AddCategoryModel)
            }
            is DeleteCategoryViewHolder -> {
                holder.bind(getItem(position) as CategoryModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is CategoryModel -> {
            VIEW_TYPE_DELETE
        }

        else -> {
            VIEW_TYPE_ADD
        }
    }

    companion object {
        private const val VIEW_TYPE_DELETE = 1
        private const val VIEW_TYPE_ADD = 2
    }
}

class AddCategoryViewHolder(private val binding: ItemCategoryAddBinding, private val onClickListener: () -> Unit) : ViewHolder(binding.root) {
    fun bind(item: AddCategoryModel) {
        if (item.isEnabled) {
            binding.addImageView.setBackgroundResource(R.drawable.circle_gray100)
            binding.addImageView.setImageResource(R.drawable.ic_plus)
            binding.addTextView.setTextColor(GlobalApplication.getContext().getColor(R.color.gray80))
            binding.btnAddNew.setOnClickListener { onClickListener() }
        } else {
            binding.addImageView.setBackgroundResource(R.drawable.circle_solid_gray30)
            binding.addImageView.setImageResource(R.drawable.ic_plus_gray10)
            binding.addTextView.setTextColor(GlobalApplication.getContext().getColor(R.color.gray40))
        }
    }
}

class DeleteCategoryViewHolder(private val binding: ItemSelectedCategoryBinding, private val onClickListener: (CategoryModel) -> Unit) :
    ViewHolder(binding.root) {
    fun bind(item: CategoryModel) {
        binding.menuNameTextView.text = item.name
        binding.menuImageView.loadImage(item.imageUrl)
        binding.ibDeleteCategory.setOnClickListener { onClickListener(item) }
    }
}