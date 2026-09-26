package com.zion830.threedollars.core.ui.sdui.preview

import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDHeaderModel
import com.threedollar.common.sdui.model.element.SDActionBarModel
import com.threedollar.common.sdui.model.element.SDButtonModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.element.SDCustomActionModel
import com.threedollar.common.sdui.model.element.SDCustomActionType
import com.threedollar.common.sdui.model.element.SDImageAlignment
import com.threedollar.common.sdui.model.element.SDImageModel
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.element.SDRatingChipModel
import com.threedollar.common.sdui.model.element.SDSurfaceStyleModel
import com.threedollar.common.sdui.model.element.SDTextModel
import com.threedollar.common.sdui.model.element.SDToggleActionModel
import com.threedollar.common.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.threedollar.common.sdui.model.section.SDStoreAppearanceDaySectionModel
import com.threedollar.common.sdui.model.section.SDStoreCalloutSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCouponSectionModel
import com.threedollar.common.sdui.model.section.SDStoreCtaSectionModel
import com.threedollar.common.sdui.model.section.SDStoreEditSectionModel
import com.threedollar.common.sdui.model.section.SDStoreImageSectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV1SectionModel
import com.threedollar.common.sdui.model.section.SDStoreInfoV2SectionModel
import com.threedollar.common.sdui.model.section.SDStoreMarginSectionModel
import com.threedollar.common.sdui.model.section.SDStorePostSectionModel
import com.threedollar.common.sdui.model.section.SDStorePreviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreReviewSectionModel
import com.threedollar.common.sdui.model.section.SDStoreTabSectionModel
import com.threedollar.common.sdui.model.section.SDStoreVisitSectionModel

private const val WHITE = "#FFFFFF"
private const val GRAY0 = "#FAFAFA"
private const val BLACK = "#0F0F0F"
private const val PINK = "#FF858F"
private const val BOSS_STORE_ID = "120009"
private const val USER_STORE_ID = "118"

private fun span(size: Int, weight: Int, color: String, text: String): String =
    "<span style=\"font-size:${size}px; font-weight:$weight; color:$color\">$text</span>"

private fun html(size: Int, weight: Int, color: String, text: String): SDTextModel =
    SDTextModel(text = span(size, weight, color, text), isHtml = true, fontColor = color)

private fun spans(fontColor: String, vararg spans: String): SDTextModel =
    SDTextModel(text = spans.joinToString(separator = ""), isHtml = true, fontColor = fontColor)

private fun text(value: String, color: String = BLACK): SDTextModel =
    SDTextModel(text = value, isHtml = false, fontColor = color)

private fun appImage(name: String): String = "https://storage.threedollars.co.kr/app/$name"

private fun image(url: String, width: Int, height: Int = width, dimmed: Boolean = false): SDImageModel =
    SDImageModel(url = url, style = SDImageModel.Style(width = width.toFloat(), height = height.toFloat(), dimmed = dimmed))

private fun style(background: String, border: String? = null): SDSurfaceStyleModel =
    SDSurfaceStyleModel(
        backgroundColor = background,
        border = border?.let { SDSurfaceStyleModel.Border(color = it, width = 1f) }
    )

private fun appLink(path: String): SDLink = SDLink(type = SDLinkType.APP_SCHEME, link = path)

private fun webLink(url: String): SDLink = SDLink(type = SDLinkType.WEB, link = url)

private fun action(type: SDCustomActionType, vararg params: Pair<String, Any?>): SDCustomActionModel =
    SDCustomActionModel(actionType = type, extraParams = mapOf(*params))

private fun chip(
    text: SDTextModel?,
    image: SDImageModel? = null,
    imageAlignment: SDImageAlignment? = null,
    additionalText: SDTextModel? = null,
    spacing: Float? = null,
    style: SDSurfaceStyleModel? = null,
): SDChipModel = SDChipModel(
    image = image,
    text = text,
    imageAlignment = imageAlignment ?: image?.let { SDImageAlignment.START },
    additionalText = additionalText,
    contentSpacing = spacing,
    style = style
)

private fun button(
    text: SDTextModel? = null,
    image: SDImageModel? = null,
    imageAlignment: SDImageAlignment? = null,
    link: SDLink? = null,
    customAction: SDCustomActionModel? = null,
    style: SDSurfaceStyleModel? = null,
): SDButtonModel = SDButtonModel(
    text = text,
    image = image,
    imageAlignment = imageAlignment,
    link = link,
    customAction = customAction,
    style = style
)

private fun actionBar(button: SDButtonModel): SDActionBarModel = SDActionBarModel(button = button)

private fun trailingAction(label: String, link: SDLink? = null, customAction: SDCustomActionModel? = null): SDButtonModel =
    button(text = html(12, 700, PINK, label), link = link, customAction = customAction, style = style(WHITE))

private fun sectionTitle(title: String, count: String? = null): SDTextModel =
    if (count == null) {
        html(16, 700, BLACK, title)
    } else {
        spans(BLACK, span(16, 700, BLACK, "$title "), span(16, 400, BLACK, count))
    }

private fun stars(size: Int, url: String = appImage("start_pink.png")): SDRatingChipModel =
    SDRatingChipModel(images = List(5) { image(url, size) }, style = style("#FFF3F4"))

private fun likeToggle(storeId: String, idKey: String, id: String, count: Int, isSelected: Boolean = false): SDToggleActionModel {
    val (addType, cancelType) = if (idKey == SDCustomActionModel.POST_ID) {
        SDCustomActionType.STORE_POST_SECTION_ADD_LIKE to SDCustomActionType.STORE_POST_SECTION_CANCEL_LIKE
    } else {
        SDCustomActionType.STORE_REVIEW_SECTION_ADD_LIKE to SDCustomActionType.STORE_REVIEW_SECTION_CANCEL_LIKE
    }
    val params = arrayOf<Pair<String, Any?>>(SDCustomActionModel.STORE_ID to storeId, idKey to id, SDCustomActionModel.STICKER_ID to "LIKE")
    return SDToggleActionModel(
        selected = button(
            text = html(10, 500, "#FF5C43", "좋아요 $count"),
            image = image(appImage("heart_fill.png"), 16),
            customAction = action(cancelType, *params),
            style = style(GRAY0)
        ),
        unselected = button(
            text = html(10, 500, "#191919", "좋아요 $count"),
            image = image(appImage("heart_line.png"), 16),
            customAction = action(addType, *params),
            style = style(GRAY0)
        ),
        isSelected = isSelected
    )
}

private fun editSection(
    address: String,
    latitude: Double,
    longitude: Double,
    actionBars: List<SDActionBarModel>,
): SDStoreEditSectionModel = SDStoreEditSectionModel(
    sectionId = "EDIT",
    map = SDStoreEditSectionModel.Map(
        location = SDStoreEditSectionModel.Location(latitude = latitude, longitude = longitude),
        footerLeft = actionBar(
            button(
                text = html(12, 500, WHITE, address),
                image = image(appImage("copy.png"), 16),
                imageAlignment = SDImageAlignment.START,
                customAction = action(SDCustomActionType.STORE_EDIT_SECTION_COPY_ADDRESS, SDCustomActionModel.ADDRESS to address),
                style = style("#181818")
            )
        ),
        footerRight = actionBar(
            button(
                image = image(appImage("zoom_3x.png"), 20),
                customAction = action(SDCustomActionType.STORE_EDIT_SECTION_MAP_ENLARGE),
                style = style(WHITE)
            )
        )
    ),
    actionBars = actionBars,
    style = style(WHITE)
)

private fun reviewCard(
    id: String,
    date: String,
    medalUrl: String,
    medalName: String,
    body: String,
    likeCount: Int,
    images: List<String> = emptyList(),
    reply: SDStoreReviewSectionModel.Reply? = null,
): SDStoreReviewSectionModel.Card = SDStoreReviewSectionModel.Card(
    cardId = "R:$id",
    header = SDHeaderModel(
        title = html(12, 500, "#2E2E2E", "리뷰어"),
        subTitle = html(12, 500, "#B7B7B7", date),
        trailingAction = button(
            text = html(12, 700, "#787878", "신고"),
            customAction = action(
                SDCustomActionType.STORE_REVIEW_SECTION_REPORT,
                SDCustomActionModel.STORE_ID to BOSS_STORE_ID,
                SDCustomActionModel.REVIEW_ID to id
            ),
            style = style(GRAY0)
        )
    ),
    metadata = listOf(
        chip(
            text = html(10, 500, PINK, medalName),
            image = image(medalUrl, 16),
            spacing = 2f,
            style = style("#FFF3F4")
        )
    ),
    stars = stars(size = 12),
    images = images.map { image(it, 96) },
    body = html(14, 400, "#2E2E2E", body),
    like = likeToggle(BOSS_STORE_ID, SDCustomActionModel.REVIEW_ID, id, likeCount),
    reply = reply,
    link = null,
    style = style(GRAY0),
    clickLog = null
)

private fun relatedStoreCard(id: String, name: String, rating: String, reviewCount: String, distance: String, menuIcon: String): ImagePreviewCardModel =
    ImagePreviewCardModel(
        cardId = "S:$id",
        image = image(menuIcon, 68),
        title = text(name),
        metricLabel = listOf(
            chip(text = text(rating, "#969696"), image = image(appImage("star_gray.png"), 12), spacing = 2f),
            chip(text = text(reviewCount, "#969696"), image = image(appImage("review_gray.png"), 12), spacing = 2f)
        ),
        contextLabel = listOf(
            chip(text = text(distance, PINK), image = image(appImage("location_pink.png"), 12), spacing = 2f)
        ),
        link = appLink("/store?storeType=USER_STORE&storeId=$id"),
        style = ImagePreviewCardModel.Style(backgroundColor = WHITE),
        refs = listOf(ImagePreviewCardModel.Ref(type = "STORE", storeId = id, storeType = "USER_STORE"))
    )

/**
 * `@Preview` 전용 샘플 모델. 값은 `core/network/src/test/resources/sdui/store-v2/` 서버 응답 픽스처에서 옮겨 왔다.
 */
internal object SDPreviewFixtures {

    private const val BOSS_IMAGE_1 = "https://storage.dev.threedollars.co.kr/boss/store/v1/v1-bd8f9756-c3a2-4333-be66-f6cfc39054bb.jpeg"
    private const val BOSS_IMAGE_2 = "https://storage.dev.threedollars.co.kr/boss/store/v1/v1-d5c3cb0c-a7d3-4d4a-8d25-3d1853f2bb48.jpeg"
    private const val BOSS_IMAGE_3 =
        "https://storage.dev.threedollars.co.kr/boss/store-certification/v1/v1-4217b84f-00ee-407f-979d-28c2f1a803c7.jpeg"
    private const val REVIEW_IMAGE_1 = "https://storage.dev.threedollars.co.kr/store-review/v1/v1-1ba31e4b-6727-4c24-9a3c-b0caca26db28.jpg"
    private const val REVIEW_IMAGE_2 = "https://storage.dev.threedollars.co.kr/store-review/v1/v1-d138103f-f9b4-4049-ae72-ff847b938f8a.jpg"
    private const val REVIEW_IMAGE_3 = "https://storage.dev.threedollars.co.kr/store-review/v1/v1-4ada100f-956d-4809-b236-3ec9f5fa202b.jpeg"
    private const val MENU_IMAGE = "https://storage.dev.threedollars.co.kr/boss/store-menu/v1/v1-6c7d4e18-fd08-4a93-9c2a-27ad0f76ff10.jpeg"
    private const val MEDAL_CHALLENGER = "https://storage.threedollars.co.kr/medal/v1-challenger-default.png?version=1"
    private const val MEDAL_NEWBIE = "https://storage.threedollars.co.kr/medal/v1-newbie-default.png?version=1"
    private const val MENU_ICON_JAJANG = "https://storage.threedollars.co.kr/menu/icon_menu_3x_Jajangmyeon.png?version=1"
    private const val MENU_ICON_HODDUCK = "https://storage.threedollars.co.kr/menu/v1_hodduck.png?version=1"
    private const val MENU_ICON_BUTTER = "https://storage.threedollars.co.kr/menu/icon_menu_24pt%3Dbutter.png"
    private const val MENU_ICON_SALT_BREAD = "https://storage.threedollars.co.kr/menu/wakbbu_salt_bread_3x.png"

    val plainText: SDTextModel = text("정보 & 메뉴", "#787878")

    val htmlText: SDTextModel = spans(
        BLACK,
        span(16, 700, BLACK, "가게 사진 "),
        span(16, 400, BLACK, "3개")
    )

    val starChip: SDChipModel = chip(
        text = html(14, 400, "#5A5A5A", "4.6 (500)"),
        image = image(appImage("star_solid_bold.png"), 16),
        spacing = 2f,
        style = style(WHITE)
    )

    val metadataChips: List<SDChipModel> = listOf(
        chip(text = html(14, 600, "#2E2E2E", "영업중"), style = style(WHITE)),
        chip(text = html(14, 400, "#787878", "15.7km"), style = style(WHITE)),
        chip(text = html(14, 400, "#787878", "사장님 직영점"), style = style(WHITE))
    )

    val visitButton: SDButtonModel = button(
        text = html(14, 600, WHITE, "방문 인증"),
        image = image(appImage("chevron_right_4x.png"), 12),
        imageAlignment = SDImageAlignment.END,
        link = appLink("/visit?storeId=$BOSS_STORE_ID"),
        style = style(PINK)
    )

    val postLike: SDToggleActionModel = likeToggle(BOSS_STORE_ID, SDCustomActionModel.POST_ID, "18741328", count = 1)

    val postLikeSelected: SDToggleActionModel = likeToggle(BOSS_STORE_ID, SDCustomActionModel.POST_ID, "18741328", count = 2, isSelected = true)

    val ratingChip: SDRatingChipModel = stars(size = 24)

    val header: SDHeaderModel = SDHeaderModel(
        title = html(16, 700, BLACK, "가게 정보 &amp; 메뉴"),
        subTitle = html(12, 500, "#787878", "19시간 전 업데이트"),
        trailingAction = trailingAction(
            label = "정보 수정",
            customAction = action(SDCustomActionType.STORE_EDIT_SECTION_UPDATE, SDCustomActionModel.STORE_ID to BOSS_STORE_ID)
        )
    )

    val actionBars: List<SDActionBarModel> = listOf(
        actionBar(visitButton),
        actionBar(
            button(
                text = html(14, 600, "#F9737E", "리뷰 작성"),
                customAction = action(SDCustomActionType.STORE_PREVIEW_SECTION_REVIEW_WRITE, SDCustomActionModel.STORE_ID to BOSS_STORE_ID),
                style = style("#FFECEE", border = "#FFECEE")
            )
        ),
        actionBar(
            button(
                text = html(14, 400, "#5A5A5A", "공유"),
                image = image(appImage("share_4x.png"), 16),
                imageAlignment = SDImageAlignment.START,
                customAction = action(
                    SDCustomActionType.STORE_PREVIEW_SECTION_SHARE,
                    SDCustomActionModel.STORE_ID to BOSS_STORE_ID,
                    SDCustomActionModel.STORE_TYPE to "BOSS_STORE",
                    SDCustomActionModel.URL to "https://app.dev.threedollars.co.kr/store?storeType=BOSS_STORE&storeId=$BOSS_STORE_ID"
                ),
                style = style(WHITE, border = "#E2E2E2")
            )
        ),
        actionBar(
            button(
                text = html(14, 400, "#5A5A5A", "길안내"),
                image = image(appImage("location_line_4x.png"), 16),
                imageAlignment = SDImageAlignment.START,
                customAction = action(
                    SDCustomActionType.STORE_PREVIEW_SECTION_NAVIGATION,
                    SDCustomActionModel.LATITUDE to 37.36954969792162,
                    SDCustomActionModel.LONGITUDE to 126.9314217684027,
                    SDCustomActionModel.STORE_NAME to "뽀미네 두쫀쿠 붕어빵"
                ),
                style = style(WHITE, border = "#E2E2E2")
            )
        )
    )

    val imagePreviewCard: ImagePreviewCardModel =
        relatedStoreCard("12804902", "역삼 슈크림 붕어빵", "4.5", "12개", "120m", MENU_ICON_BUTTER)

    val callout: SDStoreCalloutSectionModel = SDStoreCalloutSectionModel(
        sectionId = "CALLOUT",
        content = SDStoreCalloutSectionModel.Content(
            image = image(appImage("star_3.png"), 21),
            text = spans(
                WHITE,
                span(14, 600, WHITE, "가슴속 3천원이 직접 확인한 "),
                span(14, 600, PINK, "공식인증"),
                span(14, 600, WHITE, "가게입니다!")
            ),
            title = null,
            subTitle = null,
            footerLeftButton = null,
            style = style("#232323")
        ),
        style = style(WHITE)
    )

    val preview: SDStorePreviewSectionModel = SDStorePreviewSectionModel(
        sectionId = "PREVIEW",
        header = SDStorePreviewSectionModel.Header(title = html(20, 700, BLACK, "뽀미네 두쫀쿠 붕어빵"), badge = null),
        metadata = SDStorePreviewSectionModel.Metadata(
            primary = listOf(chip(text = html(14, 400, "#787878", "왁뿌 소금빵, 소금빵, 문어빵"), style = style(WHITE)), starChip),
            secondary = metadataChips,
            separator = image(appImage("rectangle_465.png"), 2)
        ),
        contributorActionBar = null,
        actionBars = actionBars,
        images = listOf(BOSS_IMAGE_1, BOSS_IMAGE_2, BOSS_IMAGE_3).map { image(it, 158) },
        bodies = emptyList(),
        additionalInfos = SDStorePreviewSectionModel.AdditionalInfos(
            type = "STORE",
            isSubscriber = false,
            storeId = BOSS_STORE_ID,
            storeType = "BOSS_STORE"
        ),
        style = style(WHITE)
    )

    val userStorePreview: SDStorePreviewSectionModel = preview.copy(
        header = SDStorePreviewSectionModel.Header(title = html(20, 700, BLACK, "역삼역 호떡"), badge = null),
        metadata = SDStorePreviewSectionModel.Metadata(
            primary = listOf(
                chip(text = html(14, 400, "#787878", "중식, 호떡"), style = style(WHITE)),
                starChip.copy(text = html(14, 400, "#5A5A5A", "4.2 (18)"))
            ),
            secondary = listOf(
                chip(text = html(14, 400, "#787878", "47m"), style = style(WHITE)),
                chip(text = html(14, 400, "#787878", "최근 방문 3명"), style = style(WHITE))
            ),
            separator = image(appImage("rectangle_465.png"), 2)
        ),
        contributorActionBar = actionBar(
            button(
                text = spans(
                    "#787878",
                    span(14, 600, "#5A5A5A", "붕어빵러버"),
                    span(14, 400, "#787878", "님이 가게 정보를 등록했어요")
                ),
                image = image(appImage("chevron-right-gray.png"), 20),
                imageAlignment = SDImageAlignment.END,
                link = appLink("/store-contributors?storeId=$USER_STORE_ID"),
                style = style(WHITE)
            )
        ),
        images = emptyList(),
        additionalInfos = SDStorePreviewSectionModel.AdditionalInfos(
            type = "STORE",
            isSubscriber = false,
            storeId = USER_STORE_ID,
            storeType = "USER_STORE"
        )
    )

    val tab: SDStoreTabSectionModel = SDStoreTabSectionModel(
        sectionId = "TAB",
        tabs = listOf("홈" to "PREVIEW", "정보 & 메뉴" to "INFO", "사진" to "IMAGE", "리뷰 500" to "REVIEW").map { (label, anchor) ->
            actionBar(
                button(
                    text = text(label, "#787878"),
                    link = appLink("/stores/$BOSS_STORE_ID#$anchor"),
                    style = style(WHITE)
                )
            )
        },
        style = style(WHITE)
    )

    val edit: SDStoreEditSectionModel = editSection(
        address = "서울특별시 강남구 역삼동 858",
        latitude = 37.4983268205018,
        longitude = 127.0276096087437,
        actionBars = listOf(
            actionBar(
                button(
                    text = html(14, 600, "#5A5A5A", "정보 수정"),
                    image = image(appImage("Edit_fill.png"), 20),
                    imageAlignment = SDImageAlignment.START,
                    customAction = action(SDCustomActionType.STORE_EDIT_SECTION_UPDATE, SDCustomActionModel.STORE_ID to USER_STORE_ID),
                    style = style("#F4F4F4")
                )
            ),
            actionBar(
                button(
                    text = html(14, 600, "#FF5C43", "없는 장소 제보"),
                    image = image(appImage("deletion.png"), 20),
                    customAction = action(SDCustomActionType.STORE_EDIT_SECTION_REPORT, SDCustomActionModel.STORE_ID to USER_STORE_ID),
                    style = style("#F4F4F4")
                )
            )
        )
    )

    val bossEdit: SDStoreEditSectionModel = editSection(
        address = "경기도 군포시 산본동 1065-4",
        latitude = 37.36954969792162,
        longitude = 126.9314217684027,
        actionBars = emptyList()
    )

    val coupon: SDStoreCouponSectionModel = SDStoreCouponSectionModel(
        sectionId = "COUPON",
        header = SDHeaderModel(title = sectionTitle("쿠폰"), trailingAction = trailingAction("내 쿠폰함 가기", link = appLink("/myCoupons"))),
        cards = listOf(
            SDStoreCouponSectionModel.Card(
                cardId = "C:881960829125365760",
                badge = null,
                title = html(16, 700, "#2E2E2E", "두쫀쿠 붕어빵 1개 증정"),
                subTitle = html(14, 400, "#5A5A5A", "26.08.31 ~ 26.09.30"),
                trailingButton = button(
                    image = image(appImage("download.png"), 30),
                    customAction = action(
                        SDCustomActionType.STORE_COUPON_SECTION_COUPON_ISSUE,
                        SDCustomActionModel.STORE_ID to BOSS_STORE_ID,
                        SDCustomActionModel.COUPON_ID to "881960829125365760"
                    ),
                    style = style("#FDF1FD")
                ),
                style = style("#FDF1FD"),
                clickLog = null
            ),
            SDStoreCouponSectionModel.Card(
                cardId = "C:881960829125365761",
                badge = chip(text = html(12, 600, WHITE, "D-3"), style = style("#2E2E2E")),
                title = html(16, 700, "#2E2E2E", "소금빵 1,000원 할인"),
                subTitle = html(14, 400, "#5A5A5A", "26.09.01 ~ 26.09.27"),
                trailingButton = button(
                    text = html(12, 700, "#F9737E", "사용하기"),
                    customAction = action(
                        SDCustomActionType.STORE_COUPON_SECTION_COUPON_USE,
                        SDCustomActionModel.STORE_ID to BOSS_STORE_ID,
                        SDCustomActionModel.COUPON_ID to "881960829125365761",
                        SDCustomActionModel.COUPON_ISSUED_KEY to "ISSUED-1"
                    ),
                    style = style(WHITE)
                ),
                style = style("#FDF1FD"),
                clickLog = null
            ),
            SDStoreCouponSectionModel.Card(
                cardId = "C:881960829125365762",
                badge = null,
                title = html(16, 700, "#A1A1A1", "두쫀쿠 붕어빵 1개 증정 (사용 완료, 긴 제목 줄바꿈 확인)"),
                subTitle = html(14, 400, "#A1A1A1", "26.08.31 ~ 26.09.30"),
                trailingButton = button(text = html(12, 700, "#A1A1A1", "사용 완료")),
                style = style("#F4F4F4"),
                clickLog = null
            )
        ),
        style = style(WHITE)
    )

    val visit: SDStoreVisitSectionModel = SDStoreVisitSectionModel(
        sectionId = "VISIT",
        header = SDHeaderModel(title = sectionTitle("이번 달 방문 인증 내역")),
        summary = SDStoreVisitSectionModel.Summary(
            chips = listOf(
                chip(
                    text = spans(BLACK, span(14, 400, BLACK, "방문 성공 "), span(14, 600, BLACK, "0명")),
                    image = image(appImage("visit_success.png"), 24),
                    spacing = 8f,
                    style = style("#F1FFF8")
                ),
                chip(
                    text = spans(BLACK, span(14, 400, BLACK, "방문 실패 "), span(14, 600, BLACK, "1명")),
                    image = image(appImage("visit_failed.png"), 24),
                    spacing = 2f,
                    style = style("#FFF3F4")
                )
            )
        ),
        history = SDStoreVisitSectionModel.History(
            items = listOf(
                chip(
                    text = html(12, 500, "#969696", "2026.09.15 20시"),
                    image = image(appImage("visit_failed_icon.png"), 4),
                    additionalText = html(12, 700, BLACK, "뽀미"),
                    spacing = 8f,
                    style = style(GRAY0)
                )
            ),
            moreText = null,
            style = style(GRAY0)
        ),
        style = style(WHITE)
    )

    val visitEmpty: SDStoreVisitSectionModel = visit.copy(
        header = SDHeaderModel(title = sectionTitle("이번 달 방문 인증 내역이 없어요 :(")),
        history = SDStoreVisitSectionModel.History(items = emptyList(), moreText = null, style = style(GRAY0))
    )

    val post: SDStorePostSectionModel = SDStorePostSectionModel(
        sectionId = "POST",
        header = SDHeaderModel(
            title = sectionTitle("가게 소식"),
            trailingAction = trailingAction("소식 더보기(20개)", link = appLink("/postList?storeId=$BOSS_STORE_ID"))
        ),
        cards = listOf(
            SDStorePostSectionModel.Card(
                cardId = "POST:18741328",
                header = chip(
                    text = html(14, 700, BLACK, "뽀미네 두쫀쿠 붕어빵"),
                    image = image(MENU_ICON_SALT_BREAD, 40),
                    additionalText = html(12, 400, "#969696", "2026.07.24"),
                    spacing = 8f,
                    style = style(GRAY0)
                ),
                images = listOf(image("https://storage.dev.threedollars.co.kr/store-post/v2/v1-2a15e807-895d-4e57-bce4-a592f6238287.png", 208)),
                body = html(
                    14,
                    400,
                    "#1A1A1A",
                    "오늘은 산본역 2번 출구 앞에서 영업해요!<br>두쫀쿠 붕어빵 신메뉴 나왔습니다.<br>" +
                        "재료 소진 시 조기 마감될 수 있어요. 늘 찾아주셔서 감사합니다 :)<br>다음 주 화요일은 재료 준비로 쉬어갑니다."
                ),
                like = postLike,
                link = null,
                style = style(GRAY0),
                clickLog = null
            )
        ),
        style = style(WHITE)
    )

    val imageSection: SDStoreImageSectionModel = SDStoreImageSectionModel(
        sectionId = "IMAGE",
        header = SDHeaderModel(
            title = sectionTitle("가게 사진", "8개"),
            trailingAction = trailingAction(
                label = "사진 제보",
                customAction = action(SDCustomActionType.STORE_IMAGE_SECTION_ADD_IMAGE, SDCustomActionModel.STORE_ID to BOSS_STORE_ID)
            )
        ),
        cards = listOf(REVIEW_IMAGE_1, REVIEW_IMAGE_2, REVIEW_IMAGE_3, BOSS_IMAGE_1).mapIndexed { index, url ->
            val isLast = index == 3
            SDStoreImageSectionModel.Card(
                cardId = "IMAGE:$index",
                image = image(url, 96, dimmed = isLast),
                title = if (isLast) html(13, 700, WHITE, "+4") else null,
                subTitle = if (isLast) html(11, 500, WHITE, "더보기") else null,
                link = if (isLast) appLink("/storeImages?storeId=$BOSS_STORE_ID") else null,
                customAction = if (isLast) {
                    null
                } else {
                    action(
                        SDCustomActionType.STORE_IMAGE_SECTION_IMAGE_ENLARGE,
                        SDCustomActionModel.IMAGE_ID to "$index",
                        SDCustomActionModel.IMAGE_URL to url
                    )
                },
                style = null,
                clickLog = null
            )
        },
        style = style(WHITE)
    )

    val imageSectionEmpty: SDStoreImageSectionModel = imageSection.copy(
        header = imageSection.header?.copy(title = sectionTitle("가게 사진", "0개")),
        cards = emptyList()
    )

    val appearanceDay: SDStoreAppearanceDaySectionModel = SDStoreAppearanceDaySectionModel(
        sectionId = "APPEARANCE_DAY",
        header = SDHeaderModel(title = sectionTitle("영업 일정")),
        items = listOf(
            "월요일" to null,
            "화요일" to "11:00 - 21:00",
            "수요일" to "11:00 - 21:00",
            "목요일" to "11:00 - 21:00",
            "금요일" to "11:00 - 22:00",
            "토요일" to "12:00 - 22:00",
            "일요일" to "12:00 - 20:00"
        ).map { (day, hours) ->
            SDStoreAppearanceDaySectionModel.Item(
                leadingText = html(14, 600, BLACK, day),
                primaryText = if (hours == null) html(14, 600, "#969696", "휴무") else html(14, 600, "#464646", hours),
                secondaryText = html(12, 500, "#787878", if (hours == null) "-" else "산본역 2번 출구 앞"),
                style = style("#F8F8F8")
            )
        },
        style = style(WHITE)
    )

    val infoV1: SDStoreInfoV1SectionModel = SDStoreInfoV1SectionModel(
        sectionId = "INFO",
        header = SDHeaderModel(
            title = html(16, 700, BLACK, "가게 정보 &amp; 메뉴"),
            subTitle = html(12, 500, "#787878", "2023.08.14 업데이트"),
            trailingAction = trailingAction(
                label = "정보 수정",
                customAction = action(SDCustomActionType.STORE_EDIT_SECTION_UPDATE, SDCustomActionModel.STORE_ID to USER_STORE_ID)
            )
        ),
        informationCard = SDStoreInfoV1SectionModel.InformationCard(
            rows = listOf(
                SDStoreInfoV1SectionModel.Row(
                    type = SDStoreInfoV1SectionModel.RowType.TRAILING_TEXT,
                    label = html(12, 700, BLACK, "가게 형태"),
                    value = html(12, 500, "#5A5A5A", "길거리"),
                    chips = null,
                    items = null
                ),
                SDStoreInfoV1SectionModel.Row(
                    type = SDStoreInfoV1SectionModel.RowType.CHIP_GROUP,
                    label = html(12, 700, BLACK, "출몰 요일"),
                    value = null,
                    chips = listOf("월" to true, "화" to true, "수" to false, "목" to false, "금" to true, "토" to false, "일" to true)
                        .map { (day, open) ->
                            if (open) {
                                chip(text = html(12, 500, WHITE, day), style = style("#5A5A5A"))
                            } else {
                                chip(text = html(12, 500, "#B7B7B7", day), style = style("#F4F4F4"))
                            }
                        },
                    items = null
                ),
                SDStoreInfoV1SectionModel.Row(
                    type = SDStoreInfoV1SectionModel.RowType.TRAILING_TEXT,
                    label = html(12, 700, BLACK, "출몰 시간대"),
                    value = html(12, 500, "#5A5A5A", "오후 5시 - 오후 10시"),
                    chips = null,
                    items = null
                ),
                SDStoreInfoV1SectionModel.Row(
                    type = SDStoreInfoV1SectionModel.RowType.INLINE_OPTION,
                    label = html(12, 700, BLACK, "결제 방식"),
                    value = null,
                    chips = null,
                    items = listOf("현금" to true, "계좌이체" to true, "카드" to false).map { (method, selected) ->
                        SDStoreInfoV1SectionModel.SelectableText(
                            text = html(12, 500, if (selected) BLACK else "#B7B7B7", method),
                            isSelected = selected
                        )
                    }
                )
            ),
            style = style(GRAY0)
        ),
        menuCard = SDStoreInfoV1SectionModel.MenuCard(
            groups = listOf(
                MENU_ICON_JAJANG to ("중식" to listOf("짜장면" to "6000원", "짬뽕" to "7000원", "탕수육 (소)" to "12000원")),
                MENU_ICON_HODDUCK to ("호떡" to listOf("꿀호떡" to "1500원", "씨앗호떡" to "2000원", "야채호떡" to "2000원"))
            ).map { (icon, group) ->
                val (category, menus) = group
                SDStoreInfoV1SectionModel.MenuGroup(
                    header = chip(text = html(14, 600, BLACK, category), image = image(icon, 28), spacing = 8f),
                    items = menus.map { (name, price) ->
                        SDStoreInfoV1SectionModel.MenuItem(
                            primaryText = html(12, 500, BLACK, name),
                            secondaryText = html(12, 500, BLACK, price)
                        )
                    }
                )
            },
            style = style(GRAY0)
        ),
        style = style(WHITE)
    )

    val infoV2: SDStoreInfoV2SectionModel = SDStoreInfoV2SectionModel(
        sectionId = "INFO",
        header = SDHeaderModel(
            title = html(16, 700, BLACK, "가게 정보 &amp; 메뉴"),
            subTitle = html(12, 500, "#787878", "19시간 전 업데이트")
        ),
        imageGallery = SDStoreInfoV2SectionModel.ImageGallery(
            images = listOf(BOSS_IMAGE_1, BOSS_IMAGE_2, BOSS_IMAGE_3).map { image(it, 288, 180) }
        ),
        detailCard = SDStoreInfoV2SectionModel.DetailCard(
            rows = listOf(
                SDStoreInfoV2SectionModel.DetailRow(
                    type = SDStoreInfoV2SectionModel.DetailRowType.LINK,
                    label = html(12, 500, BLACK, "SNS"),
                    value = html(12, 500, PINK, "https://threedollars.co.kr"),
                    link = webLink("https://threedollars.co.kr"),
                    title = null,
                    body = null
                ),
                SDStoreInfoV2SectionModel.DetailRow(
                    type = SDStoreInfoV2SectionModel.DetailRowType.TEXT,
                    label = null,
                    value = null,
                    link = null,
                    title = html(12, 500, BLACK, "사장님 한마디"),
                    body = html(12, 500, "#787878", "매일 아침 직접 반죽해서 구워요. 두쫀쿠 붕어빵 꼭 드셔보세요!")
                )
            ),
            style = style(GRAY0)
        ),
        accountCards = listOf(
            SDStoreInfoV2SectionModel.AccountCard(
                title = html(12, 700, BLACK, "계좌번호"),
                account = chip(
                    text = html(12, 500, "#5A5A5A", "카카오뱅크 3333-01-0000000"),
                    additionalText = html(12, 500, "#5A5A5A", "뽀미")
                ),
                copyButton = button(
                    text = html(12, 600, PINK, "복사"),
                    customAction = action(SDCustomActionType.STORE_INFO_V2_SECTION_COPY_ACCOUNT_HOLDER),
                    style = style(WHITE)
                ),
                style = style(GRAY0)
            )
        ),
        menuListCard = SDStoreInfoV2SectionModel.MenuListCard(
            items = listOf(
                "두쫀쿠 붕어빵 3개" to "3,000원",
                "슈크림 붕어빵 3개" to "2,000원",
                "왁뿌 소금빵" to "15,000원"
            ).map { (name, price) ->
                SDStoreInfoV2SectionModel.MenuItem(
                    image = image(MENU_IMAGE, 44),
                    primaryText = html(12, 500, BLACK, name),
                    secondaryText = html(12, 500, "#787878", price)
                )
            },
            style = style(GRAY0)
        ),
        style = style(WHITE)
    )

    val cta: SDStoreCtaSectionModel = SDStoreCtaSectionModel(
        sectionId = "CTA",
        content = SDStoreCtaSectionModel.Content(
            title = html(14, 600, "#5A5A5A", "🧑‍🍳 혹시 이 가게 사장님이신가요?"),
            subTitle = html(12, 500, "#969696", "가슴속 3천원 사장님앱으로 가게 정보를 직접 관리할 수 있습니다!"),
            footerLeftButton = button(
                text = html(12, 500, "#00C667", "사장님 앱 소개보기"),
                image = image(appImage("chevron_green_right.png"), 12),
                imageAlignment = SDImageAlignment.END,
                link = webLink("https://massive-iguana-121.notion.site/3-28c7ad52990e809caba2fb2040677a2a"),
                style = style(GRAY0)
            )
        ),
        style = style(GRAY0)
    )

    val review: SDStoreReviewSectionModel = SDStoreReviewSectionModel(
        sectionId = "REVIEW",
        header = SDHeaderModel(
            title = sectionTitle("방문자 리뷰", "500개"),
            trailingAction = trailingAction(
                label = "리뷰 쓰기",
                customAction = action(SDCustomActionType.STORE_REVIEW_SECTION_REVIEW_WRITE, SDCustomActionModel.STORE_ID to BOSS_STORE_ID)
            )
        ),
        summary = SDStoreReviewSectionModel.Summary(
            title = html(10, 500, "#969696", "평균 별점"),
            stars = ratingChip,
            rating = html(20, 600, "#2E2E2E", "4.6점"),
            style = style("#FFF3F4")
        ),
        cards = listOf(
            reviewCard(
                id = "17845094",
                date = "2026.06.19",
                medalUrl = MEDAL_CHALLENGER,
                medalName = "붕어빵 전문가",
                body = "두쫀쿠 붕어빵 처음 먹어봤는데 겉은 바삭하고 속은 쫀득해요. 줄 서서 먹을 만합니다!",
                likeCount = 2,
                images = listOf(REVIEW_IMAGE_1, REVIEW_IMAGE_2)
            ),
            SDStoreReviewSectionModel.Card(
                cardId = "R:100573",
                header = null,
                metadata = null,
                stars = null,
                images = null,
                body = html(14, 400, "#969696", "신고에 의해 블라인드 처리된 리뷰입니다."),
                like = null,
                reply = null,
                link = null,
                style = style(GRAY0),
                clickLog = null
            ),
            reviewCard(
                id = "100572",
                date = "2026.06.12",
                medalUrl = MEDAL_NEWBIE,
                medalName = "사무치게 그리운",
                body = "사장님이 친절하시고 팥이 꽉 차 있어요.",
                likeCount = 0,
                images = listOf(REVIEW_IMAGE_3),
                reply = SDStoreReviewSectionModel.Reply(
                    header = SDHeaderModel(
                        title = html(12, 700, BLACK, "사장님"),
                        subTitle = html(12, 500, "#B7B7B7", "2026.06.13")
                    ),
                    body = html(14, 400, "#2E2E2E", "찾아주셔서 감사합니다! 다음에도 따끈하게 구워드릴게요 :)"),
                    style = style("#F4F4F4")
                )
            )
        ),
        more = actionBar(
            button(
                text = html(12, 500, "#787878", "리뷰 497개 더보기"),
                link = appLink("/reviewList?storeType=BOSS_STORE&storeId=$BOSS_STORE_ID"),
                style = style(WHITE)
            )
        ),
        style = style(WHITE)
    )

    val blindedReview: SDStoreReviewSectionModel = review.copy(
        cards = review.cards.orEmpty().filter { it.isBlinded },
        more = null
    )

    val admob: SDStoreAdmobSectionModel = SDStoreAdmobSectionModel(
        sectionId = "AD_MOB",
        cards = listOf(
            SDStoreAdmobSectionModel.Card(type = "ADMOB_CARD", cardId = "ADMOB:AD_MOB:1", clickLog = null, impressionLog = null)
        ),
        style = style(WHITE)
    )

    val margin: SDStoreMarginSectionModel = SDStoreMarginSectionModel(sectionId = "MARGIN", height = 8, style = style(WHITE))

    val relatedStores: SDRelatedStoresSectionModel = SDRelatedStoresSectionModel(
        header = SDHeaderModel(
            title = spans(
                BLACK,
                span(16, 700, BLACK, "바로 가 볼 만한 "),
                span(16, 700, PINK, "근처 "),
                span(16, 700, BLACK, "가게")
            )
        ),
        cards = listOf(
            imagePreviewCard,
            relatedStoreCard("12804901", "산본역 꿀호떡", "4.8", "31개", "250m", MENU_ICON_HODDUCK),
            relatedStoreCard("12804900", "뽀미네 소금빵", "0.0", "0개", "1.2km", MENU_ICON_SALT_BREAD)
        ),
        reference = listOf(
            SDRelatedStoresSectionModel.Reference(
                type = "EXPERIMENT",
                experimentKey = "abtest_related_store_content_type",
                variant = "nearby"
            )
        ),
        sectionId = "RELATED_STORES",
        style = style(WHITE)
    )

    /** `StoreScreenV2WithCoupon.json` 과 같은 순서의 사장님 가게 화면. */
    val bossStoreScreen: List<SDSectionModel> = listOf(
        callout, preview, margin, tab, coupon, margin, bossEdit, infoV2, appearanceDay, margin, admob, margin,
        post, margin, visit, margin, imageSection, margin, review, margin, relatedStores, margin
    )

    /** `StoreScreenV2UserStore.json` 과 같은 순서의 유저 제보 가게 화면. */
    val userStoreScreen: List<SDSectionModel> = listOf(
        userStorePreview, margin, tab, edit, infoV1, cta, margin, admob, margin, visitEmpty, margin,
        imageSectionEmpty, margin, review, margin, relatedStores, margin
    )
}
