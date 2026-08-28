package com.threedollar.data.screen

import com.google.gson.Gson
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailScreenMapperEngagementTest {

    @Test
    fun mapperMapsLiveVisitSummaryChipShapeWithoutDroppingSection() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {
                  "type":"VISIT",
                  "header":{"title":{"text":"<span style=\"font-size:16px; font-weight:700; color:#0F0F0F\">이번 달 방문 인증 내역</span>","isHtml":true,"fontColor":"#0F0F0F"}},
                  "summary":{"chips":[
                    {"image":{"url":"visit-success","style":{"width":24,"height":24}},"text":{"text":"<span style=\"font-size:14px; font-weight:400; color:#0F0F0F\">방문 성공</span><span style=\"font-size:14px; font-weight:600; color:#0F0F0F\">0명</span>","isHtml":true},"style":{"backgroundColor":"#F1FFF8"}},
                    {"image":{"url":"visit-failed","style":{"width":24,"height":24}},"text":{"text":"방문 실패 0명","isHtml":false},"style":{"backgroundColor":"#FFF3F4"}}
                  ]},
                  "history":{"items":[],"style":{"backgroundColor":"#FAFAFA"}}
                }
              ],
              "viewLog":{"screenName":"store_detail"}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val visit = requireNotNull(response.asStoreDetailModelOrNull())
            .sections
            .single() as StoreDetailSectionModel.Visit

        assertEquals(listOf("방문 성공0명", "방문 실패 0명"), visit.summary.chips.map { it.text.displayText() })
    }

    @Test
    fun mapperMapsCouponVisitPostAndReviewInteractionPayloads() {
        val response = Gson().fromJson(
            """
            {
              "sections": [
                {"type":"COUPON","header":{"title":{"text":"쿠폰","isHtml":false,"fontColor":"#111111"}},"cards":[{"cardId":"coupon-1","badge":{"text":{"text":"발급 가능","isHtml":false,"fontColor":"#FF0000"}},"title":{"text":"1천원 할인","isHtml":false,"fontColor":"#111111"},"subTitle":{"text":"오늘까지","isHtml":false,"fontColor":"#555555"},"trailingButton":{"customAction":{"actionType":"STORE_COUPON_SECTION_COUPON_ISSUE","extraParams":{"STORE_ID":100186,"COUPON_ID":"coupon-1"}},"style":{"backgroundColor":"#FFFFFF"}},"style":{"backgroundColor":"#FFFFFF"},"clickLog":{"screenName":"detail","objectType":"card","objectId":"coupon","extraParameters":{}}}]},
                {"type":"VISIT","header":{"title":{"text":"방문","isHtml":false,"fontColor":"#111111"}},"summary":{"title":{"text":"이번 달","isHtml":false,"fontColor":"#111111"},"stars":{"images":[{"url":"star","style":{"width":16,"height":16}}],"style":{"backgroundColor":"#FFFFFF"}},"rating":{"text":"4.5","isHtml":false,"fontColor":"#111111"},"style":{"backgroundColor":"#FFFFFF"}},"history":{"items":[{"text":{"text":"8월 27일","isHtml":false,"fontColor":"#555555"}}],"moreText":{"text":"외 2회","isHtml":false,"fontColor":"#555555"},"style":{"backgroundColor":"#FFFFFF"}}},
                {"type":"POST","header":{"title":{"text":"소식","isHtml":false,"fontColor":"#111111"}},"cards":[{"cardId":"post-7","header":{"text":{"text":"사장님","isHtml":false,"fontColor":"#111111"}},"images":[{"url":"post","style":{"width":240,"height":160}}],"body":{"text":"오늘 열어요","isHtml":false,"fontColor":"#333333"},"like":{"selected":{"customAction":{"actionType":"STORE_POST_SECTION_CANCEL_LIKE","extraParams":{"POST_ID":7,"STICKER_ID":"like"}},"style":{"backgroundColor":"#FFFFFF"}},"unselected":{"customAction":{"actionType":"STORE_POST_SECTION_ADD_LIKE","extraParams":{"POST_ID":7,"STICKER_ID":"like"}},"style":{"backgroundColor":"#FFFFFF"}},"isSelected":false},"link":{"type":"APP_SCHEME","link":"/post?id=7"},"style":{"backgroundColor":"#FFFFFF"}}]},
                {"type":"REVIEW","header":{"title":{"text":"리뷰","isHtml":false,"fontColor":"#111111"}},"summary":{"title":{"text":"평점","isHtml":false,"fontColor":"#111111"},"stars":{"images":[],"style":{"backgroundColor":"#FFFFFF"}},"rating":{"text":"4.8","isHtml":false,"fontColor":"#111111"},"style":{"backgroundColor":"#FFFFFF"}},"cards":[{"cardId":"review-9","header":{"title":{"text":"사용자","isHtml":false,"fontColor":"#111111"}},"metadata":[{"text":{"text":"오늘","isHtml":false,"fontColor":"#555555"}}],"stars":{"images":[],"style":{"backgroundColor":"#FFFFFF"}},"images":[{"url":"review","style":{"width":100,"height":100}}],"body":{"text":"맛있어요","isHtml":false,"fontColor":"#333333"},"like":{"selected":{"customAction":{"actionType":"STORE_REVIEW_SECTION_CANCEL_LIKE","extraParams":{"REVIEW_ID":9,"STICKER_ID":"like"}},"style":{"backgroundColor":"#FFFFFF"}},"unselected":{"customAction":{"actionType":"STORE_REVIEW_SECTION_ADD_LIKE","extraParams":{"REVIEW_ID":9,"STICKER_ID":"like"}},"style":{"backgroundColor":"#FFFFFF"}},"isSelected":true},"reply":{"header":{"title":{"text":"사장님 답글","isHtml":false,"fontColor":"#111111"}},"body":{"text":"감사합니다","isHtml":false,"fontColor":"#333333"},"style":{"backgroundColor":"#F7F7F7"}},"link":{"type":"APP_SCHEME","link":"/review?id=9"},"style":{"backgroundColor":"#FFFFFF"}}],"more":{"type":"MORE","button":{"text":{"text":"더보기","isHtml":false,"fontColor":"#111111"},"link":{"type":"APP_SCHEME","link":"/reviews"},"style":{"backgroundColor":"#FFFFFF"}}}}
              ],
              "viewLog":{"screenName":"store_detail","objectType":"screen","objectId":"detail","extraParameters":{}}
            }
            """.trimIndent(),
            StoreDetailScreenResponse::class.java,
        )

        val sections = requireNotNull(response.asStoreDetailModelOrNull()).sections
        val coupon = sections[0] as StoreDetailSectionModel.Coupon
        val visit = sections[1] as StoreDetailSectionModel.Visit
        val post = sections[2] as StoreDetailSectionModel.Post
        val review = sections[3] as StoreDetailSectionModel.Review

        assertEquals("coupon-1", coupon.cards.single().cardId)
        assertEquals("STORE_COUPON_SECTION_COUPON_ISSUE", coupon.cards.single().trailingButton.customAction?.actionType)
        assertEquals("4.5", visit.summary.ratingSummary?.rating?.text)
        assertEquals("외 2회", visit.history.moreText?.text)
        assertEquals("post-7", post.cards.single().cardId)
        assertEquals("STORE_POST_SECTION_ADD_LIKE", post.cards.single().like?.unselected?.customAction?.actionType)
        assertEquals("review-9", review.cards.single().cardId)
        assertEquals(true, review.cards.single().like?.isSelected)
        assertEquals("감사합니다", review.cards.single().reply?.body?.text)
        assertEquals("/reviews", review.more?.button?.link?.link)
    }
}
