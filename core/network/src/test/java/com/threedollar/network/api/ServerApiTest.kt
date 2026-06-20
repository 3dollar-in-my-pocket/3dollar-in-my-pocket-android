package com.threedollar.network.api

import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.http.GET

class ServerApiTest {

    @Test
    fun serverApiDoesNotDeclareMissingV2StorePreviewEndpoint() {
        val paths = ServerApi::class.java
            .declaredMethods
            .mapNotNull { it.getAnnotation(GET::class.java)?.value }

        assertFalse(paths.contains("/api/v2/screen/store/{storeId}"))
    }
}
