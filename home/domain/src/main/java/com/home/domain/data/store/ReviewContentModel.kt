package com.home.domain.data.store


data class ReviewContentModel(
    val review: ReviewModel = ReviewModel(),
    val reviewReport: ReviewReportModel = ReviewReportModel(),
    val reviewWriter: ReviewWriterModel = ReviewWriterModel(),
) : UserStoreDetailItem