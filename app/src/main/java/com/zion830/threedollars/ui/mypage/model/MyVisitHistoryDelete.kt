package com.zion830.threedollars.ui.mypage.model

import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse

/** 방문 인증 삭제 결과. [message] 가 null 이면 화면이 기본 문구(실패는 공통 에러 안내)를 쓴다. */
internal sealed interface MyVisitDeleteResult {
    val message: String?

    data class Deleted(override val message: String?) : MyVisitDeleteResult

    data class Failed(override val message: String?) : MyVisitDeleteResult

    companion object {
        fun from(response: BaseResponse<String>): MyVisitDeleteResult = if (response.ok) {
            Deleted(message = response.data.nonBlankOrNull() ?: response.message.nonBlankOrNull())
        } else {
            Failed(message = response.message.nonBlankOrNull())
        }

        fun fromError(): MyVisitDeleteResult = Failed(message = null)

        private fun String?.nonBlankOrNull(): String? = this?.takeIf { it.isNotBlank() }
    }
}

internal fun createVisitHistoryEditClickEvent(): ClickEvent = ClickEvent(
    screen = ScreenName.VISITED_LIST,
    objectType = LogObjectType.BUTTON,
    objectId = LogObjectId.EDIT,
)

internal fun createDeleteVisitClickEvent(storeId: String, storeType: String, visitId: String): ClickEvent = ClickEvent(
    screen = ScreenName.VISITED_LIST,
    objectType = LogObjectType.BUTTON,
    objectId = LogObjectId.DELETE_VISIT,
    additionalParams = mapOf(
        ParameterName.STORE_ID to storeId,
        ParameterName.STORE_TYPE to storeType,
        ParameterName.VISIT_ID to visitId,
    ),
)
