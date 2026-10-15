package com.zion830.threedollars.ui.storeDetail.sdui

import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDStoreThreadPostSectionModel
import com.threedollar.domain.store.repository.ThreadsRepository
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.STORE_ID
import com.zion830.threedollars.ui.storeDetail.sdui.StoreDetailSduiFixtures.sections
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailActionResolver.Resolution
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailDestination
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreThreadPostEmbedLoader
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class StoreThreadPostEmbedLoaderTest {

    private val firstPermalink = "https://www.threads.com/@gyeol_jeong84/post/DOaaa"
    private val secondPermalink = "https://www.threads.com/@other_user/post/DObbb"
    private val clickLog = SDLogModel(screenName = "store_detail", objectType = "card", objectId = "threads_post")

    private val section = SDStoreThreadPostSectionModel(
        sectionId = "THREADS_POST",
        header = null,
        cards = listOf(
            SDStoreThreadPostSectionModel.Card(firstPermalink, clickLog),
            SDStoreThreadPostSectionModel.Card(secondPermalink, clickLog),
        ),
        impressionLog = null,
        style = null,
    )

    private class FakeThreadsRepository(private val htmlsByUrl: Map<String, String>) : ThreadsRepository {
        val requestedUrls = mutableListOf<String>()

        override suspend fun getOEmbedHtml(url: String): Result<String> {
            requestedUrls += url
            return htmlsByUrl[url]?.let { Result.success(it) } ?: Result.failure(IOException("not found"))
        }
    }

    private fun resolve(repository: FakeThreadsRepository): SDStoreThreadPostSectionModel = runBlocking {
        val loader = StoreThreadPostEmbedLoader(repository)
        val loadingSections = loader.applyEmbeds(listOf(section))
        loader.fetchPendingEmbeds(loadingSections)
        loader.applyEmbeds(loadingSections).single() as SDStoreThreadPostSectionModel
    }

    // TH-1449 TC8
    @Test
    fun `TH1449_TC8_일부_permalink의_oEmbed가_실패하면_실패한_카드만_빠지고_나머지는_노출된다`() {
        // Given
        val repository = FakeThreadsRepository(mapOf(firstPermalink to "<blockquote>ok</blockquote>"))

        // When
        val resolved = resolve(repository)

        // Then
        assertEquals(listOf(firstPermalink), resolved.embeds?.map { it.permalink })
        assertEquals("<blockquote>ok</blockquote>", resolved.embeds?.single()?.html)
        assertEquals(setOf(firstPermalink, secondPermalink), repository.requestedUrls.toSet())
    }

    // TH-1449 TC9
    @Test
    fun `TH1449_TC9_모든_permalink의_oEmbed가_실패하면_노출할_카드가_없어_섹션이_숨겨진다`() {
        // Given
        val repository = FakeThreadsRepository(emptyMap())

        // When
        val resolved = resolve(repository)

        // Then
        assertTrue(resolved.embeds.orEmpty().isEmpty())
        assertTrue(resolved.embeds != null)
    }

    @Test
    fun `oEmbed를_받기_전에는_embeds가_null이라_로딩상태로_그린다`() {
        // Given
        val loader = StoreThreadPostEmbedLoader(FakeThreadsRepository(emptyMap()))

        // When
        val loading = loader.applyEmbeds(listOf(section)).single() as SDStoreThreadPostSectionModel

        // Then
        assertNull(loading.embeds)
    }

    // TH-1449 TC13
    @Test
    fun `TH1449_TC13_카드내부_외부링크_탭은_카드의_clickLog를_실은_웹링크로_외부에서_연다`() {
        // Given
        val event = SDActionEvent(link = SDLink(type = SDLinkType.WEB, link = firstPermalink), clickLog = clickLog)
        val context = StoreDetailActionResolver.Context(
            storeId = STORE_ID,
            isBossStore = false,
            storeName = "호떡집",
            sections = sections,
        )

        // When
        val resolution = StoreDetailActionResolver.resolve(event, context)

        // Then
        assertEquals(Resolution.Navigate(StoreDetailDestination.OpenLink(event.link!!)), resolution)
        assertEquals(clickLog, event.clickLog)
    }
}
