package com.zion830.threedollars.ui.addstore.adapter

import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemReviewBinding
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.ext.disableDoubleClick
import zion830.com.common.listener.OnItemClickListener

class ReviewRecyclerAdapter(
    private val reviewEditClickEvent: OnItemClickListener<Review>,
    private val reviewDeleteClickEvent: OnItemClickListener<Review>,
) : ListAdapter<Review, ReviewViewHolder>(BaseDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReviewViewHolder(
        parent,
        reviewEditClickEvent.disableDoubleClick(),
        reviewDeleteClickEvent.disableDoubleClick()
    )

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position), null)
    }
}

class ReviewViewHolder(
    parent: ViewGroup,
    private val reviewEditClickEvent: OnItemClickListener<Review>,
    private val reviewDeleteClickEvent: OnItemClickListener<Review>,
) : BaseViewHolder<ItemReviewBinding, Review>(R.layout.item_review, parent) {

    override fun bind(item: Review, listener: OnItemClickListener<Review>?) {
        super.bind(item, listener)
        initMenuButton(item)
    }

    private fun initMenuButton(item: Review) {
        val popupMenu = PopupMenu(binding.ibSidemenu.context, binding.ibSidemenu, Gravity.BOTTOM, 0, R.style.PopupMenu)
        popupMenu.menuInflater.inflate(R.menu.review_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_edit_review -> {
                    reviewEditClickEvent.onClick(item)
                    false
                }
                else -> {
                    reviewDeleteClickEvent.onClick(item)
                    false
                }
            }
        }

        binding.tvCreatedAt.text = StringUtils.getTimeString(item.createdAt)
        binding.ibSidemenu.isVisible = item.user?.id == SharedPrefUtils.getUserId()
        binding.ibSidemenu.setOnClickListener {
            popupMenu.show()
        }
    }
}