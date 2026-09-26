package com.zion830.threedollars.ui.storeDetail.sdui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.domain.home.data.store.ReasonModel
import com.threedollar.domain.home.request.ReportReviewModelRequest
import com.zion830.threedollars.ui.storeDetail.user.model.StoreDetailDisplayItem
import okhttp3.MultipartBody

@Immutable
data class StoreDetailSduiUiState(
    val storeId: String = "",
    val isLoading: Boolean = true,
    val sections: List<SDSectionModel> = emptyList(),
    val isFavorite: Boolean = false,
    val storeType: String? = null,
    val storeName: String = "",
    val isUploading: Boolean = false,
) {
    val hasContent: Boolean get() = sections.isNotEmpty()
}

@Immutable
sealed interface StoreDetailSduiUiIntent {
    /**
     * 새 가게를 조회한다. 같은 가게면 무시한다. [fragment]가 있으면 로드 후 그 섹션으로 스크롤한다.
     * [withPreview] 면 홈 미리보기 시트용 PREVIEW(`/preview`)도 함께 받아 [StoreDetailSduiViewModel.preview] 에 둔다.
     */
    data class Load(
        val storeId: String,
        val latitude: Double?,
        val longitude: Double?,
        val fragment: String? = null,
        val withPreview: Boolean = false,
    ) : StoreDetailSduiUiIntent

    /** 스크롤 위치를 유지한 채 다시 조회한다. */
    data object Refresh : StoreDetailSduiUiIntent

    data class OnAction(val event: SDActionEvent) : StoreDetailSduiUiIntent

    data object OnFavoriteClick : StoreDetailSduiUiIntent

    /** 상세가 실제로 화면에 보였다. 페이지뷰는 이때 한 번 보낸다. */
    data object OnDisplayed : StoreDetailSduiUiIntent

    data class OnImpression(val key: String, val log: SDLogModel?) : StoreDetailSduiUiIntent

    data class ScrollToFragment(val fragment: String) : StoreDetailSduiUiIntent

    data class OnReviewDeleteConfirmed(val reviewId: String) : StoreDetailSduiUiIntent

    data class OnCouponUseConfirmed(val issuedKey: String) : StoreDetailSduiUiIntent

    data class OnStoreReportSubmit(val deleteReasonType: String) : StoreDetailSduiUiIntent

    data class OnReviewReportSubmit(val reviewId: Long, val request: ReportReviewModelRequest) : StoreDetailSduiUiIntent

    data class OnReviewSubmit(val contents: String, val rating: Int) : StoreDetailSduiUiIntent

    data class OnImagesSelected(val images: List<MultipartBody.Part>) : StoreDetailSduiUiIntent

    /** 정보 수정·보스 리뷰 작성처럼 다른 화면에서 가게 정보를 바꾸고 돌아왔다. */
    data object OnStoreChanged : StoreDetailSduiUiIntent

    data class OnDisplayItemDisplayed(val item: StoreDetailDisplayItem) : StoreDetailSduiUiIntent

    data class OnVisitInducementClick(val isOpened: Boolean) : StoreDetailSduiUiIntent

    data class OnDisappearanceReasonClick(val reason: ReasonModel) : StoreDetailSduiUiIntent

    data object OnDisappearanceReportClick : StoreDetailSduiUiIntent
}

@Immutable
sealed interface StoreDetailSduiUiEffect {
    data class ShowToast(@StringRes val messageRes: Int? = null, val message: String? = null) : StoreDetailSduiUiEffect

    /** 공통 에러 알럿. 별도 재시도 UI 는 두지 않는다. */
    data class ShowErrorAlert(val message: String?) : StoreDetailSduiUiEffect

    /** 상세를 닫는다. 메시지가 있으면 [asAlert] 에 따라 알럿 또는 토스트로 먼저 알린다. */
    data class Close(val message: String? = null, @StringRes val messageRes: Int? = null, val asAlert: Boolean = false) :
        StoreDetailSduiUiEffect

    data class ScrollToSection(val index: Int) : StoreDetailSduiUiEffect

    data class Navigate(val destination: StoreDetailDestination) : StoreDetailSduiUiEffect
}

/**
 * 서버 액션이 요청한 화면 밖 동작. 실행은 화면(호스트)이 맡는다.
 */
@Immutable
sealed interface StoreDetailDestination {
    data class OpenLink(val link: SDLink) : StoreDetailDestination
    data class Share(val url: String) : StoreDetailDestination
    data class Directions(val latitude: Double, val longitude: Double, val storeName: String) : StoreDetailDestination
    data class CopyAddress(val address: String) : StoreDetailDestination
    data class CopyText(val text: String) : StoreDetailDestination
    data class MapEnlarge(val latitude: Double, val longitude: Double, val storeName: String) : StoreDetailDestination
    /** 가게 사진 전체 목록 뷰어. 본인이 올린 사진은 삭제할 수 있다. */
    data class StorePhotos(val storeId: String, val startIndex: Int) : StoreDetailDestination

    /** 제보·사장님 가게 모두 같은 별점+글 작성 시트를 쓴다 (iOS `ReviewBottomSheet` 와 동일). */
    data class WriteReview(val storeId: String) : StoreDetailDestination
    data class AddImage(val storeId: String) : StoreDetailDestination
    data class ShowImages(val imageUrls: List<String>, val startIndex: Int) : StoreDetailDestination
    data class EditStore(val storeId: String) : StoreDetailDestination
    data class ReportStore(val storeId: String) : StoreDetailDestination
    data class ReportReview(val storeId: String, val reviewId: Long, val reasons: List<ReasonModel>) : StoreDetailDestination
    data class ConfirmDeleteReview(val reviewId: String) : StoreDetailDestination
    data class ConfirmUseCoupon(val issuedKey: String) : StoreDetailDestination
    /** 방문 인증 화면이 가게 정보를 직접 조회한다 (iOS `VisitViewModel` 과 동일). */
    data class Visit(val storeId: String) : StoreDetailDestination
    data class ReviewList(val storeId: String, val isBossStore: Boolean) : StoreDetailDestination
}
