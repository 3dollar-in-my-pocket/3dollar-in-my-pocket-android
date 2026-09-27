package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.domain.store.model.StoreNotExistsException
import com.threedollar.network.result.ApiError
import com.threedollar.network.result.ApiException
import com.threedollar.network.result.WeakNetworkException
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailErrorMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class StoreDetailErrorMessageTest {

    // TH-1226 TC22
    @Test
    fun `TH1226_TC22_서버에러는_서버메시지를_알럿에_보여준다`() {
        // Given
        val serverError = ApiException(error = ApiError.SERVER_INTERNAL, message = "일시적인 문제가 발생했어요")

        // When
        val message = StoreDetailErrorMessage.from(serverError)

        // Then
        assertEquals("일시적인 문제가 발생했어요", message)
    }

    // TH-1226 TC22
    @Test
    fun `TH1226_TC22_네트워크예외는_원문대신_기본문구를_쓰도록_null이다`() {
        // Given
        val networkError = WeakNetworkException(IOException("javax.net.ssl.SSLHandshakeException: Trust anchor not found"))

        // When
        val message = StoreDetailErrorMessage.from(networkError)

        // Then
        assertNull(message)
        assertNull(StoreDetailErrorMessage.from(IllegalStateException("boom")))
    }

    // TH-1226 TC15
    @Test
    fun `TH1226_TC15_삭제된가게는_서버메시지를_쓴다`() {
        assertEquals("삭제된 가게입니다", StoreDetailErrorMessage.from(StoreNotExistsException("삭제된 가게입니다")))
    }
}
