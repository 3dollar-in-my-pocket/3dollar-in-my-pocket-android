package com.my.presentation.page.data

import com.my.domain.model.FavoriteStoresModel
import com.my.domain.model.VisitHistoryModel
import java.text.SimpleDateFormat
import java.util.Locale

data class MyPageShop(
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
    val storeType: String,
    val storeId: String,
    val visitedData: ShopVisitedData
) {
    data class ShopVisitedData(val isExists: Boolean, val date: String)
}

val myPageShopsPreview = listOf(
    MyPageShop(
        title = "강남역 0번 출구",
        imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
        tags = listOf("#붕어빵", "#풀빵"),
        storeType = "",
        storeId = "",
        visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
    ),
    MyPageShop(
        title = "강남역 1110번 출구",
        imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
        tags = listOf("#붕어빵", "#풀빵", "#떡볶이", "#오뎅", "배고파"),
        storeType = "",
        storeId = "",
        visitedData = MyPageShop.ShopVisitedData(true, "10월 1일 19:23:00")
    )
)

val myPageShopPreview = MyPageShop(
    title = "강남역 0번 출구",
    imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
    tags = listOf("#붕어빵", "#풀빵"),
    storeType = "",
    storeId = "",
    visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
)

fun FavoriteStoresModel.toMyPageShops(): List<MyPageShop> {
    return contents.map {
        MyPageShop(
            title = it.storeName,
            imageUrl = it.categories.firstOrNull()?.imageUrl ?: "",
            tags = it.categories.map { it.name },
            storeType = it.storeType,
            storeId = it.storeId,
            visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
        )
    }
}

fun VisitHistoryModel.toMyPageShops(): List<MyPageShop> {
    return contents.map {
        MyPageShop(
            title = it.store.storeName,
            imageUrl = it.store.categories.firstOrNull()?.imageUrl ?: "",
            tags = it.store.categories.map { it.name },
            storeType = it.store.storeType,
            storeId = it.store.storeId,
            visitedData = MyPageShop.ShopVisitedData(true, formatDateString(it.dateOfVisit))
        )
    }
}

fun formatDateString(originalDateString: String): String {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val targetFormat = SimpleDateFormat("MM월 dd일 HH:mm:ss", Locale.KOREAN)
    val date = originalFormat.parse(originalDateString)
    return date?.let { targetFormat.format(it) } ?: originalDateString
}