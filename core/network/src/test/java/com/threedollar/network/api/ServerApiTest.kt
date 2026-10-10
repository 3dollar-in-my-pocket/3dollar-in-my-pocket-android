package com.threedollar.network.api

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

class ServerApiTest {

    @Test
    fun serverApiDoesNotDeclareMissingV2StorePreviewEndpoint() {
        val paths = ServerApi::class.java
            .declaredMethods
            .mapNotNull { it.getAnnotation(GET::class.java)?.value }

        assertFalse(paths.contains("/api/v2/screen/store/{storeId}"))
    }

    // TH-1401 TC4
    @Test
    fun `TH1401_TC4_큐레이션섹션조회는_탭경로와_지도쿼리_선택기기헤더를_사용한다`() {
        // Given
        val method = ServerApi::class.java.declaredMethods.firstOrNull { it.name == "getHomeCurationSection" }

        // When
        val path = method?.getAnnotation(GET::class.java)?.value

        // Then
        assertNotNull("큐레이션 섹션 API가 있어야 한다", method)
        assertEquals("/api/v1/screen/home/section/curation/{tabId}", path)
        assertEquals("tabId", method!!.parameterAnnotations[0].filterIsInstance<Path>().single().value)
        assertEquals(listOf("mapLatitude", "mapLongitude"), method.parameterAnnotations.flatMap { it.filterIsInstance<Query>() }.map { it.value })
        assertEquals(listOf("X-Device-Latitude", "X-Device-Longitude"), method.parameterAnnotations.flatMap { it.filterIsInstance<Header>() }.map { it.value })
        assertEquals(java.lang.Double::class.java, method.parameterTypes[3])
        assertEquals(java.lang.Double::class.java, method.parameterTypes[4])
    }

    // TH-1401 TC5
    @Test
    fun `TH1401_TC5_칩조회는_단수carousel경로와_categoryId쿼리를_사용한다`() {
        // Given
        val method = ServerApi::class.java.declaredMethods.firstOrNull { it.name == "getHomeCurationCards" }

        // When
        val path = method?.getAnnotation(GET::class.java)?.value

        // Then
        assertNotNull("캐러셀 카드 API가 있어야 한다", method)
        assertEquals("/api/v1/screen/home/section/curation/{tabId}/carousel/{carouselId}/cards", path)
        assertEquals(listOf("tabId", "carouselId"), method!!.parameterAnnotations.flatMap { it.filterIsInstance<Path>() }.map { it.value })
        assertEquals(listOf("categoryId", "mapLatitude", "mapLongitude"), method.parameterAnnotations.flatMap { it.filterIsInstance<Query>() }.map { it.value })
        assertEquals(listOf("X-Device-Latitude", "X-Device-Longitude"), method.parameterAnnotations.flatMap { it.filterIsInstance<Header>() }.map { it.value })
        assertEquals(java.lang.Double::class.java, method.parameterTypes[5])
        assertEquals(java.lang.Double::class.java, method.parameterTypes[6])
    }
}
