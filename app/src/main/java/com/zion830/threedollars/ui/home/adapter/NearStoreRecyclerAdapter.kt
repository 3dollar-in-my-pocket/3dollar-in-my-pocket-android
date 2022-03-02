package com.zion830.threedollars.ui.home.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemHomeEmptyBinding
import com.zion830.threedollars.databinding.ItemNearStoreAdBinding
import com.zion830.threedollars.databinding.ItemStoreListAdBinding
import com.zion830.threedollars.databinding.ItemStoreLocationBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.category.adapter.SearchByAdViewHolder
import com.zion830.threedollars.ui.category.adapter.SearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.category.adapter.SearchByDistanceViewHolder
import com.zion830.threedollars.ui.mypage.adapter.bindMenuIcons
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.listener.OnItemClickListener

class NearStoreRecyclerAdapter(
    private val clickListener: OnItemClickListener<StoreInfo?>,
    private val certificationClick: (StoreInfo?) -> Unit
) : ListAdapter<AdAndStoreItem?, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {
    var focusedIndex = 0

    fun getItemLocation(position: Int) = when (getItem(position)) {
        is StoreInfo -> {
            LatLng(
                (getItem(position) as StoreInfo).latitude,
                (getItem(position) as StoreInfo).longitude
            )
        }
        else -> {
            null
        }
    }

    fun getItemPosition(item: StoreInfo) =
        currentList.indexOfFirst {
            if (it is StoreInfo) {
                it.storeId == item.storeId
            } else {
                false
            }
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is StoreInfo -> {
            VIEW_TYPE_STORE
        }
        is Popups -> {
            VIEW_TYPE_AD
        }
        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_AD -> {
            NearStoreAdViewHolder(parent)
        }
        VIEW_TYPE_STORE -> {
            NearStoreViewHolder(parent, certificationClick)
        }
        else -> {
            NearStoreEmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NearStoreViewHolder -> {
                holder.bind(getItem(position) as StoreInfo, listener = clickListener)
                holder.bindPosition(focusedIndex == position)
            }
            is NearStoreAdViewHolder -> {
                holder.bind(getItem(position) as Popups, null)
            }
            is NearStoreEmptyViewHolder -> {
                holder.bind(getItem(position) as HomeStoreEmptyResponse, null)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_AD = 1
        private const val VIEW_TYPE_STORE = 2
    }
}

class NearStoreEmptyViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemHomeEmptyBinding, HomeStoreEmptyResponse>(R.layout.item_home_empty, parent) {

    @SuppressLint("Range")
    override fun bind(
        item: HomeStoreEmptyResponse,
        listener: OnItemClickListener<HomeStoreEmptyResponse>?
    ) {
        super.bind(item, listener)
    }
}

class NearStoreAdViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemNearStoreAdBinding, Popups>(R.layout.item_near_store_ad, parent) {

    @SuppressLint("Range")
    override fun bind(item: Popups, listener: OnItemClickListener<Popups>?) {
        super.bind(item, listener)
        binding.tvAdTitle.text = item.title
        if (!item.fontColor.isNullOrEmpty()) {
            binding.tvAdTitle.setTextColor(Color.parseColor(item.fontColor))
            binding.tvAdBody.setTextColor(Color.parseColor(item.fontColor))
        }
        binding.tvAdBody.text = item.subTitle

        if (!item.bgColor.isNullOrEmpty()) {
            binding.layoutItem.setBackgroundColor(Color.parseColor(item.bgColor))
        }

        Glide.with(binding.ivAdImage)
            .load(item.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivAdImage)
    }
}

class NearStoreViewHolder(
    parent: ViewGroup?,
    private val certificationClick: (StoreInfo?) -> Unit
) :
    BaseViewHolder<ItemStoreLocationBinding, StoreInfo?>(R.layout.item_store_location, parent) {

    fun bindPosition(isSelected: Boolean) {
        binding.isSelectedItem = isSelected
        binding.tvDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isSelected) R.drawable.ic_near_line else R.drawable.ic_near,
            0,
            0,
            0
        )
        binding.tvRating.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isSelected) R.drawable.ic_star_line else R.drawable.ic_star_pink,
            0,
            0,
            0
        )
    }

    override fun bind(item: StoreInfo?, listener: OnItemClickListener<StoreInfo?>?) {
        super.bind(item, listener)

        if (item == null) {
            return
        }

        binding.item = item
        binding.tvDest.setOnClickListener {
            certificationClick(item)
        }
        val categoryInfo = SharedPrefUtils.getCategories()
        val categories =
            item.categories.joinToString(" ") { "#${categoryInfo.find { categoryInfo -> categoryInfo.category == it }?.name}" }
        binding.tvDistance.text = if (item.distance < 1000) "${item.distance}m" else "1km+"
        binding.tvStoreName.text = item.storeName
        binding.ivMenuIcon.bindMenuIcons(item.categories)
        binding.tvRating.text = "${item.rating}점"
        binding.tvCategory.text = categories
    }
}