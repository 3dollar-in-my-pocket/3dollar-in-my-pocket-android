package com.zion830.threedollars.ui.addstore.adapter

import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.ListAdapter
import com.threedollar.common.ext.disableDoubleClick
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemReviewBinding
import com.zion830.threedollars.datasource.model.v2.response.my.Review
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder

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

        binding.tvCreatedAt.text = StringUtils.getTimeString(item.createdAt, "yy.MM.dd E")
        binding.ibSidemenu.isInvisible = item.user.userId != LegacySharedPrefUtils.getUserId()
        binding.ibSidemenu.setOnClickListener {
            popupMenu.show()
        }
    }
}