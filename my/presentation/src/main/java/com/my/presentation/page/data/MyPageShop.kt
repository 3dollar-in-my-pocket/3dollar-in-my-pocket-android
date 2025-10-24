package com.my.presentation.page.data

import com.threedollar.domain.my.model.FavoriteStoresModel
import com.threedollar.domain.my.model.VisitHistoryModel
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
            tags = it.categories.map { "#${it.name}" },
            storeType = it.storeType,
            storeId = it.storeId,
            visitedData = MyPageShop.ShopVisitedData(false, "")
        )
    }
}

fun VisitHistoryModel.toMyPageShops(): List<MyPageShop> {
    return contents.map {
        MyPageShop(
            title = it.store.storeName,
            imageUrl = it.store.categories.firstOrNull()?.imageUrl ?: "",
            tags = it.store.categories.map { "#${it.name}" },
            storeType = it.store.storeType,
            storeId = it.store.storeId,
            visitedData = MyPageShop.ShopVisitedData(true, it.dateOfVisit)
        )
    }
}

fun formatDateString(originalDateString: String): String {
    // 여러 날짜 포맷 지원
    val possibleFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    val targetFormat = SimpleDateFormat("MM월 dd일 HH:mm:ss", Locale.KOREAN)

    // 각 포맷으로 파싱 시도
    for (format in possibleFormats) {
        try {
            val date = format.parse(originalDateString)
            date?.let {
                return targetFormat.format(it)
            }
        } catch (e: Exception) {
            // 다음 포맷으로 시도
            continue
        }
    }

    // 모든 포맷 실패 시 원본 문자열 반환
    return originalDateString
}