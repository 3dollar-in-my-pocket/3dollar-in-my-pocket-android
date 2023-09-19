package com.home.domain.data.store

data class CategoryModel(
    val categoryId: String = "",
    val classificationModel: ClassificationModel = ClassificationModel(),
    val description: String = "",
    val disableImageUrl: String = "",
    val imageUrl: String = "",
    val isNew: Boolean = false,
    val name: String = ""
)