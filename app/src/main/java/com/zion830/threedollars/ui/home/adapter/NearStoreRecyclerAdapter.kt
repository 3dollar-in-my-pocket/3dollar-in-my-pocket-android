package com.zion830.threedollars.ui.home.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.dpToPx
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemBossStoreLocationBinding
import com.zion830.threedollars.databinding.ItemHomeEmptyBinding
import com.zion830.threedollars.databinding.ItemNearStoreAdBinding
import com.zion830.threedollars.databinding.ItemStoreLocationBinding
import com.zion830.threedollars.repository.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.repository.model.v2.response.HomeStoreEmptyResponse
import com.zion830.threedollars.repository.model.v2.response.Popups
import com.zion830.threedollars.repository.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.mypage.adapter.bindMenuIcons
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.BaseViewHolder
import zion830.com.common.base.convertDpToPx
import zion830.com.common.base.loadUrlImg
import zion830.com.common.listener.OnItemClickListener


class NearStoreRecyclerAdapter(
    private val clickListener: OnItemClickListener<StoreInfo?>,
    private val bossClickListener: OnItemClickListener<BossNearStoreResponse.BossNearStoreModel?>,
    private val adClickListener: OnItemClickListener<Popups>,
    private val certificationClick: (StoreInfo?) -> Unit
) : ListAdapter<AdAndStoreItem?, RecyclerView.ViewHolder>(BaseDiffUtilCallback()) {
    var focusedIndex = 0
    var isAd = false

    fun getItemLocation(position: Int) = when (getItem(position)) {
        is StoreInfo -> {
            LatLng(
                (getItem(position) as StoreInfo).latitude,
                (getItem(position) as StoreInfo).longitude
            )
        }
        is BossNearStoreResponse.BossNearStoreModel -> {
            LatLng(
                (getItem(position) as BossNearStoreResponse.BossNearStoreModel).location.latitude,
                (getItem(position) as BossNearStoreResponse.BossNearStoreModel).location.longitude
            )
        }
        else -> {
            null
        }
    }

    fun isFoodTruckOpen(position: Int): Boolean {
        return when (getItem(position)) {
            is BossNearStoreResponse.BossNearStoreModel -> {
                (getItem(position) as BossNearStoreResponse.BossNearStoreModel).openStatus?.status == "CLOSED"
            }
            else -> {
                false
            }
        }
    }

    fun getItemPosition(item: AdAndStoreItem) =
        currentList.indexOfFirst {
            if (it is StoreInfo && item is StoreInfo) {
                it.storeId == item.storeId
            } else if (it is BossNearStoreResponse.BossNearStoreModel && item is BossNearStoreResponse.BossNearStoreModel) {
                it.bossStoreId == item.bossStoreId
            } else {
                false
            }
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is StoreInfo -> {
            VIEW_TYPE_ROAD_FOOD_STORE
        }
        is BossNearStoreResponse.BossNearStoreModel -> {
            VIEW_TYPE_FOOD_TRUCK_STORE
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
        VIEW_TYPE_ROAD_FOOD_STORE -> {
            NearStoreViewHolder(parent, certificationClick)
        }
        VIEW_TYPE_FOOD_TRUCK_STORE -> {
            BossNearStoreViewHolder(parent)
        }
        else -> {
            NearStoreEmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NearStoreViewHolder -> {
                holder.bind(getItem(position) as StoreInfo, listener = clickListener)
                if (isAd) {
                    holder.bindPosition(if (focusedIndex == 0) focusedIndex == position else focusedIndex + 1 == position)
                } else {
                    holder.bindPosition(focusedIndex == position)
                }
            }
            is NearStoreAdViewHolder -> {
                holder.bind(getItem(position) as Popups, adClickListener)
            }
            is NearStoreEmptyViewHolder -> {
                holder.bind(getItem(position) as HomeStoreEmptyResponse, null)
            }
            is BossNearStoreViewHolder -> {
                holder.bind(
                    getItem(position) as BossNearStoreResponse.BossNearStoreModel,
                    listener = bossClickListener
                )
                if (isAd) {
                    holder.bindPosition(if (focusedIndex == 0) focusedIndex == position else focusedIndex + 1 == position)
                } else {
                    holder.bindPosition(focusedIndex == position)
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_AD = 1
        private const val VIEW_TYPE_ROAD_FOOD_STORE = 2
        private const val VIEW_TYPE_FOOD_TRUCK_STORE = 3
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
        val params = binding.storeImageView.layoutParams
        params.width = GlobalApplication.getContext()
            .convertDpToPx(if (item.emptyImage == R.drawable.ic_no_store) 75f else 45f).toInt()
        params.height = GlobalApplication.getContext()
            .convertDpToPx(if (item.emptyImage == R.drawable.ic_no_store) 75f else 45f).toInt()
        binding.storeImageView.layoutParams = params
    }
}

class NearStoreAdViewHolder(parent: ViewGroup) :
    BaseViewHolder<ItemNearStoreAdBinding, Popups>(R.layout.item_near_store_ad, parent) {

    @SuppressLint("Range")
    override fun bind(item: Popups, listener: OnItemClickListener<Popups>?) {
        super.bind(item, listener)
        if (!item.fontColor.isNullOrEmpty()) {
            binding.tvAdTitle.setTextColor(item.fontColor.toColorInt())
            binding.tvAdBody.setTextColor(item.fontColor.toColorInt())
        }

        if (!item.bgColor.isNullOrEmpty()) {
            binding.layoutItem.setBackgroundColor(item.bgColor.toColorInt())
        }
        binding.ivAdImage.loadUrlImg(item.imageUrl)
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

class BossNearStoreViewHolder(parent: ViewGroup?) :
    BaseViewHolder<ItemBossStoreLocationBinding, BossNearStoreResponse.BossNearStoreModel?>(
        R.layout.item_boss_store_location,
        parent
    ) {

    fun bindPosition(isSelected: Boolean) {
        binding.isSelectedItem = isSelected
        binding.tvDistance.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isSelected) R.drawable.ic_near_line else R.drawable.ic_near_green,
            0,
            0,
            0
        )
        binding.tvReviewCount.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isSelected) R.drawable.ic_review_white else R.drawable.ic_review_green,
            0,
            0,
            0
        )
    }

    override fun bind(
        item: BossNearStoreResponse.BossNearStoreModel?,
        listener: OnItemClickListener<BossNearStoreResponse.BossNearStoreModel?>?
    ) {
        super.bind(item, listener)

        if (item == null) {
            return
        }

        binding.item = item
        if (item.openStatus?.status == "CLOSED") {
            binding.readyTextView.isVisible = true
            binding.ivMenuIcon.alpha = 0.4f
        } else {
            binding.readyTextView.isVisible = false
            binding.ivMenuIcon.alpha = 1.0f
        }

        if (item.categories.isNotEmpty()) {
            binding.ivMenuIcon.loadUrlImg(item.categories[0].imageUrl)
        }
        val categories = item.categories.joinToString(" ") { it.name.toString() }
        binding.tvDistance.text = if (item.distance < 1000) "${item.distance}m" else "1km+"
        binding.tvReviewCount.text = "${item.totalFeedbacksCounts}개"
        binding.tvStoreName.text = item.name
        binding.tvCategory.text = "#${categories}"

    }
}