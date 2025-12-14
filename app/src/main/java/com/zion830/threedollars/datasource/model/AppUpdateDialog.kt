package com.zion830.threedollars.datasource.model

data class AppUpdateDialog(
    val enabled: Boolean,
    val title: String?,
    val message: String?,
    val linkUrl: String?,
    val currentVersion: String
)