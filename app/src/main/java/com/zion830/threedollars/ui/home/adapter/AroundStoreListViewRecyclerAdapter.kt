package com.zion830.threedollars.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.data.AdMobItem
import com.threedollar.common.ext.loadImage
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants.USER_STORE
import com.threedollar.common.utils.getDistanceText
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.ContentModel
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.databinding.ItemListViewAdBinding
import com.zion830.threedollars.databinding.ItemListViewAdmobBinding
import com.zion830.threedollars.databinding.ItemListViewBinding
import com.zion830.threedollars.databinding.ItemListViewEmptyBinding
import com.zion830.threedollars.utils.StringUtils
import zion830.com.common.base.BaseDiffUtilCallback
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR


class AroundStoreListViewRecyclerAdapter(
    private val clickListener: OnItemClickListener<ContentModel>,
    private val clickAdListener:OnItemClickListener<AdvertisementModelV2>
) : ListAdapter<AdAndStoreItem, ViewHolder>(BaseDiffUtilCallback()) {
    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ContentModel -> {
            VIEW_TYPE_STORE
        }

        is AdvertisementModelV2 -> {
            VIEW_TYPE_AD
        }

        is AdMobItem -> {
            VIEW_TYPE_ADMOB
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

        VIEW_TYPE_ADMOB -> {
            NearStoreAdMobListViewViewHolder(ItemListViewAdmobBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        else -> {
            NearStoreEmptyListViewViewHolder(ItemListViewEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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

            is NearStoreAdMobListViewViewHolder -> {
                holder.bind()
            }

            is NearStoreEmptyListViewViewHolder -> {}
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_STORE = 1
        private const val VIEW_TYPE_AD = 2
        private const val VIEW_TYPE_ADMOB = 3
    }
}

class NearStoreEmptyListViewViewHolder(binding: ItemListViewEmptyBinding) : ViewHolder(binding.root)

class NearStoreAdMobListViewViewHolder(private val binding: ItemListViewAdmobBinding) : ViewHolder(binding.root) {
    private var isAdLoaded = false

    fun bind() {
        // 광고가 이미 로드되지 않은 경우에만 로드
        if (!isAdLoaded) {
            binding.admobView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // 광고 로드 성공
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    // 광고 로드 실패 시 재시도 가능하도록 플래그를 false로 유지
                    isAdLoaded = false
                }
            }

            val adRequest = AdRequest.Builder().build()
            binding.admobView.loadAd(adRequest)
        }
    }
}

class NearStoreAdListViewViewHolder(private val binding: ItemListViewAdBinding, private val clickAdListener: OnItemClickListener<AdvertisementModelV2>) : ViewHolder(binding.root) {
    fun bind(advertisementModelV2: AdvertisementModelV2) {
        binding.root.onSingleClick { clickAdListener.onClick(advertisementModelV2) }
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
                text = GlobalApplication.getContext().getString(CommonR.string.resent_visit_count, visitCount)
                setTextAppearance(DesignSystemR.style.apple_gothic_medium_size_12dp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), DesignSystemR.color.gray70))
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                setBackgroundResource(DesignSystemR.drawable.rect_radius_18_gray_10)
            } else {
                text = StringUtils.getString(CommonR.string.only_boss)
                setTextAppearance(DesignSystemR.style.apple_gothic_bold_size_12dp)
                setTextColor(ContextCompat.getColor(GlobalApplication.getContext(), DesignSystemR.color.pink))
                val drawableStart = ContextCompat.getDrawable(GlobalApplication.getContext(), DesignSystemR.drawable.ic_check_pink_16)
                setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                setBackgroundResource(DesignSystemR.drawable.rect_radius_18_pink_100)
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
        distanceTextView.text = root.context.getDistanceText(item.distanceM)
        storeNameTextView.text = item.storeModel.storeName
        reviewTextView.text = "${item.extraModel.reviewsCount}개"
    }
}