package com.zion830.threedollars.ui.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.data.AdMobItem
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2Empty
import com.threedollar.domain.home.data.store.ContentModel
import com.zion830.threedollars.datasource.model.v2.response.StoreEmptyResponse

class AdAndStoreItemDiffCallback : DiffUtil.ItemCallback<AdAndStoreItem>() {

    override fun areItemsTheSame(oldItem: AdAndStoreItem, newItem: AdAndStoreItem): Boolean {
        return when {
            oldItem is ContentModel && newItem is ContentModel ->
                oldItem.storeModel.storeId == newItem.storeModel.storeId

            oldItem is AdvertisementModelV2 && newItem is AdvertisementModelV2 ->
                oldItem.advertisementId == newItem.advertisementId

            oldItem is AdvertisementModelV2Empty && newItem is AdvertisementModelV2Empty -> true

            oldItem is AdMobItem && newItem is AdMobItem -> true

            oldItem is StoreEmptyResponse && newItem is StoreEmptyResponse -> true

            else -> oldItem == newItem
        }
    }

    override fun areContentsTheSame(oldItem: AdAndStoreItem, newItem: AdAndStoreItem): Boolean {
        return oldItem == newItem
    }
}
