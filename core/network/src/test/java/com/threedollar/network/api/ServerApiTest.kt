package com.threedollar.network.api

import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.http.GET

class ServerApiTest {

    @Test
    fun getStoreScreenUsesV2PreviewStoreEndpoint() {
        val getAnnotation = requireNotNull(
            ServerApi::class.java
                .declaredMethods
                .first { it.name == "getStoreScreen" }
                .getAnnotation(GET::class.java),
        )

        assertEquals("/api/v2/screen/store/{storeId}", getAnnotation.value)
    }
}
