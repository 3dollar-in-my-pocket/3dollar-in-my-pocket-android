package com.threedollar.domain.home.data.store

data class AccountNumberModel(
    val bank: BankModel = BankModel(),
    val accountHolder: String = "",
    val accountNumber: String = "",
    val description: String? = "",
)
