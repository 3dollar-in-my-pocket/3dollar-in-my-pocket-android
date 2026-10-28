package com.zion830.threedollars.ui.mypage.model

import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class MyVisitHistoryDeleteTest {

    private val now: Instant = Instant.parse("2026-10-28T00:00:00Z")

    // TH-1539 TC1
    @Test
    fun `TH1539_TC1_createdAt이_24시간이내면_삭제버튼을_보인다`() {
        // Given
        val createdAts = listOf("2026-10-28T08:59:59", "2026-10-27T09:00:01")

        // When
        val deletable = createdAts.map { MyVisitDeletePolicy.isDeletable(it, now) }

        // Then
        assertEquals(listOf(true, true), deletable)
    }

    // TH-1539 TC6
    @Test
    fun `TH1539_TC6_createdAt이_24시간지났으면_편집모드에서도_삭제버튼이없다`() {
        // Given
        val createdAts = listOf("2026-10-27T09:00:00", "2026-07-20T22:07:01")

        // When
        val deletable = createdAts.map { MyVisitDeletePolicy.isDeletable(it, now) }

        // Then
        assertEquals(listOf(false, false), deletable)
    }

    // TH-1539 TC6
    @Test
    fun `TH1539_TC6_createdAt을_파싱하지못하면_삭제할수없다`() {
        // Given
        val createdAts = listOf(null, "", "2026-10-28 08:00:00", "invalid")

        // When
        val deletable = createdAts.map { MyVisitDeletePolicy.isDeletable(it, now) }

        // Then
        assertEquals(listOf(false, false, false, false), deletable)
    }

    // TH-1539 TC16
    @Test
    fun `TH1539_TC16_24시간이내_인증이_하나도없으면_편집버튼을_숨긴다`() {
        // Given
        val onlyOld = listOf("2026-09-25T09:09:52", "2026-07-20T22:07:01", null)
        val withRecent = onlyOld + "2026-10-27T23:56:00"

        // When
        val hasDeletableOld = MyVisitDeletePolicy.hasDeletable(onlyOld, now)
        val hasDeletableRecent = MyVisitDeletePolicy.hasDeletable(withRecent, now)

        // Then
        assertFalse(hasDeletableOld)
        assertTrue(hasDeletableRecent)
    }

    // TH-1539 TC5
    @Test
    fun `TH1539_TC5_삭제에성공하면_서버응답과무관하게_고정문구용_성공결과다`() {
        // Given
        val responses = listOf(
            BaseResponse(ok = true, data = "OK", message = null),
            BaseResponse<String>(ok = true, data = null, message = "삭제했어요"),
        )

        // When
        val results = responses.map(MyVisitDeleteResult::from)

        // Then
        assertEquals(listOf(MyVisitDeleteResult.Deleted, MyVisitDeleteResult.Deleted), results)
    }

    @Test
    fun `회귀_서버가거부하면_서버에러메시지로_실패한다`() {
        // Given
        val response = BaseResponse<String>(ok = false, data = null, message = "하루가 지난 방문 인증은 삭제할 수 없어요", error = "bad_request")

        // When
        val result = MyVisitDeleteResult.from(response)

        // Then
        assertEquals(MyVisitDeleteResult.Failed(message = "하루가 지난 방문 인증은 삭제할 수 없어요"), result)
    }

    // TH-1539 TC9
    @Test
    fun `TH1539_TC9_네트워크오류나_메시지없는실패면_공통에러안내용으로_메시지가없다`() {
        // Given
        val emptyFailure = BaseResponse<String>(ok = false, data = null, message = " ")

        // When
        val results = listOf(MyVisitDeleteResult.from(emptyFailure), MyVisitDeleteResult.fromError())

        // Then
        assertEquals(listOf(MyVisitDeleteResult.Failed(null), MyVisitDeleteResult.Failed(null)), results)
    }

    // TH-1539 TC13
    @Test
    fun `TH1539_TC13_편집버튼_클릭로그는_visited_list_button_edit이다`() {
        // Given
        val screen = ScreenName.VISITED_LIST

        // When
        val event = createVisitHistoryEditClickEvent()

        // Then
        assertEquals(screen, event.screen)
        assertEquals(LogObjectType.BUTTON.value, event.extraParameters[ParameterName.OBJECT_TYPE])
        assertEquals(LogObjectId.EDIT.value, event.extraParameters[ParameterName.OBJECT_ID])
    }

    // TH-1539 TC14
    @Test
    fun `TH1539_TC14_삭제확정_클릭로그에_store_id_store_type_visit_id를_담는다`() {
        // Given
        val storeId = "12806182"
        val storeType = "USER_STORE"
        val visitId = "4567"

        // When
        val event = createDeleteVisitClickEvent(storeId = storeId, storeType = storeType, visitId = visitId)

        // Then
        assertEquals(ScreenName.VISITED_LIST, event.screen)
        assertEquals(LogObjectType.BUTTON.value, event.extraParameters[ParameterName.OBJECT_TYPE])
        assertEquals("delete_visit", event.extraParameters[ParameterName.OBJECT_ID])
        assertEquals(storeId, event.extraParameters[ParameterName.STORE_ID])
        assertEquals(storeType, event.extraParameters[ParameterName.STORE_TYPE])
        assertEquals(visitId, event.extraParameters[ParameterName.VISIT_ID])
    }
}
