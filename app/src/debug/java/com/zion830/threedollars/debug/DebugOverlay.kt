package com.zion830.threedollars.debug

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import base.compose.ColorWhite
import base.compose.Gray70
import base.compose.Green
import base.compose.Gray100
import base.compose.Gray40
import base.compose.Gray50
import base.compose.Pink
import base.compose.PretendardFontFamily
import com.chuckerteam.chucker.api.Chucker
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.zion830.threedollars.ui.storeDetail.sdui.ui.StoreDetailSduiActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * 화면마다 decorView 맨 위에 투명한 Compose 레이어를 얹어 DEV 버튼·디버깅 메뉴·GA 로그 알림·가게 ID 를 그린다.
 * 그려진 요소 밖의 터치는 아래 화면으로 그대로 내려간다. iOS `DebugOverlay` 와 같은 역할.
 */
object DebugOverlay {
    const val OVERLAY_TAG = "debug_overlay"

    fun attach(activity: ComponentActivity) {
        val decorView = activity.window?.decorView as? ViewGroup ?: return
        if (decorView.findViewWithTag<ComposeView>(OVERLAY_TAG) != null) return
        // 창에 붙기 전·붙는 도중의 decorView 에 ComposeView 를 넣으면 첫 measure 에서 Recomposer 를 못 찾아 죽는다.
        if (!decorView.isAttachedToWindow) {
            decorView.doOnAttach { it.post { attach(activity) } }
            return
        }
        if (decorView.findViewTreeLifecycleOwner() == null) return
        val overlay = ComposeView(activity).apply {
            tag = OVERLAY_TAG
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent { DebugOverlayContent(activity) }
        }
        decorView.addView(overlay, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}

@Composable
private fun DebugOverlayContent(activity: ComponentActivity) {
    val preferences = DebugTools.preferences
    var isMenuOpen by remember { mutableStateOf(false) }
    var isShowStoreId by remember { mutableStateOf(preferences.isShowStoreId) }
    val storeId = remember(activity) {
        (activity as? StoreDetailSduiActivity)?.intent?.getStringExtra(StoreDetailSduiActivity.EXTRA_STORE_ID)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        GALogToastHost(
            onTapGroup = { groupId -> GALogViewerActivity.start(activity, focusGroupId = groupId) },
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        )

        if (isShowStoreId && !storeId.isNullOrBlank()) {
            StoreIdPill(
                storeId = storeId,
                onCopy = { copyToClipboard(activity, label = "store_id", text = storeId, toast = "가게 ID $storeId 복사됨") },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, bottom = STORE_ID_BOTTOM_OFFSET),
            )
        }

        if (isMenuOpen) {
            DebugMenuPanel(
                activity = activity,
                maxPanelHeight = maxHeight * 0.75f,
                onClose = { isMenuOpen = false },
                onStoreIdToggle = { isShowStoreId = it },
            )
        }

        DebugFloatingButton(
            containerWidthPx = constraints.maxWidth.toFloat(),
            containerHeightPx = constraints.maxHeight.toFloat(),
            onClick = { isMenuOpen = !isMenuOpen },
        )
    }
}

private val STORE_ID_BOTTOM_OFFSET = 88.dp
private val FLOATING_BUTTON_SIZE = 48.dp

@Composable
private fun DebugFloatingButton(
    containerWidthPx: Float,
    containerHeightPx: Float,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current
    val preferences = DebugTools.preferences
    val sizePx = with(density) { FLOATING_BUTTON_SIZE.toPx() }
    val edgePx = with(density) { 8.dp.toPx() }
    val topInset = WindowInsets.safeDrawing.getTop(density).toFloat()
    val bottomInset = WindowInsets.safeDrawing.getBottom(density).toFloat()

    fun clamp(offset: Offset): Offset = Offset(
        x = offset.x.coerceIn(edgePx, (containerWidthPx - sizePx - edgePx).coerceAtLeast(edgePx)),
        y = offset.y.coerceIn(topInset + edgePx, (containerHeightPx - bottomInset - sizePx - edgePx).coerceAtLeast(topInset + edgePx)),
    )

    var offset by remember(containerWidthPx, containerHeightPx) {
        val saved = preferences.floatingButtonPosition
        val initial = if (saved != null) {
            Offset(saved.first * containerWidthPx, saved.second * containerHeightPx)
        } else {
            Offset(
                containerWidthPx - sizePx - with(density) { 16.dp.toPx() },
                containerHeightPx - bottomInset - sizePx - with(density) { 120.dp.toPx() },
            )
        }
        mutableStateOf(clamp(initial))
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .size(FLOATING_BUTTON_SIZE)
            .clip(CircleShape)
            .background(Gray100.copy(alpha = 0.85f))
            .border(2.dp, Pink, CircleShape)
            .pointerInput(containerWidthPx, containerHeightPx) {
                detectDragGestures(
                    onDragEnd = {
                        preferences.floatingButtonPosition = offset.x / containerWidthPx to offset.y / containerHeightPx
                    },
                ) { change, dragAmount ->
                    change.consume()
                    offset = clamp(offset + dragAmount)
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "DEV", color = ColorWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PretendardFontFamily)
    }
}

@Composable
private fun StoreIdPill(storeId: String, onCopy: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Gray100.copy(alpha = 0.85f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "가게 ID $storeId", color = ColorWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = PretendardFontFamily)
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Pink)
                .clickable(onClick = onCopy)
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(text = "복사", color = ColorWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = PretendardFontFamily)
        }
    }
}

@Composable
private fun DebugMenuPanel(
    activity: ComponentActivity,
    maxPanelHeight: Dp,
    onClose: () -> Unit,
    onStoreIdToggle: (Boolean) -> Unit,
) {
    val preferences = DebugTools.preferences
    var isShowStoreId by remember { mutableStateOf(preferences.isShowStoreId) }
    var isRecording by remember { mutableStateOf(preferences.isGALogRecordingEnabled) }
    var isToast by remember { mutableStateOf(preferences.isGALogToastEnabled) }
    var isImpressionToast by remember { mutableStateOf(preferences.isGALogImpressionToastEnabled) }

    BackHandler(onBack = onClose)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = maxPanelHeight)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(ColorWhite)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "디버깅 메뉴",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PretendardFontFamily,
                color = Gray100,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            DebugToggleRow(
                title = "가게 상세 진입 시 ID 노출",
                description = "가게 상세에 가게 ID 플로팅 뷰를 띄웁니다",
                isOn = isShowStoreId,
            ) {
                isShowStoreId = it
                preferences.isShowStoreId = it
                onStoreIdToggle(it)
            }
            DebugToggleRow(
                title = "GA 로그 기록",
                description = "앱이 보내는 GA 로그와 직전 탭(요소·화면 캡처)을 기록합니다. 끄면 기록·알림이 모두 멈춥니다",
                isOn = isRecording,
            ) {
                isRecording = it
                preferences.isGALogRecordingEnabled = it
                if (!it) DebugTools.gaLogStore.clear()
            }
            DebugToggleRow(
                title = "GA 로그 실시간 알림",
                description = "로그가 나갈 때 화면 상단에 0.3초 단위로 묶어 보여줍니다",
                isOn = isToast,
                isEnabled = isRecording,
            ) {
                isToast = it
                preferences.isGALogToastEnabled = it
            }
            DebugToggleRow(
                title = "GA 로그 알림에 impression 포함",
                description = "스크롤 중 노출 로그까지 알림에 띄웁니다 (뷰어에는 항상 기록)",
                isOn = isImpressionToast,
                isEnabled = isRecording && isToast,
            ) {
                isImpressionToast = it
                preferences.isGALogImpressionToastEnabled = it
            }
            DebugLinkRow("GA 로그 뷰어") {
                onClose()
                GALogViewerActivity.start(activity, focusGroupId = null)
            }
            DebugLinkRow("Chucker (네트워크 로그)") {
                onClose()
                activity.startActivity(Chucker.getLaunchIntent(activity))
            }
            DebugLinkRow("AdMob Ad Inspector") {
                onClose()
                openAdInspector(activity)
            }
            DebugLinkRow("광고 ID(AAID) 보기") {
                onClose()
                showAdvertisingId(activity)
            }
        }
    }
}

@Composable
private fun DebugToggleRow(
    title: String,
    description: String,
    isOn: Boolean,
    isEnabled: Boolean = true,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled) 1f else 0.4f)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = PretendardFontFamily, color = Gray100)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = description, fontSize = 12.sp, fontFamily = PretendardFontFamily, color = Gray50)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = isOn,
            onCheckedChange = onChange,
            enabled = isEnabled,
            colors = SwitchDefaults.colors(checkedTrackColor = Pink, checkedThumbColor = ColorWhite),
        )
    }
}

@Composable
private fun DebugLinkRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = PretendardFontFamily, color = Gray100, modifier = Modifier.weight(1f))
        Text(text = ">", fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = PretendardFontFamily, color = Gray50)
    }
}

/**
 * GA 로그를 0.3초 묶음 단위로 상단 카드로 보여준다. 최대 2장, 2.5초 뒤 사라지고 길게 누르면 멈춘다. 탭하면 뷰어로 이동.
 */
@Composable
private fun GALogToastHost(onTapGroup: (Long) -> Unit, modifier: Modifier = Modifier) {
    val store = DebugTools.gaLogStore
    val preferences = DebugTools.preferences
    val cardIds = remember { mutableStateListOf<Long>() }
    val updateCounts = remember { mutableStateMapOf<Long, Int>() }

    fun show(group: GALogGroup) {
        if (!preferences.isGALogRecordingEnabled || !preferences.isGALogToastEnabled) return
        if (group.collapsedLines(preferences.isGALogImpressionToastEnabled).isEmpty()) return
        if (group.id !in cardIds) {
            cardIds.add(0, group.id)
            while (cardIds.size > MAX_TOAST_CARD_COUNT) cardIds.removeAt(cardIds.lastIndex)
        }
        updateCounts[group.id] = (updateCounts[group.id] ?: 0) + 1
    }

    LaunchedEffect(store) {
        // 화면 전환 직후 나간 로그(새 화면 page_view 등)는 이 화면 오버레이가 붙기 전이라 직전 묶음을 한 번 보여준다.
        store.groups.lastOrNull()
            ?.takeIf { System.currentTimeMillis() - it.entries.last().timeMillis < TOAST_DISPLAY_MILLIS }
            ?.let(::show)
        store.updatedGroup.collect(::show)
    }
    LaunchedEffect(store) {
        store.version.collect { if (store.groups.isEmpty()) cardIds.clear() }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        cardIds.forEach { groupId ->
            val group = store.group(groupId) ?: return@forEach
            key(groupId) {
                GALogToastCard(
                    group = group,
                    updateCount = updateCounts[groupId] ?: 0,
                    includesImpression = preferences.isGALogImpressionToastEnabled,
                    onDismiss = { cardIds.remove(groupId) },
                    onTap = {
                        cardIds.remove(groupId)
                        onTapGroup(groupId)
                    },
                )
            }
        }
    }
}

private const val MAX_TOAST_CARD_COUNT = 2
private const val TOAST_DISPLAY_MILLIS = 2_500L
private const val TOAST_MAX_LINE_COUNT = 3

@Composable
private fun GALogToastCard(
    group: GALogGroup,
    updateCount: Int,
    includesImpression: Boolean,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
) {
    var isHolding by remember { mutableStateOf(false) }
    LaunchedEffect(updateCount, isHolding) {
        if (isHolding) return@LaunchedEffect
        delay(TOAST_DISPLAY_MILLIS)
        onDismiss()
    }
    val lines = group.collapsedLines(includesImpression)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Gray100.copy(alpha = 0.88f))
            .pointerInput(group.id) {
                detectTapGestures(
                    onPress = {
                        isHolding = true
                        tryAwaitRelease()
                        isHolding = false
                    },
                    onTap = { onTap() },
                )
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val trigger = group.tap?.let { "👆 ${it.elementDescription}" } ?: "📡 로그"
        Text(
            text = "$trigger · 로그 ${group.entries.size}건",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PretendardFontFamily,
            color = ColorWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        lines.take(TOAST_MAX_LINE_COUNT).forEach { (entry, count) ->
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = entry.kind.color)) { append("● ") }
                    withStyle(SpanStyle(color = if (entry.kind == GALogEntry.Kind.IMPRESSION) Gray40 else ColorWhite)) {
                        append(entry.toastBody(count))
                    }
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PretendardFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val hiddenCount = lines.drop(TOAST_MAX_LINE_COUNT).sumOf { it.second }
        if (hiddenCount > 0) {
            Text(text = "+${hiddenCount}건 더", fontSize = 11.sp, fontFamily = PretendardFontFamily, color = Gray40)
        }
    }
}

private fun GALogEntry.toastBody(count: Int): String = buildString {
    append(name)
    val label = target ?: screen.takeIf { kind == GALogEntry.Kind.PAGE_VIEW }
    label?.let { append("  $it") }
    (extraParameters.firstOrNull { it.first == "store_id" } ?: extraParameters.firstOrNull())
        ?.let { (key, value) -> append("  $key=$value") }
    if (count > 1) append("  ×$count")
}

private fun copyToClipboard(context: Context, label: String, text: String, toast: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
}

/** 광고 유닛별 요청/응답·실패 원인을 기기에서 확인한다. 테스트 기기에서만 열린다(에뮬레이터는 자동 등록). */
private fun openAdInspector(activity: ComponentActivity) {
    MobileAds.openAdInspector(activity) { error ->
        if (error != null) {
            AlertDialog.Builder(activity)
                .setTitle("Ad Inspector 실행 실패")
                .setMessage(error.message)
                .setPositiveButton("확인", null)
                .show()
        }
    }
}

/** AdMob 콘솔의 테스트 기기 목록(광고 ID 기준)과 이 기기를 대조하는 용도. */
private fun showAdvertisingId(activity: ComponentActivity) {
    activity.lifecycleScope.launch {
        val info = withContext(Dispatchers.IO) {
            runCatching { AdvertisingIdClient.getAdvertisingIdInfo(activity) }.getOrNull()
        }
        val id = info?.id.orEmpty()
        val limitText = when (info?.isLimitAdTrackingEnabled) {
            true -> "켜짐"
            false -> "꺼짐"
            null -> "확인 불가"
        }
        AlertDialog.Builder(activity)
            .setTitle("광고 ID (AAID)")
            .setMessage(
                "광고 추적 제한: $limitText\n${id.ifBlank { "광고 ID 를 가져오지 못했습니다" }}\n\n" +
                    "광고 ID 를 삭제했거나 제한이 켜져 있으면 0 으로 표시되고, AdMob 테스트 기기와 매칭되지 않습니다.",
            )
            .setPositiveButton("복사") { _, _ -> copyToClipboard(activity, "aaid", id, "광고 ID 복사됨") }
            .setNegativeButton("닫기", null)
            .show()
    }
}

internal val GALogEntry.Kind.color: Color
    get() = when (this) {
        GALogEntry.Kind.PAGE_VIEW -> Green
        GALogEntry.Kind.CLICK -> Pink
        GALogEntry.Kind.IMPRESSION -> Gray40
        GALogEntry.Kind.OTHER -> Gray70
    }
