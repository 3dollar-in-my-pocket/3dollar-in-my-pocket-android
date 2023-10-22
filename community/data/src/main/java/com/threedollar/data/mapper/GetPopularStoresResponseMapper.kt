package com.threedollar.data.mapper

import com.threedollar.common.utils.toDefaultInt
import com.threedollar.domain.data.Cursor
import com.threedollar.domain.data.PopularStore
import com.threedollar.network.data.neighborhood.GetPopularStoresResponse

object GetPopularStoresResponseMapper {
    fun GetPopularStoresResponse.Content.toMapper(): PopularStore {
        return PopularStore(
            account = this.account.toMapper(),
            address = this.address.toMapper(),
            categories = this.categories.orEmpty().map { it.toMapper() },
            createdAt = this.createdAt.orEmpty(),
            isDeleted = this.isDeleted ?: false,
            isOwner = this.isOwner ?: false,
            location = this.location.toMapper(),
            storeId = this.storeId.orEmpty(),
            storeName = this.storeName.orEmpty(),
            storeType = this.storeType.orEmpty(),
            updatedAt = this.updatedAt.orEmpty()
        )
    }

    fun GetPopularStoresResponse.Content.Category?.toMapper(): PopularStore.Category {
        return PopularStore.Category(
            categoryId = this?.categoryId.orEmpty(),
            classification = this?.classification.toMapper(),
            description = this?.description.orEmpty(),
            imageUrl = this?.imageUrl.orEmpty(),
            isNew = this?.isNew ?: false,
            name = this?.name.orEmpty()
        )
    }

    fun GetPopularStoresResponse.Content.Category.Classification?.toMapper(): PopularStore.Category.Classification {
        return PopularStore.Category.Classification(this?.description.orEmpty(), this?.type.orEmpty())
    }

    fun GetPopularStoresResponse.Content.Account?.toMapper(): PopularStore.Account {
        return PopularStore.Account(this?.accountId.orEmpty(), this?.accountType.orEmpty())
    }

    fun GetPopularStoresResponse.Content.Address?.toMapper(): PopularStore.Address {
        return PopularStore.Address(this?.fullAddress.orEmpty())
    }

    fun GetPopularStoresResponse.Content.Location?.toMapper(): PopularStore.Location {
        return PopularStore.Location(this?.latitude.toDefaultInt(), this?.longitude.toDefaultInt())
    }

    fun GetPopularStoresResponse.Cursor?.toMapper(): Cursor {
        return Cursor(this?.nextCursor.orEmpty(), this?.hasMore ?: false)
    }
}