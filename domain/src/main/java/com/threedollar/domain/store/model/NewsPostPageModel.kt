package com.threedollar.domain.store.model

import com.threedollar.domain.home.data.store.CursorModel
import com.threedollar.domain.home.data.store.NewsPostModel

/** 가게 소식 목록 한 페이지. [cursor]로 다음 페이지를 이어 받는다. */
data class NewsPostPageModel(
    val posts: List<NewsPostModel>,
    val cursor: CursorModel,
)
