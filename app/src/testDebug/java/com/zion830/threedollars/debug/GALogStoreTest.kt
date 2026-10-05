package com.zion830.threedollars.debug

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class GALogStoreTest {

    private var isRecording = true
    private val store = GALogStore(isRecordingEnabled = { isRecording })

    private fun entry(timeMillis: Long, name: String = "click", vararg parameters: Pair<String, String>) =
        GALogEntry(timeMillis = timeMillis, name = name, parameters = parameters.sortedBy { it.first })

    @Test
    fun `0_3초_안에_나간_로그는_한_그룹으로_묶고_넘으면_새_그룹을_만든다`() {
        store.append(entry(1_000))
        store.append(entry(1_300))
        store.append(entry(1_301))

        assertEquals(listOf(2, 1), store.groups.map { it.entries.size })
    }

    @Test
    fun `0_7초_안의_직전_탭을_그룹_트리거로_붙이고_한번만_쓴다`() {
        val tap = DebugTapInfo(timeMillis = 1_000, elementDescription = "\"리뷰 작성\" Compose", screenName = "HomeActivity")
        store.recordTap(tap)

        store.append(entry(1_500))
        store.append(entry(3_000))

        assertSame(tap, store.groups[0].tap)
        assertNull(store.groups[1].tap)
    }

    @Test
    fun `탭이_0_7초보다_오래되면_트리거로_붙이지_않는다`() {
        store.recordTap(DebugTapInfo(timeMillis = 1_000, elementDescription = "x", screenName = null))

        store.append(entry(1_701))

        assertNull(store.groups.single().tap)
    }

    @Test
    fun `기록이_꺼져_있으면_탭을_남기지_않는다`() {
        isRecording = false
        store.recordTap(DebugTapInfo(timeMillis = 1_000, elementDescription = "x", screenName = null))
        isRecording = true

        store.append(entry(1_100))

        assertNull(store.groups.single().tap)
    }

    @Test
    fun `같은_이벤트와_대상은_한줄로_합치고_impression은_뺄수있다`() {
        store.append(entry(1_000, "click", "object_id" to "store", "object_type" to "card"))
        store.append(entry(1_010, "click", "object_id" to "store", "object_type" to "card"))
        store.append(entry(1_020, "impression", "object_id" to "admob", "object_type" to "card"))

        val group = store.groups.single()

        assertEquals(listOf("click" to 2, "impression" to 1), group.collapsedLines(includesImpression = true).map { it.first.name to it.second })
        assertEquals(listOf("click" to 2), group.collapsedLines(includesImpression = false).map { it.first.name to it.second })
    }

    @Test
    fun `500건을_넘으면_오래된_그룹부터_버린다`() {
        repeat(GALogStore.MAX_ENTRY_COUNT + 1) { index -> store.append(entry(index * 1_000L)) }

        assertEquals(GALogStore.MAX_ENTRY_COUNT, store.entryCount)
        assertEquals(1_000L, store.groups.first().startTimeMillis)
    }

    @Test
    fun `클리어하면_그룹과_대기중인_탭을_비운다`() {
        store.recordTap(DebugTapInfo(timeMillis = 1_000, elementDescription = "x", screenName = null))
        store.append(entry(5_000))

        store.clear()
        store.append(entry(5_100))

        assertEquals(1, store.groups.size)
        assertNull(store.groups.single().tap)
    }

    @Test
    fun `서버드리븐_로그의_대상과_추가파라미터를_요약한다`() {
        val log = entry(
            1_000,
            "click",
            "object_id" to "admob",
            "object_type" to "card",
            "screen" to "store_detail",
            "store_id" to "120009",
            "store_type" to "BOSS_STORE",
        )

        assertEquals(GALogEntry.Kind.CLICK, log.kind)
        assertEquals("card/admob", log.target)
        assertEquals(listOf("store_id" to "120009", "store_type" to "BOSS_STORE"), log.extraParameters)
        assertEquals("screen: store_detail · store_id: 120009, store_type: BOSS_STORE", log.detailText(isExpanded = false))
        assertEquals(GALogEntry.Kind.PAGE_VIEW, entry(1_000, GALogEntry.PAGE_VIEW_EVENT_NAME).kind)
    }
}
