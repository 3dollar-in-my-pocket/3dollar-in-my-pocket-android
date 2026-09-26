package com.zion830.threedollars.ui.storeDetail.sdui.model

import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.network.result.ApiException

/**
 * 공통 에러 알럿에 보여줄 문구. 서버가 내려준 메시지만 쓰고, 네트워크·파싱 예외처럼 서버 메시지가 없는 실패는
 * null 을 돌려 화면이 기본 문구를 쓰게 한다 (예외 원문이 사용자에게 노출되지 않도록).
 */
object StoreDetailErrorMessage {
    fun from(throwable: Throwable?): String? = when (throwable) {
        is ApiException -> throwable.message
        is StoreNotExistsException -> throwable.message
        else -> null
    }?.takeIf { it.isNotBlank() }
}
