package com.zion830.threedollars.ui.mypage.model

import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class MyVisitHistoryDeleteTest {

    // TH-1539 TC5
    @Test
    fun `TH1539_TC5_삭제에성공하면_서버응답data문자열을_토스트메시지로쓴다`() {
        // Given
        val response = BaseResponse(ok = true, data = "방문 인증을 삭제했어요", message = null)

        // When
        val result = MyVisitDeleteResult.from(response)

        // Then
        assertEquals(MyVisitDeleteResult.Deleted(message = "방문 인증을 삭제했어요"), result)
    }

    // TH-1539 TC5
    @Test
    fun `TH1539_TC5_성공응답에data가없으면_message를_쓰고_둘다없으면_토스트없이_삭제로처리한다`() {
        // Given
        val withMessage = BaseResponse<String>(ok = true, data = null, message = "삭제했어요")
        val withNothing = BaseResponse<String>(ok = true, data = "", message = "")

        // When
        val results = listOf(withMessage, withNothing).map(MyVisitDeleteResult::from)

        // Then
        assertEquals(
            listOf(MyVisitDeleteResult.Deleted(message = "삭제했어요"), MyVisitDeleteResult.Deleted(message = null)),
            results,
        )
    }

    // TH-1539 TC6
    @Test
    fun `TH1539_TC6_서버가거부하면_서버에러메시지로_실패한다`() {
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
