package com.threedollar.domain.home.data.store

data class AroundStoreModel(
    val contentModels: List<ContentModel> = listOf(),
    val cursorModel: CursorModel
)