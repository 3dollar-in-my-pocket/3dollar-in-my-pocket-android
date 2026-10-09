package com.zion830.threedollars.debug

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray30
import base.compose.Gray50
import base.compose.Gray70
import base.compose.PretendardFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 개발 빌드 GA 로그 뷰어. 0.3초 묶음(그룹)마다 직전 탭한 요소·화면·캡처와 함께 로그를 보여준다. iOS `GALogViewerViewController` 와 같은 구성.
 */
class GALogViewerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val focusGroupId = intent.getLongExtra(EXTRA_FOCUS_GROUP_ID, NO_FOCUS).takeIf { it != NO_FOCUS }
        setContent {
            GALogViewerScreen(
                focusGroupId = focusGroupId,
                onClose = ::finish,
                onClear = { DebugTools.gaLogStore.clear() },
            )
        }
    }

    companion object {
        private const val EXTRA_FOCUS_GROUP_ID = "extra_focus_group_id"
        private const val NO_FOCUS = -1L

        fun start(context: Context, focusGroupId: Long?) {
            context.startActivity(
                Intent(context, GALogViewerActivity::class.java)
                    .putExtra(EXTRA_FOCUS_GROUP_ID, focusGroupId ?: NO_FOCUS),
            )
        }
    }
}

private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREA)

@Composable
private fun GALogViewerScreen(focusGroupId: Long?, onClose: () -> Unit, onClear: () -> Unit) {
    val store = DebugTools.gaLogStore
    val version by store.version.collectAsState()
    val groups = remember(version) { store.groups.reversed() }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }
    var snapshot by remember { mutableStateOf<Bitmap?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(focusGroupId) {
        val index = groups.indexOfFirst { it.id == focusGroupId }
        if (index >= 0) listState.scrollToItem(groups.take(index).sumOf { 1 + it.entries.size })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray10)
            .safeDrawingPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorWhite)
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ToolbarButton("닫기", onClose)
            Text(
                text = "GA 로그 (${store.entryCount})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            ToolbarButton("클리어", onClear)
        }

        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (DebugTools.preferences.isGALogRecordingEnabled) {
                        "아직 전송된 GA 로그가 없어요"
                    } else {
                        "GA 로그 기록이 꺼져 있어요\n디버깅 메뉴에서 켜주세요"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = PretendardFontFamily,
                    color = Gray50,
                    textAlign = TextAlign.Center,
                )
            }
            return@Column
        }

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            groups.forEach { group ->
                item(key = "group-${group.id}") {
                    GALogGroupHeader(group = group, onTapSnapshot = { snapshot = it })
                }
                group.entries.forEachIndexed { index, entry ->
                    val rowKey = "entry-${group.id}-$index"
                    item(key = rowKey) {
                        GALogEntryRow(
                            entry = entry,
                            elapsedMillis = entry.timeMillis - group.startTimeMillis,
                            isExpanded = expanded[rowKey] == true,
                            onClick = { expanded[rowKey] = expanded[rowKey] != true },
                        )
                    }
                }
            }
        }
    }

    snapshot?.let { image ->
        Dialog(onDismissRequest = { snapshot = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { snapshot = null },
            ) {
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun ToolbarButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontFamily = PretendardFontFamily,
        color = Gray100,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun GALogGroupHeader(group: GALogGroup, onTapSnapshot: (Bitmap) -> Unit) {
    val snapshot = group.tap?.snapshot
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray10)
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${timeFormat.format(Date(group.startTimeMillis))} · ${group.entries.size}건",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PretendardFontFamily,
                color = Gray100,
            )
            Text(
                text = group.tap?.let { tap -> "👆 ${tap.elementDescription}" + (tap.screenName?.let { " · $it" } ?: "") }
                    ?: "트리거 미확인 (노출·화면 진입 또는 비동기 로그)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PretendardFontFamily,
                color = Gray70,
                maxLines = 2,
            )
        }
        if (snapshot != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                bitmap = snapshot.asImageBitmap(),
                contentDescription = "탭 위치 캡처",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 40.dp, height = 86.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Gray30, RoundedCornerShape(4.dp))
                    .clickable { onTapSnapshot(snapshot) },
            )
        }
    }
}

@Composable
private fun GALogEntryRow(entry: GALogEntry, elapsedMillis: Long, isExpanded: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(entry.kind.color),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = listOfNotNull(entry.name, entry.target).joinToString("  "),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "+%.2fs".format(elapsedMillis / 1000f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PretendardFontFamily,
                color = Gray50,
            )
        }
        Text(
            text = entry.detailText(isExpanded),
            fontSize = 12.sp,
            fontFamily = PretendardFontFamily,
            color = Gray70,
            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
            modifier = Modifier.padding(start = 32.dp, end = 16.dp, top = 4.dp, bottom = 10.dp),
        )
        HorizontalDivider(color = Gray10, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

/** 접힌 상태는 화면·추가 파라미터 요약, 펼치면 모든 파라미터를 한 줄씩. */
internal fun GALogEntry.detailText(isExpanded: Boolean): String {
    if (isExpanded) return parameters.joinToString("\n") { (key, value) -> "$key: $value" }
    val screenText = screen?.let { "screen: $it" }
    val parameterText = extraParameters.joinToString(", ") { (key, value) -> "$key: $value" }.ifEmpty { null }
    return listOfNotNull(screenText, parameterText).joinToString(" · ")
}
