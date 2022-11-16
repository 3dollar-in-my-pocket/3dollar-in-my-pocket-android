package com.zion830.threedollars.ui.home.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemHomeBossCategoryBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossCategoriesResponse
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class BossCategoriesRecyclerAdapter(
    private val clickListener: (BossCategoriesResponse.BossCategoriesModel) -> Unit
) : ListAdapter<BossCategoriesResponse.BossCategoriesModel, BossCategoriesViewHolder>(
    BaseDiffUtilCallback()
) {

    private var selectPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BossCategoriesViewHolder(parent)

    override fun onBindViewHolder(holder: BossCategoriesViewHolder, position: Int) {
        holder.bind(getItem(position), listener = null)
        holder.itemView.setOnClickListener {
            clickListener(getItem(position))
            notifyItemChanged(holder.bindingAdapterPosition)
            notifyItemChanged(selectPosition)
            selectPosition = holder.bindingAdapterPosition
        }
        holder.setSingleSelectPosition(selectPosition)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class BossCategoriesViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemHomeBossCategoryBinding, BossCategoriesResponse.BossCategoriesModel>(
        R.layout.item_home_boss_category,
        parent
    ) {
    @SuppressLint("Range")
    override fun bind(
        item: BossCategoriesResponse.BossCategoriesModel,
        listener: OnItemClickListener<BossCategoriesResponse.BossCategoriesModel>?
    ) {
        super.bind(item, listener)
    }

    fun setSingleSelectPosition(selectPosition: Int) {
        if (bindingAdapterPosition == selectPosition) {
            binding.categoryNameTextView.setBackgroundResource(R.drawable.rect_green_radius16)
            binding.categoryNameTextView.setTextColor(
                ContextCompat.getColor(
                    GlobalApplication.getContext(),
                    R.color.white
                )
            )
        } else {
            binding.categoryNameTextView.setBackgroundResource(R.drawable.rect_white_radius16)
            binding.categoryNameTextView.setTextColor(
                ContextCompat.getColor(
                    GlobalApplication.getContext(),
                    R.color.gray30
                )
            )
        }
    }
}