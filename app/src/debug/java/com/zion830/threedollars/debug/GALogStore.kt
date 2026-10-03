package com.zion830.threedollars.debug

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** 앱이 보낸 GA 이벤트 1건. 파라미터는 key 순으로 정렬해 둔다. */
data class GALogEntry(
    val timeMillis: Long,
    val name: String,
    val parameters: List<Pair<String, String>>,
) {
    enum class Kind { PAGE_VIEW, CLICK, IMPRESSION, OTHER }

    val kind: Kind
        get() = when (name) {
            PAGE_VIEW_EVENT_NAME -> Kind.PAGE_VIEW
            "click" -> Kind.CLICK
            "impression" -> Kind.IMPRESSION
            else -> Kind.OTHER
        }

    val screen: String? get() = valueOf("screen") ?: valueOf("screen_name")
    val objectType: String? get() = valueOf("object_type")
    val objectId: String? get() = valueOf("object_id")

    val target: String?
        get() = when {
            objectType != null && objectId != null -> "$objectType/$objectId"
            else -> objectType ?: objectId
        }

    val extraParameters: List<Pair<String, String>>
        get() = parameters.filterNot { it.first in SUMMARY_KEYS }

    val collapseKey: String
        get() = listOf(name, objectType.orEmpty(), objectId.orEmpty(), if (kind == Kind.PAGE_VIEW) screen.orEmpty() else "")
            .joinToString("|")

    private fun valueOf(key: String): String? = parameters.firstOrNull { it.first == key }?.second

    companion object {
        const val PAGE_VIEW_EVENT_NAME = "screen_view"
        private val SUMMARY_KEYS = setOf("screen", "screen_name", "screen_class", "object_type", "object_id")
    }
}

/** 로그 직전에 사용자가 탭한 요소. 화면 캡처는 비동기로 채워진다. */
class DebugTapInfo(
    val timeMillis: Long,
    val elementDescription: String,
    val screenName: String?,
) {
    @Volatile
    var snapshot: Bitmap? = null
}

/** [GALogStore.GROUPING_INTERVAL_MILLIS] 안에 연달아 나간 로그 묶음. 첫 로그 직전의 탭을 트리거로 붙인다. */
class GALogGroup(
    val id: Long,
    val startTimeMillis: Long,
    val tap: DebugTapInfo?,
    firstEntry: GALogEntry,
) {
    private val _entries = mutableListOf(firstEntry)
    val entries: List<GALogEntry> get() = _entries

    internal fun add(entry: GALogEntry) {
        _entries.add(entry)
    }

    /** 같은 이벤트·대상을 한 줄로 합친다. 알림 카드에서 impression 을 뺄 수 있다. */
    fun collapsedLines(includesImpression: Boolean): List<Pair<GALogEntry, Int>> {
        val lines = mutableListOf<Pair<GALogEntry, Int>>()
        entries.filter { includesImpression || it.kind != GALogEntry.Kind.IMPRESSION }.forEach { entry ->
            val index = lines.indexOfFirst { it.first.collapseKey == entry.collapseKey }
            if (index >= 0) lines[index] = lines[index].first to lines[index].second + 1 else lines.add(entry to 1)
        }
        return lines
    }
}

/**
 * 개발 빌드에서 앱이 보낸 GA 로그를 메모리에 쌓는다 (앱 종료 시 사라짐). iOS `GALogStore` 와 같은 규칙.
 * [append]·[recordTap]·[clear]는 메인 스레드에서 부른다. 다른 스레드에서는 [record]를 쓴다.
 */
class GALogStore(
    private val isRecordingEnabled: () -> Boolean,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private val _groups = mutableListOf<GALogGroup>()
    private var lastTap: DebugTapInfo? = null
    private var nextGroupId = 0L

    private val _updatedGroup = MutableSharedFlow<GALogGroup>(extraBufferCapacity = 64)
    val updatedGroup: SharedFlow<GALogGroup> = _updatedGroup.asSharedFlow()

    private val _version = MutableStateFlow(0)
    /** 목록이 바뀔 때마다(추가·클리어) 올라간다. 뷰어가 다시 그리는 신호. */
    val version: StateFlow<Int> = _version.asStateFlow()

    val groups: List<GALogGroup> get() = _groups
    val entryCount: Int get() = _groups.sumOf { it.entries.size }

    fun record(eventName: String, parameters: Bundle) {
        if (!isRecordingEnabled()) return
        val entry = GALogEntry(timeMillis = clock(), name = eventName, parameters = parameters.toSortedPairs())
        if (Looper.myLooper() == Looper.getMainLooper()) append(entry) else mainHandler.post { append(entry) }
    }

    fun recordTap(tap: DebugTapInfo) {
        if (!isRecordingEnabled()) return
        lastTap = tap
    }

    fun append(entry: GALogEntry) {
        val lastGroup = _groups.lastOrNull()
        val group = if (lastGroup != null && entry.timeMillis - lastGroup.startTimeMillis <= GROUPING_INTERVAL_MILLIS) {
            lastGroup.also { it.add(entry) }
        } else {
            GALogGroup(id = nextGroupId++, startTimeMillis = entry.timeMillis, tap = consumeTap(entry.timeMillis), firstEntry = entry)
                .also { _groups.add(it) }
        }
        trimIfNeeded()
        _version.value += 1
        _updatedGroup.tryEmit(group)
    }

    fun clear() {
        _groups.clear()
        lastTap = null
        _version.value += 1
    }

    fun group(id: Long): GALogGroup? = _groups.firstOrNull { it.id == id }

    private fun consumeTap(beforeMillis: Long): DebugTapInfo? {
        val tap = lastTap ?: return null
        val interval = beforeMillis - tap.timeMillis
        if (interval !in 0..TAP_MATCH_INTERVAL_MILLIS) return null
        lastTap = null
        return tap
    }

    private fun trimIfNeeded() {
        while (entryCount > MAX_ENTRY_COUNT && _groups.size > 1) {
            _groups.removeAt(0)
        }
        // 화면 캡처는 장당 1MB 남짓이라 최근 묶음만 남긴다.
        _groups.dropLast(MAX_SNAPSHOT_GROUP_COUNT).forEach { it.tap?.snapshot = null }
    }

    companion object {
        const val GROUPING_INTERVAL_MILLIS = 300L
        const val TAP_MATCH_INTERVAL_MILLIS = 700L
        const val MAX_ENTRY_COUNT = 500
        const val MAX_SNAPSHOT_GROUP_COUNT = 30
    }
}

@Suppress("DEPRECATION")
private fun Bundle.toSortedPairs(): List<Pair<String, String>> =
    keySet().sorted().map { key -> key to get(key).toString() }
