package com.zion830.threedollars.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.ContentModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants.USER_STORE
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemHomeEmptyBinding
import com.zion830.threedollars.databinding.ItemNearStoreAdBinding
import com.zion830.threedollars.databinding.ItemStoreLocationBinding
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback


class AroundStoreMapViewRecyclerAdapter(
    private val clickListener: OnItemClickListener<ContentModel>,
    private val adClickListener: OnItemClickListener<AdvertisementModel>,
    private val certificationClick: (ContentModel) -> Unit
) : ListAdapter<AdAndStoreItem, ViewHolder>(BaseDiffUtilCallback()) {
    var focusedIndex = 0

    fun getItemLocation(position: Int) = when (getItem(position)) {
        is ContentModel -> {
            LatLng(
                (getItem(position) as ContentModel).storeModel.locationModel.latitude,
                (getItem(position) as ContentModel).storeModel.locationModel.longitude
            )
        }
        else -> {
            null
        }
    }

    fun getItemPosition(item: AdAndStoreItem) = currentList.indexOfFirst {
        if (it is ContentModel && item is ContentModel) {
            it.storeModel.storeId == item.storeModel.storeId
        } else {
            false
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ContentModel -> {
            VIEW_TYPE_STORE
        }
        is AdvertisementModel -> {
            VIEW_TYPE_AD
        }
        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_AD -> {
            NearStoreAdMapViewViewHolder(
                binding = ItemNearStoreAdBinding.inflate(LayoutInflater.from(parent.context), parent, false), adClickListener = adClickListener
            )
        }
        VIEW_TYPE_STORE -> {
            NearStoreMapViewViewHolder(
                binding = ItemStoreLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                certificationClick = certificationClick,
                clickListener = clickListener
            )
        }
        else -> {
            NearStoreEmptyMapViewViewHolder(ItemHomeEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is NearStoreMapViewViewHolder -> {
                holder.bind(getItem(position) as ContentModel)
            }
            is NearStoreAdMapViewViewHolder -> {
                holder.bind(getItem(position) as AdvertisementModel)
            }
            is NearStoreEmptyMapViewViewHolder -> {
                holder.bind(getItem(position) as StoreEmptyResponse)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_AD = 1
        private const val VIEW_TYPE_STORE = 2
    }
}

class NearStoreEmptyMapViewViewHolder(private val binding: ItemHomeEmptyBinding) : ViewHolder(binding.root) {
    fun bind(item: StoreEmptyResponse) {
        setEmptyUi(item)
    }

    private fun setEmptyUi(item: StoreEmptyResponse) {
        binding.run {
            emptyImageView.setImageResource(item.emptyImage)
            emptyTitleTextView.text = StringUtils.getString(item.emptyTitle)
            emptyBodyTextView.text = StringUtils.getString(item.emptyBody)
        }
    }
}

class NearStoreAdMapViewViewHolder(private val binding: ItemNearStoreAdBinding, private val adClickListener: OnItemClickListener<AdvertisementModel>) :
    ViewHolder(binding.root) {
    @SuppressLint("Range")
    fun bind(item: AdvertisementModel) {
        binding.run {
            setOnclick(item)
            setText(item)
            setImage(item)
        }
    }

    private fun ItemNearStoreAdBinding.setOnclick(item: AdvertisementModel) {
        rootConstraintLayout.setOnClickListener {
            adClickListener.onClick(item)
        }
    }

    private fun ItemNearStoreAdBinding.setImage(item: AdvertisementModel) =
        Glide.with(ivAdImage)
            .load(item.imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivAdImage)

    private fun ItemNearStoreAdBinding.setText(item: AdvertisementModel) {
        titleTextView.text = item.title
        bodyTextView.text = item.subTitle
    }
}

class NearStoreMapViewViewHolder(
    private val binding: ItemStoreLocationBinding,
    private val certificationClick: (ContentModel) -> Unit,
    private val clickListener: OnItemClickListener<ContentModel>
) : ViewHolder(binding.root) {

    fun bind(item: ContentModel) {
        binding.run {
            setOnClick(item)
            setVisitTextView(item)
            setVisible(item)
            setText(item)
            setImage(item)
        }
    }

    private fun ItemStoreLocationBinding.setVisitTextView(item: ContentModel) {
        bossOrResentVisitTextView.apply {
            if (item.storeModel.storeType == USER_STORE) {
                val visitCount = item.extraModel.visitCountsModel?.existsCounts ?: 0
                text = GlobalApplication.getContext().getString(R.string.resent_visit_count, visitCount)
                setTextAppearance(R.style.apple_gothic_medium_size_12sp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.white))
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            } else {
                text = StringUtils.getString(R.string.only_boss)
                setTextAppearance(R.style.apple_gothic_bold_size_12sp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.pink))
                val drawableStart = ContextCompat.getDrawable(GlobalApplication.getContext(), R.drawable.ic_check_pink_16)
                setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
            }
        }
    }

    private fun ItemStoreLocationBinding.setOnClick(item: ContentModel) {
        root.setOnClickListener {
            clickListener.onClick(item)
        }
        visitButton.setOnClickListener {
            this@NearStoreMapViewViewHolder.certificationClick(item)
        }
    }

    private fun setImage(item: ContentModel) = Glide.with(binding.menuIconImageView)
        .load(item.storeModel.categories.first().imageUrl)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(binding.menuIconImageView)

    private fun ItemStoreLocationBinding.setVisible(item: ContentModel) {
        visitButton.visibility = if (item.storeModel.storeType == USER_STORE) View.VISIBLE else View.INVISIBLE
        newImageView.isVisible = item.extraModel.tagsModel.isNew
    }

    private fun ItemStoreLocationBinding.setText(item: ContentModel) {
        tagTextView.text = item.storeModel.categories.joinToString(" ") { "#${it.name}" }
        distanceTextView.text = if (item.distanceM < 1000) "${item.distanceM}m" else StringUtils.getString(R.string.more_1km)
        storeNameTextView.text = item.storeModel.storeName
        reviewTextView.text = "${item.extraModel.reviewsCount}개"
    }
}