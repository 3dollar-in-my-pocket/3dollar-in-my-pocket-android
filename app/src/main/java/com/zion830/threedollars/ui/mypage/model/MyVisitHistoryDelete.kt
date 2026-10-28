package com.zion830.threedollars.ui.mypage.model

import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseResponse
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 방문 인증 삭제 가능 여부 (iOS 와 동일 기준).
 * 서버 `visit.createdAt`(KST 로컬 시각)으로부터 24시간이 지나기 전까지만 삭제할 수 있고, 파싱에 실패하면 삭제할 수 없다.
 */
internal object MyVisitDeletePolicy {
    private val SERVER_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val SERVER_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    private val DELETABLE_DURATION: Duration = Duration.ofHours(24)

    fun isDeletable(createdAt: String?, now: Instant = Instant.now()): Boolean {
        val createdAtInstant = try {
            LocalDateTime.parse(createdAt ?: return false, SERVER_FORMAT).atZone(SERVER_ZONE).toInstant()
        } catch (e: DateTimeParseException) {
            return false
        }
        return Duration.between(createdAtInstant, now) < DELETABLE_DURATION
    }

    fun hasDeletable(createdAts: List<String?>, now: Instant = Instant.now()): Boolean =
        createdAts.any { isDeletable(it, now) }
}

/** 방문 인증 삭제 결과. 성공은 고정 문구를 띄우고, 실패 [Failed.message] 가 null 이면 공통 에러 안내를 쓴다. */
internal sealed interface MyVisitDeleteResult {
    data object Deleted : MyVisitDeleteResult

    data class Failed(val message: String?) : MyVisitDeleteResult

    companion object {
        fun from(response: BaseResponse<String>): MyVisitDeleteResult = if (response.ok) {
            Deleted
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
