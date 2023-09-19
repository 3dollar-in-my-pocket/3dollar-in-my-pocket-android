package com.home.domain.data.store

data class AroundStoreModel(
    val contentModels: List<ContentModel> = listOf(),
    val cursorModel: CursorModel
)