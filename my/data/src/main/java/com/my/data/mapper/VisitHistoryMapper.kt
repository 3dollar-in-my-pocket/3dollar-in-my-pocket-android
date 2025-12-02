package com.my.data.mapper

import com.my.domain.model.VisitHistoryModel
import com.my.domain.model.VisitHistoryItemModel
import com.my.domain.model.StoreModel
import com.my.domain.model.CategoryModel
import com.my.domain.model.AddressModel
import com.my.domain.model.CursorModel
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2

object VisitHistoryMapper {

    fun toDomainModel(response: MyVisitHistoryResponseV2): VisitHistoryModel {
        return VisitHistoryModel(
            contents = response.contents?.map { visitHistoryV2 ->
                VisitHistoryItemModel(
                    visitHistoryId = visitHistoryV2.visit.visitId ?: "",
                    store = StoreModel(
                        storeId = visitHistoryV2.store.storeId ?: "",
                        storeName = visitHistoryV2.store.storeName ?: "",
                        storeType = visitHistoryV2.store.storeType ?: "",
                        rating = 0.0, // Not available in Store model
                        reviewsCount = 0, // Not available in Store model
                        categories = visitHistoryV2.store.categories?.map { category ->
                            CategoryModel(
                                categoryId = category.categoryId ?: "",
                                name = category.name ?: "",
                                imageUrl = category.imageUrl ?: ""
                            )
                        } ?: emptyList(),
                        address = AddressModel(
                            fullAddress = visitHistoryV2.store.address?.fullAddress ?: "",
                            district = "" // District not available in Address model
                        ),
                        distance = 0, // Not available in Store model
                        isDeleted = visitHistoryV2.store.isDeleted ?: false
                    ),
                    dateOfVisit = visitHistoryV2.visit.visitDate ?: "",
                    type = visitHistoryV2.visit.type ?: ""
                )
            } ?: emptyList(),
            cursor = response.cursor?.let { cursor ->
                CursorModel(
                    hasMore = cursor.hasMore ?: false,
                    nextCursor = cursor.nextCursor
                )
            }
        )
    }
}