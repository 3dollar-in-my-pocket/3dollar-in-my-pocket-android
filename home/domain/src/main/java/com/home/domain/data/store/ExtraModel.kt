package com.home.domain.data.store


data class ExtraModel(
    val rating: Int? = null,
    val reviewsCount: Int = 0,
    val tagsModel: TagsModel = TagsModel(),
    val visitCountsModel: VisitCountsModel? = null
)