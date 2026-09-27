package com.zion830.threedollars.ui.storeDetail.post.model

import androidx.compose.runtime.Immutable
import com.threedollar.domain.home.data.store.NewsPostModel
import com.threedollar.domain.home.data.store.SectionTypeModel
import com.threedollar.domain.home.data.store.StickerModel
import com.threedollar.domain.store.model.NewsPostPageModel

/**
 * 사장님 가게 소식 목록 상태. 페이지·좋아요 반영은 순수 함수로 두어 유닛 테스트한다.
 */
@Immutable
data class StorePostListUiState(
    val posts: List<NewsPostModel> = emptyList(),
    val nextCursor: String? = null,
    val hasMore: Boolean = true,
    val isLoading: Boolean = false,
) {
    /** iOS 와 같이 첫 소식의 가게명으로 타이틀을 만든다. 소식이 없으면 비어 있다. */
    val storeName: String
        get() = posts.firstOrNull()?.storeName.orEmpty()

    val canLoadMore: Boolean
        get() = hasMore && !isLoading

    fun appendPage(page: NewsPostPageModel): StorePostListUiState = copy(
        posts = posts + page.posts,
        nextCursor = page.cursor.nextCursor,
        hasMore = page.cursor.hasMore && page.cursor.nextCursor != null,
    )

    fun replacePost(post: NewsPostModel): StorePostListUiState = copy(
        posts = posts.map { if (it.postId == post.postId) post else it },
    )
}

@Immutable
sealed interface StorePostListUiIntent {
    data object OnInit : StorePostListUiIntent
    data object OnLoadNextPage : StorePostListUiIntent
    data object OnBackClick : StorePostListUiIntent
    data class OnLikeClick(val postId: String) : StorePostListUiIntent
    data class OnImageClick(val postId: String, val imageIndex: Int) : StorePostListUiIntent
}

@Immutable
sealed interface StorePostListUiEffect {
    data object Close : StorePostListUiEffect
    data class ShowImages(val imageUrls: List<String>, val startIndex: Int) : StorePostListUiEffect
    data class ShowErrorAlert(val message: String?) : StorePostListUiEffect
}

/** 좋아요 버튼이 대표하는 스티커. 서버가 스티커를 내려주지 않아도 기본 좋아요로 그린다. */
val NewsPostModel.likeSticker: StickerModel
    get() = stickers.firstOrNull() ?: StickerModel(stickerId = DEFAULT_STICKER_ID, emoji = "", count = 0, reactedByMe = false)

/** 이미지 섹션만 뷰어·가로 스크롤에 쓴다. 알 수 없는 섹션 타입은 건너뛴다. */
val NewsPostModel.imageSections
    get() = sections.filter { it.sectionType == SectionTypeModel.IMAGE }

/**
 * 좋아요 API 에 보낼 스티커 id. 이미 누른 상태면 null 을 보내 취소한다
 * ([com.threedollar.domain.store.repository.StoreRepository.putStorePostSticker] 규약).
 */
fun NewsPostModel.likeRequestStickerId(): String? = likeSticker.takeUnless { it.reactedByMe }?.stickerId

/** 좋아요 API 성공 후 반영할 소식. 첫 스티커의 눌림 상태를 뒤집고 개수를 ±1 한다. */
fun NewsPostModel.toggledLike(): NewsPostModel {
    val sticker = likeSticker
    val reacted = !sticker.reactedByMe
    val toggled = sticker.copy(
        reactedByMe = reacted,
        count = (sticker.count + if (reacted) 1 else -1).coerceAtLeast(0),
    )
    return copy(stickers = listOf(toggled) + stickers.drop(1))
}

private const val DEFAULT_STICKER_ID = "LIKE"
const val STORE_POST_PAGE_SIZE = 20
