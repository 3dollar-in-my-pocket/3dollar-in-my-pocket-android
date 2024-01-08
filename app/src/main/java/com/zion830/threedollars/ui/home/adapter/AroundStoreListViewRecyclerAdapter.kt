package com.zion830.threedollars.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.store.ContentModel
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants.USER_STORE
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ItemListViewAdBinding
import com.zion830.threedollars.databinding.ItemListViewBinding
import com.zion830.threedollars.databinding.ItemListViewEmptyBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback


class AroundStoreListViewRecyclerAdapter(
    private val clickListener: OnItemClickListener<ContentModel>,
    private val clickAdListener:OnItemClickListener<AdvertisementModelV2>
) : ListAdapter<AdAndStoreItem, ViewHolder>(BaseDiffUtilCallback()) {
    private var emptyAd: AdvertisementModelV2? = null
    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ContentModel -> {
            VIEW_TYPE_STORE
        }

        is AdvertisementModelV2 -> {
            VIEW_TYPE_AD
        }

        else -> {
            VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        VIEW_TYPE_STORE -> {
            NearStoreListViewViewHolder(
                binding = ItemListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                clickListener = clickListener
            )
        }

        VIEW_TYPE_AD -> {
            NearStoreAdListViewViewHolder(ItemListViewAdBinding.inflate(LayoutInflater.from(parent.context), parent, false),clickAdListener)
        }

        else -> {
            NearStoreEmptyListViewViewHolder(ItemListViewEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false),clickAdListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is NearStoreListViewViewHolder -> {
                holder.bind(getItem(position) as ContentModel)
            }

            is NearStoreAdListViewViewHolder -> {
                holder.bind(getItem(position) as AdvertisementModelV2)
            }

            is NearStoreEmptyListViewViewHolder -> {
                holder.bind(emptyAd)
            }
        }
    }

    fun setAd(advertisementModelV2: AdvertisementModelV2?) {
        emptyAd = advertisementModelV2
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_STORE = 1
        private const val VIEW_TYPE_AD = 2
    }
}

class NearStoreEmptyListViewViewHolder(private val binding: ItemListViewEmptyBinding, private val clickAdListener: OnItemClickListener<AdvertisementModelV2>) : ViewHolder(binding.root) {
    fun bind(advertisementModelV2: AdvertisementModelV2?) {
        binding.itemListViewAd.root.isVisible = advertisementModelV2 == null
        advertisementModelV2?.let {
            binding.root.setOnClickListener { clickAdListener.onClick(advertisementModelV2) }
            binding.itemListViewAd.imgAd.loadImage(it.image.url)
            binding.itemListViewAd.tvAdTitle.text = it.title.content
            binding.itemListViewAd.tvAdBody.text = it.subTitle.content
            it.title.fontColor.let { if(it.isNotEmpty()) binding.itemListViewAd.tvAdBody.setTextColor(Color.parseColor(it)) }
            it.subTitle.fontColor.let { if(it.isNotEmpty()) binding.itemListViewAd.tvAdBody.setTextColor(Color.parseColor(it)) }
            it.background.color.let { if(it.isNotEmpty()) binding.itemListViewAd.constraintAd.setBackgroundColor(Color.parseColor(it)) }
        }
    }
}

class NearStoreAdListViewViewHolder(private val binding: ItemListViewAdBinding, private val clickAdListener: OnItemClickListener<AdvertisementModelV2>) : ViewHolder(binding.root) {
    fun bind(advertisementModelV2: AdvertisementModelV2) {
        binding.root.setOnClickListener { clickAdListener.onClick(advertisementModelV2) }
        binding.imgAd.loadImage(advertisementModelV2.image.url)
        binding.tvAdTitle.text = advertisementModelV2.title.content
        binding.tvAdBody.text = advertisementModelV2.subTitle.content
        advertisementModelV2.title.fontColor.let { if(it.isNotEmpty()) binding.tvAdBody.setTextColor(Color.parseColor(it)) }
        advertisementModelV2.subTitle.fontColor.let { if(it.isNotEmpty()) binding.tvAdBody.setTextColor(Color.parseColor(it)) }
        advertisementModelV2.background.color.let { if(it.isNotEmpty()) binding.constraintAd.setBackgroundColor(Color.parseColor(it)) }
    }
}

class NearStoreListViewViewHolder(
    private val binding: ItemListViewBinding,
    private val clickListener: OnItemClickListener<ContentModel>,
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

    private fun ItemListViewBinding.setVisitTextView(item: ContentModel) {
        bossOrResentVisitTextView.apply {
            if (item.storeModel.storeType == USER_STORE) {
                val visitCount = item.extraModel.visitCountsModel?.existsCounts ?: 0
                text = GlobalApplication.getContext().getString(R.string.resent_visit_count, visitCount)
                setTextAppearance(R.style.apple_gothic_medium_size_12sp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.gray70))
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                setBackgroundResource(R.drawable.rect_radius_18_gray_10)
            } else {
                text = StringUtils.getString(R.string.only_boss)
                setTextAppearance(R.style.apple_gothic_bold_size_12sp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), R.color.pink))
                val drawableStart = ContextCompat.getDrawable(GlobalApplication.getContext(), R.drawable.ic_check_pink_16)
                setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                setBackgroundResource(R.drawable.rect_radius_18_pink_100)
            }
        }
    }

    private fun ItemListViewBinding.setOnClick(item: ContentModel) {
        root.setOnClickListener {
            clickListener.onClick(item)
        }
    }

    private fun setImage(item: ContentModel) = binding.menuIconImageView.loadImage(item.storeModel.categories.first().imageUrl)

    private fun ItemListViewBinding.setVisible(item: ContentModel) {
        newImageView.isVisible = item.extraModel.tagsModel.isNew
        ratingTextView.isVisible = item.storeModel.storeType == USER_STORE
        ratingView.isVisible = item.storeModel.storeType == USER_STORE

    }

    private fun ItemListViewBinding.setText(item: ContentModel) {
        ratingTextView.text = (item.extraModel.rating ?: 0).toString()
        tagTextView.text = item.storeModel.categories.joinToString(" ") { "#${it.name}" }
        distanceTextView.text = if (item.distanceM < 1000) "${item.distanceM}m" else StringUtils.getString(R.string.more_1km)
        storeNameTextView.text = item.storeModel.storeName
        reviewTextView.text = "${item.extraModel.reviewsCount}개"
    }
}