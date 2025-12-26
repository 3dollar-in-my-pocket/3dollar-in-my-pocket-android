package com.threedollar.domain.home.data.store



data class ImagesModel(
    val contents: List<ImageContentModel> = listOf(),
    val cursor: CursorModel = CursorModel()
)