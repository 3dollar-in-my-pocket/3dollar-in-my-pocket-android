package com.threedollar.domain.home.data.store


data class ExtraModel(
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val tagsModel: TagsModel = TagsModel(),
    val visitCountsModel: VisitCountsModel = VisitCountsModel()
)