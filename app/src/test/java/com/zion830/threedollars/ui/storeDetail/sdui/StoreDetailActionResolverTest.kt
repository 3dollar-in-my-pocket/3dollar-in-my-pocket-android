package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.INFO_INDEX
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.STORE_ID
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.appLink
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.customAction
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.sections
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver.Resolution
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailActionResolverTest {

    private val context = StoreDetailActionResolver.Context(
        storeId = STORE_ID,
        isBossStore = false,
        storeName = "호떡집",
        sections = sections,
    )

    private fun resolve(event: SDActionEvent, context: StoreDetailActionResolver.Context = this.context) =
        StoreDetailActionResolver.resolve(event, context)

    private fun customEvent(type: SDCustomActionType?, vararg params: Pair<String, Any?>) =
        SDActionEvent(customAction = customAction(type, *params))

    // TH-1226 TC12
    @Test
    fun `TH1226_TC12_공유는_서버가_내려준_URL로_공유시트를_연다`() {
        // Given
        val event = customEvent(
            SDCustomActionType.STORE_PREVIEW_SECTION_SHARE,
            SDCustomActionModel.STORE_ID to STORE_ID,
            SDCustomActionModel.URL to "https://link.threedollars.co.kr/stores/118",
        )

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.Share("https://link.threedollars.co.kr/stores/118")), resolution)
    }

    // TH-1226 TC9
    @Test
    fun `TH1226_TC9_같은가게의_탭링크는_해당섹션으로_스크롤한다`() {
        // Given
        val event = SDActionEvent(link = appLink("/stores/$STORE_ID#INFO"))

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.ScrollTo(INFO_INDEX), resolution)
    }

    // TH-1226 TC19
    @Test
    fun `TH1226_TC19_같은가게의_reviewList링크는_상세를_새로띄우지않고_리뷰목록으로_간다`() {
        // Given
        val event = SDActionEvent(link = appLink("/reviewList?storeType=USER_STORE&storeId=$STORE_ID"))
        val otherStore = SDActionEvent(link = appLink("/reviewList?storeType=USER_STORE&storeId=999"))

        // When
        val sameStore = resolve(event)
        val fromOtherStore = resolve(otherStore)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.ReviewList(STORE_ID, isBossStore = false)), sameStore)
        assertEquals(Resolution.Navigate(StoreDetailDestination.OpenLink(otherStore.link!!)), fromOtherStore)
    }

    // TH-1226 TC13
    @Test
    fun `TH1226_TC13_리뷰작성은_가게타입에맞는_작성화면으로_간다`() {
        // Given
        val event = customEvent(SDCustomActionType.STORE_REVIEW_SECTION_REVIEW_WRITE, SDCustomActionModel.STORE_ID to STORE_ID)

        // When
        val userStore = resolve(event)
        val bossStore = resolve(event, context.copy(isBossStore = true))

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.WriteReview(STORE_ID, isBossStore = false)), userStore)
        assertEquals(Resolution.Navigate(StoreDetailDestination.WriteReview(STORE_ID, isBossStore = true)), bossStore)
    }

    // TH-1226 TC14
    @Test
    fun `TH1226_TC14_가게신고는_신고다이얼로그를_연다`() {
        // Given
        val event = customEvent(SDCustomActionType.STORE_EDIT_SECTION_REPORT, SDCustomActionModel.STORE_ID to STORE_ID)

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.ReportStore(STORE_ID)), resolution)
    }

    @Test
    fun `길안내는_숫자로_온_좌표와_가게명으로_지도앱선택을_연다`() {
        // Given
        val event = customEvent(
            SDCustomActionType.STORE_PREVIEW_SECTION_NAVIGATION,
            SDCustomActionModel.LATITUDE to 37.4979,
            SDCustomActionModel.LONGITUDE to 127.0276,
            SDCustomActionModel.STORE_NAME to "호떡집",
        )

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.Directions(37.4979, 127.0276, "호떡집")), resolution)
    }

    @Test
    fun `지도크게보기는_EDIT섹션의_가게위치를_쓴다`() {
        // Given
        val event = customEvent(SDCustomActionType.STORE_EDIT_SECTION_MAP_ENLARGE)

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.MapEnlarge(37.4979, 127.0276, "호떡집")), resolution)
    }

    @Test
    fun `사진확대는_가게사진_전체목록에서_탭한사진_위치로_연다`() {
        // Given
        val event = customEvent(
            SDCustomActionType.STORE_IMAGE_SECTION_IMAGE_ENLARGE,
            SDCustomActionModel.IMAGE_ID to "1",
            SDCustomActionModel.IMAGE_URL to "https://image/b.png",
        )

        // When
        val resolution = resolve(event)

        // Then
        val expected = StoreDetailDestination.ShowImages(
            imageUrls = listOf("https://image/a.png", "https://image/b.png", "https://image/c.png"),
            startIndex = 1,
        )
        assertEquals(Resolution.Navigate(expected), resolution)
    }

    @Test
    fun `리뷰와_소식_좋아요는_스티커를_등록하고_취소는_스티커를_비운다`() {
        // Given
        val addReviewLike = customEvent(SDCustomActionType.STORE_REVIEW_SECTION_ADD_LIKE, SDCustomActionModel.REVIEW_ID to "10", SDCustomActionModel.STICKER_ID to "LIKE")
        val cancelPostLike = customEvent(SDCustomActionType.STORE_POST_SECTION_CANCEL_LIKE, SDCustomActionModel.POST_ID to "79")
        val unknownPostLike = customEvent(null, SDCustomActionModel.POST_ID to "79")

        // When
        val addReview = resolve(addReviewLike)
        val cancelPost = resolve(cancelPostLike)
        val unknownPost = resolve(unknownPostLike)

        // Then
        assertEquals(Resolution.LikeReview(STORE_ID, "10", "LIKE"), addReview)
        assertEquals(Resolution.LikePost(STORE_ID, "79", null), cancelPost)
        assertEquals(Resolution.LikePost(STORE_ID, "79", StoreDetailActionResolver.DEFAULT_STICKER_ID), unknownPost)
    }

    @Test
    fun `쿠폰발급은_바로_발급하고_쿠폰사용은_확인알럿을_먼저_띄운다`() {
        // Given
        val issue = customEvent(SDCustomActionType.STORE_COUPON_SECTION_COUPON_ISSUE, SDCustomActionModel.COUPON_ID to "881960829125365760")
        val use = customEvent(SDCustomActionType.STORE_COUPON_SECTION_COUPON_USE, SDCustomActionModel.COUPON_ISSUED_KEY to "issued-key")

        // When
        val issueResolution = resolve(issue)
        val useResolution = resolve(use)

        // Then
        assertEquals(Resolution.IssueCoupon(STORE_ID, "881960829125365760"), issueResolution)
        assertEquals(Resolution.Navigate(StoreDetailDestination.ConfirmUseCoupon("issued-key")), useResolution)
    }

    @Test
    fun `리뷰삭제는_확인알럿을_띄우고_리뷰신고는_신고사유를_먼저_조회한다`() {
        // Given
        val delete = customEvent(SDCustomActionType.STORE_REVIEW_SECTION_DELETE, SDCustomActionModel.REVIEW_ID to "10")
        val report = customEvent(SDCustomActionType.STORE_REVIEW_SECTION_REPORT, SDCustomActionModel.REVIEW_ID to "10")

        // When
        val deleteResolution = resolve(delete)
        val reportResolution = resolve(report)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.ConfirmDeleteReview("10")), deleteResolution)
        assertEquals(Resolution.ReportReview(STORE_ID, 10L), reportResolution)
    }

    @Test
    fun `같은가게의_방문인증링크는_EDIT섹션_위치로_방문인증을_연다`() {
        // Given
        val event = SDActionEvent(link = appLink("/visit?storeId=$STORE_ID"))

        // When
        val resolution = resolve(event)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.Visit(STORE_ID, "호떡집", 37.4979, 127.0276)), resolution)
    }

    @Test
    fun `웹링크와_다른화면_앱링크는_공통링크처리로_넘긴다`() {
        // Given
        val web = SDActionEvent(link = SDLink(type = SDLinkType.WEB, link = "https://instagram.com/store"))
        val contributors = SDActionEvent(link = appLink("/store-contributors?storeId=$STORE_ID"))

        // When
        val webResolution = resolve(web)
        val contributorsResolution = resolve(contributors)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.OpenLink(web.link!!)), webResolution)
        assertEquals(Resolution.Navigate(StoreDetailDestination.OpenLink(contributors.link!!)), contributorsResolution)
    }

    @Test
    fun `필수파라미터가_빠진_액션은_무시한다`() {
        // Given
        val shareWithoutUrl = customEvent(SDCustomActionType.STORE_PREVIEW_SECTION_SHARE)

        // When
        val resolution = resolve(shareWithoutUrl)

        // Then
        assertEquals(Resolution.Ignore, resolution)
    }

    @Test
    fun `계좌복사는_계좌문구가_있을때만_복사하고_다른_파라미터는_복사하지_않는다`() {
        // Given
        val withAccount = customEvent(
            SDCustomActionType.STORE_INFO_V2_SECTION_COPY_ACCOUNT_HOLDER,
            SDCustomActionModel.STORE_ID to STORE_ID,
            SDCustomActionModel.ACCOUNT_NUMBER to "카카오뱅크 3333-01-1234567",
        )
        val withoutAccount = customEvent(SDCustomActionType.STORE_INFO_V2_SECTION_COPY_ACCOUNT_HOLDER, SDCustomActionModel.STORE_ID to STORE_ID)

        // When
        val copy = resolve(withAccount)
        val ignored = resolve(withoutAccount)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.CopyText("카카오뱅크 3333-01-1234567")), copy)
        assertEquals(Resolution.Ignore, ignored)
    }
}
