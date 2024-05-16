package com.my.presentation.page.data

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
