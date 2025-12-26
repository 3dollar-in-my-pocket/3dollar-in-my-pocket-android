package com.threedollar.domain.home.data.store

enum class PaymentType(val title: String) {
    CASH("현금"), ACCOUNT_TRANSFER("계좌이체"), CARD ("카드")
}