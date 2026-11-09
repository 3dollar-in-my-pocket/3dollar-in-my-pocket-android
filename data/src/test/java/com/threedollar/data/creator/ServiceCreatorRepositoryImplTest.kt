package com.threedollar.data.creator

import com.threedollar.data.creator.repository.ServiceCreatorRepositoryImpl
import com.threedollar.data.fake.FakeServiceCreatorApi
import com.threedollar.network.result.ApiException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ServiceCreatorRepositoryImplTest {

    private val serviceCreatorApi = FakeServiceCreatorApi()
    private val repository = ServiceCreatorRepositoryImpl(serviceCreatorApi)

    // TH-1546 TC1
    @Test
    fun `TH1546_TC1_팀원목록을_조회하면_서버가_내려준_역할순서와_이름을_그대로_옮긴다`() {
        // Given
        serviceCreatorApi.givenFixture("creator/ServiceCreators.json")

        // When
        val groups = runBlocking { repository.listCreators() }.getOrThrow()

        // Then
        assertEquals(
            listOf("iOS", "Android", "Server", "Marketing", "Product Design", "Brand Design"),
            groups.map { it.role },
        )
        assertEquals(listOf("양민설", "조혜원"), groups.first { it.role == "Product Design" }.creatorNames)
    }

    // TH-1546 TC3
    @Test
    fun `TH1546_TC3_팀원목록_조회가_실패하면_서버_메시지를_담은_실패를_돌려준다`() {
        // Given
        serviceCreatorApi.givenError(
            code = 400,
            body = """{"ok":false,"error":"invalid_parameter","message":"일시적인 문제가 발생하였습니다"}""",
        )

        // When
        val result = runBlocking { repository.listCreators() }

        // Then
        val error = result.exceptionOrNull()
        assertTrue(error is ApiException)
        assertEquals("일시적인 문제가 발생하였습니다", error?.message)
    }
}
