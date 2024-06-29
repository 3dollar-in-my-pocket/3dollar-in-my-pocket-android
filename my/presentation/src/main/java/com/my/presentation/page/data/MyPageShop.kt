package com.my.presentation.page.data

import com.threedollar.network.data.favorite.MyFavoriteFolderResponse
import com.threedollar.network.data.visit_history.MyVisitHistory
import com.threedollar.network.data.visit_history.MyVisitHistoryResponseV2
import java.text.SimpleDateFormat
import java.util.Locale

data class MyPageShop(
    val title: String,
    val imageUrl: String,
    val tags: List<String>,
    val visitedData: ShopVisitedData
) {
    data class ShopVisitedData(val isExists: Boolean, val date: String)
}

val myPageShopsPreview = listOf(
    MyPageShop(
        title = "강남역 0번 출구",
        imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
        tags = listOf("#붕어빵", "#풀빵"),
        visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
    ),
    MyPageShop(
        title = "강남역 1110번 출구",
        imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
        tags = listOf("#붕어빵", "#풀빵", "#떡볶이", "#오뎅", "배고파"),
        visitedData = MyPageShop.ShopVisitedData(true, "10월 1일 19:23:00")
    )
)

val myPageShopPreview = MyPageShop(
    title = "강남역 0번 출구",
    imageUrl = "https://cdn-icons-png.flaticon.com/512/71/71403.png",
    tags = listOf("#붕어빵", "#풀빵"),
    visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
)

fun MyFavoriteFolderResponse.toMyPageShops(): List<MyPageShop> {
    return favorites.map {
        MyPageShop(
            title = it.storeName,
            imageUrl = it.categories.first().imageUrl,
            tags = it.categories.map { it.name },
            visitedData = MyPageShop.ShopVisitedData(false, "10월 1일 19:23:00")
        )
    }
}

fun MyVisitHistoryResponseV2.toMyPageShops(): List<MyPageShop> {
    return contents?.map {
        MyPageShop(
            title = it.store.storeName,
            imageUrl = it.store.categories.first().imageUrl.orEmpty(),
            tags = it.store.categories.map { it.name.orEmpty() },
            visitedData = MyPageShop.ShopVisitedData(it.visit.isExist(), formatDateString(it.visit.createdAt.orEmpty()))
        )
    }.orEmpty()
}

fun formatDateString(originalDateString: String): String {
    val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val targetFormat = SimpleDateFormat("MM월 dd일 HH:mm:ss", Locale.KOREAN)
    val date = originalFormat.parse(originalDateString)
    return date?.let { targetFormat.format(it) } ?: originalDateString
}