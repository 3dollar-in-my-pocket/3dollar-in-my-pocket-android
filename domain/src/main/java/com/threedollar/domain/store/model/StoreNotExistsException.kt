package com.threedollar.domain.store.model

/**
 * 삭제되었거나 존재하지 않는 가게를 조회했을 때의 실패. [message]는 서버가 내려준 안내 문구다.
 */
class StoreNotExistsException(
    override val message: String?
) : Exception(message)
