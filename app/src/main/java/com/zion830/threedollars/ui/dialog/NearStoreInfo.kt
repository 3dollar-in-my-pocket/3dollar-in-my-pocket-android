package com.zion830.threedollars.ui.dialog

import com.threedollar.common.utils.Constants
import com.threedollar.domain.home.data.store.ContentModel
import java.io.Serializable

data class NearStoreInfo(
    val storeName: String,
    val isBossStore: Boolean,
    val categoryName: String
) : Serializable {
    companion object {
        fun fromContentModel(contentModel: ContentModel): NearStoreInfo {
            return NearStoreInfo(
                storeName = contentModel.storeModel.storeName,
                isBossStore = contentModel.storeModel.storeType == Constants.BOSS_STORE,
                categoryName = contentModel.storeModel.categories.firstOrNull()?.name ?: ""
            )
        }

        fun fromContentModels(contentModels: List<ContentModel>): List<NearStoreInfo> {
            return contentModels.map { fromContentModel(it) }
        }
    }
}
