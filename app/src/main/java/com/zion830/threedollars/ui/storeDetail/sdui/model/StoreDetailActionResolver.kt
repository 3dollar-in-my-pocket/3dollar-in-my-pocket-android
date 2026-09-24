package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.ADDRESS
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.COUPON_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.COUPON_ISSUED_KEY
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.IMAGE_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.IMAGE_URL
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.LATITUDE
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.LONGITUDE
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.POST_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.REVIEW_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.STICKER_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.STORE_ID
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.STORE_NAME
import com.threedollar.common.sdui.model.element.SDCustomActionModel.Keys.URL
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel

/**
 * 탭 이벤트를 실행할 일로 번역한다. 네트워크·화면 전환은 하지 않고 무엇을 할지만 정한다.
 */
object StoreDetailActionResolver {

    /** 상세 화면이 알고 있는 현재 가게 정보. */
    data class Context(
        val storeId: String,
        val isBossStore: Boolean,
        val storeName: String,
        val sections: List<SDSectionModel>,
    )

    sealed interface Resolution {
        data class Navigate(val destination: StoreDetailDestination) : Resolution
        data class ScrollTo(val index: Int) : Resolution
        data class LikeReview(val storeId: String, val reviewId: String, val stickerId: String?) : Resolution
        data class LikePost(val storeId: String, val postId: String, val stickerId: String?) : Resolution
        data class IssueCoupon(val storeId: String, val couponId: String) : Resolution
        data class ReportReview(val storeId: String, val reviewId: Long) : Resolution
        data object Ignore : Resolution
    }

    const val DEFAULT_STICKER_ID = "LIKE"
    private const val REVIEW_LIST_PATH = "reviewList"
    private const val VISIT_PATH = "visit"
    private const val STORE_ID_QUERY = "storeId"

    fun resolve(event: SDActionEvent, context: Context): Resolution {
        event.customAction?.let { action ->
            val resolution = resolveCustomAction(action, context)
            if (resolution != Resolution.Ignore) return resolution
        }
        return event.link?.let { resolveLink(it, context) } ?: Resolution.Ignore
    }

    private fun resolveCustomAction(action: SDCustomActionModel, context: Context): Resolution {
        val storeId = action.param(STORE_ID) ?: context.storeId
        return when (action.actionType) {
            SDCustomActionType.STORE_PREVIEW_SECTION_SHARE ->
                action.param(URL)?.let { navigate(StoreDetailDestination.Share(it)) }

            SDCustomActionType.STORE_PREVIEW_SECTION_NAVIGATION -> {
                val latitude = action.doubleParam(LATITUDE)
                val longitude = action.doubleParam(LONGITUDE)
                if (latitude != null && longitude != null) {
                    navigate(StoreDetailDestination.Directions(latitude, longitude, action.param(STORE_NAME) ?: context.storeName))
                } else {
                    null
                }
            }

            SDCustomActionType.STORE_PREVIEW_SECTION_REVIEW_WRITE,
            SDCustomActionType.STORE_REVIEW_SECTION_REVIEW_WRITE ->
                navigate(StoreDetailDestination.WriteReview(storeId, context.isBossStore))

            SDCustomActionType.STORE_EDIT_SECTION_UPDATE -> navigate(StoreDetailDestination.EditStore(storeId))
            SDCustomActionType.STORE_EDIT_SECTION_REPORT -> navigate(StoreDetailDestination.ReportStore(storeId))
            SDCustomActionType.STORE_EDIT_SECTION_COPY_ADDRESS ->
                action.param(ADDRESS)?.let { navigate(StoreDetailDestination.CopyAddress(it)) }

            SDCustomActionType.STORE_EDIT_SECTION_MAP_ENLARGE -> context.mapLocation()?.let { (latitude, longitude) ->
                navigate(StoreDetailDestination.MapEnlarge(latitude, longitude, context.storeName))
            }

            SDCustomActionType.STORE_INFO_V2_SECTION_COPY_ACCOUNT_HOLDER ->
                action.accountText()?.let { navigate(StoreDetailDestination.CopyText(it)) }

            SDCustomActionType.STORE_COUPON_SECTION_COUPON_ISSUE ->
                action.param(COUPON_ID)?.let { Resolution.IssueCoupon(storeId, it) }

            SDCustomActionType.STORE_COUPON_SECTION_COUPON_USE ->
                action.param(COUPON_ISSUED_KEY)?.let { navigate(StoreDetailDestination.ConfirmUseCoupon(it)) }

            SDCustomActionType.STORE_POST_SECTION_ADD_LIKE ->
                action.param(POST_ID)?.let { Resolution.LikePost(storeId, it, action.param(STICKER_ID) ?: DEFAULT_STICKER_ID) }

            SDCustomActionType.STORE_POST_SECTION_CANCEL_LIKE ->
                action.param(POST_ID)?.let { Resolution.LikePost(storeId, it, stickerId = null) }

            SDCustomActionType.STORE_IMAGE_SECTION_ADD_IMAGE -> navigate(StoreDetailDestination.AddImage(storeId))
            SDCustomActionType.STORE_IMAGE_SECTION_IMAGE_ENLARGE -> context.imageViewer(action)?.let(::navigate)

            SDCustomActionType.STORE_REVIEW_SECTION_REPORT ->
                action.param(REVIEW_ID)?.toLongOrNull()?.let { Resolution.ReportReview(storeId, it) }

            SDCustomActionType.STORE_REVIEW_SECTION_DELETE ->
                action.param(REVIEW_ID)?.let { navigate(StoreDetailDestination.ConfirmDeleteReview(it)) }

            SDCustomActionType.STORE_REVIEW_SECTION_ADD_LIKE ->
                action.param(REVIEW_ID)?.let {
                    Resolution.LikeReview(storeId, it, action.param(STICKER_ID) ?: DEFAULT_STICKER_ID)
                }

            SDCustomActionType.STORE_REVIEW_SECTION_CANCEL_LIKE ->
                action.param(REVIEW_ID)?.let { Resolution.LikeReview(storeId, it, stickerId = null) }

            null -> action.param(POST_ID)?.let {
                Resolution.LikePost(storeId, it, action.param(STICKER_ID) ?: DEFAULT_STICKER_ID)
            }
        } ?: Resolution.Ignore
    }

    private fun resolveLink(link: SDLink, context: Context): Resolution {
        val raw = link.link?.takeIf { it.isNotBlank() } ?: return Resolution.Ignore
        if (link.type != SDLinkType.APP_SCHEME) return navigate(StoreDetailDestination.OpenLink(link))

        StoreSectionFragment.parseLink(raw)?.let { sectionLink ->
            if (sectionLink.storeId == context.storeId) {
                return StoreSectionFragment.resolveIndex(context.sections, sectionLink.fragment)
                    ?.let { Resolution.ScrollTo(it) }
                    ?: Resolution.Ignore
            }
        }

        val isCurrentStore = queryParameter(raw, STORE_ID_QUERY) == context.storeId
        when (lastPathSegment(raw)) {
            REVIEW_LIST_PATH -> if (isCurrentStore) {
                return navigate(StoreDetailDestination.ReviewList(context.storeId, context.isBossStore))
            }

            VISIT_PATH -> if (isCurrentStore) {
                context.mapLocation()?.let { (latitude, longitude) ->
                    return navigate(StoreDetailDestination.Visit(context.storeId, context.storeName, latitude, longitude))
                }
            }
        }
        return navigate(StoreDetailDestination.OpenLink(link))
    }

    private fun navigate(destination: StoreDetailDestination) = Resolution.Navigate(destination)

    private fun lastPathSegment(link: String): String? {
        val path = link.substringBefore('#').substringBefore('?')
        return path.substringAfter("://", missingDelimiterValue = path)
            .split('/')
            .lastOrNull { it.isNotBlank() }
    }

    private fun queryParameter(link: String, key: String): String? =
        link.substringBefore('#')
            .substringAfter('?', missingDelimiterValue = "")
            .split('&')
            .firstOrNull { it.substringBefore('=') == key }
            ?.substringAfter('=', missingDelimiterValue = "")

    private fun Context.mapLocation(): Pair<Double, Double>? {
        val location = sections.filterIsInstance<SDStoreEditSectionModel>().firstOrNull()?.map?.location ?: return null
        val latitude = location.latitude ?: return null
        val longitude = location.longitude ?: return null
        return latitude to longitude
    }

    private fun Context.imageViewer(action: SDCustomActionModel): StoreDetailDestination.ShowImages? {
        val cards = sections.filterIsInstance<SDStoreImageSectionModel>()
            .flatMap { it.cards.orEmpty() }
            .filter { it.image?.url != null }
        val imageUrls = cards.mapNotNull { it.image?.url }
        val targetId = action.param(IMAGE_ID)
        val targetUrl = action.param(IMAGE_URL)
        if (imageUrls.isEmpty()) {
            return targetUrl?.let { StoreDetailDestination.ShowImages(listOf(it), 0) }
        }
        val index = cards.indexOfFirst { card ->
            val cardAction = card.customAction
            (targetId != null && cardAction?.param(IMAGE_ID) == targetId) ||
                (targetUrl != null && card.image?.url == targetUrl)
        }.coerceAtLeast(0)
        return StoreDetailDestination.ShowImages(imageUrls, index.coerceAtMost(imageUrls.lastIndex))
    }

    private fun SDCustomActionModel.accountText(): String? =
        param(SDCustomActionModel.ACCOUNT_NUMBER)?.takeIf { it.isNotBlank() }
}
