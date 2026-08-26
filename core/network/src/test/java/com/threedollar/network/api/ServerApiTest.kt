package com.threedollar.network.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

class ServerApiTest {

    @Test
    fun serverApiDeclaresV2StoreDetailEndpointWithLongIdAndOptionalLocationHeaders() {
        val method = ServerApi::class.java
            .declaredMethods
            .singleOrNull { it.getAnnotation(GET::class.java)?.value == "/api/v2/screen/store/{storeId}" }

        assertNotNull(method)
        method!!
        assertEquals(Long::class.javaPrimitiveType, method.parameterTypes[0])
        assertEquals("storeId", method.parameterAnnotations[0].filterIsInstance<Path>().single().value)
        assertEquals("Experiment-Context", method.parameterAnnotations[1].filterIsInstance<Header>().single().value)
        assertEquals("X-Device-Latitude", method.parameterAnnotations[2].filterIsInstance<Header>().single().value)
        assertEquals("X-Device-Longitude", method.parameterAnnotations[3].filterIsInstance<Header>().single().value)
        assertEquals(String::class.java, method.parameterTypes[1])
        assertEquals(Double::class.javaObjectType, method.parameterTypes[2])
        assertEquals(Double::class.javaObjectType, method.parameterTypes[3])
    }

    @Test
    fun serverApiDeclaresStoreDetailMutationEndpoints() {
        val methods = ServerApi::class.java.declaredMethods

        assertEquals(
            "/api/v1/store/{storeId}/news-post/{postId}/stickers",
            methods.singleOrNull { it.name == "putStorePostStickers" }?.getAnnotation(PUT::class.java)?.value,
        )
        assertEquals(
            "/api/v1/store/{storeId}/coupon/{couponId}/issue",
            methods.singleOrNull { it.name == "issueStoreCoupon" }?.getAnnotation(POST::class.java)?.value,
        )
        assertEquals(
            "/api/v1/issued-coupon/{issuedKey}/use",
            methods.singleOrNull { it.name == "useIssuedCoupon" }?.getAnnotation(PUT::class.java)?.value,
        )
        assertEquals(
            "/api/v2/store/review/{reviewId}",
            methods.singleOrNull { it.name == "deleteStoreReview" }?.getAnnotation(DELETE::class.java)?.value,
        )
    }
}
