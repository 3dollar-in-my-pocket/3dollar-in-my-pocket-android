package com.zion830.threedollars.debug

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlin.math.hypot

/**
 * 화면 탭을 가로채 "직전에 무엇을 눌렀는지"(요소·화면·캡처)를 [GALogStore]에 남긴다. 이벤트는 그대로 원래 콜백으로 넘긴다.
 * iOS `DebugTapRecordingWindow` 와 같은 역할.
 */
class TapRecordingWindowCallback(
    private val activity: Activity,
    private val delegate: Window.Callback,
    private val store: GALogStore,
    private val isRecordingEnabled: () -> Boolean,
) : Window.Callback by delegate {

    private var downX = 0f
    private var downY = 0f

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isRecordingEnabled()) recordTapIfNeeded(event)
        return delegate.dispatchTouchEvent(event)
    }

    private fun recordTapIfNeeded(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val density = activity.resources.displayMetrics.density
                if (hypot(event.x - downX, event.y - downY) > TAP_MOVEMENT_TOLERANCE_DP * density) return
                val decorView = activity.window.decorView
                if (isOnDebugOverlay(decorView, event.x, event.y)) return
                val tap = DebugTapInfo(
                    timeMillis = System.currentTimeMillis(),
                    elementDescription = describeElement(decorView, event.x, event.y),
                    screenName = screenName(activity),
                )
                store.recordTap(tap)
                captureSnapshot(activity, event.x, event.y) { tap.snapshot = it }
            }
        }
    }

    companion object {
        private const val TAP_MOVEMENT_TOLERANCE_DP = 10f
        private const val SNAPSHOT_SCALE = 0.35f
        private const val MARKER_RADIUS_DP = 22f
        private const val TEXT_SEARCH_DEPTH = 6
        private const val MARKER_COLOR = "#FF5C43"

        fun install(activity: Activity, store: GALogStore, isRecordingEnabled: () -> Boolean) {
            val window = activity.window ?: return
            val current = window.callback ?: return
            if (current is TapRecordingWindowCallback) return
            window.callback = TapRecordingWindowCallback(activity, current, store, isRecordingEnabled)
        }

        /** DEV 버튼·메뉴·알림 카드를 누른 건 앱 탭이 아니다. */
        private fun isOnDebugOverlay(decorView: View, x: Float, y: Float): Boolean {
            val overlay = decorView.findViewWithTag<ViewGroup>(DebugOverlay.OVERLAY_TAG) ?: return false
            val composeRoot = overlay.getChildAt(0) as? ViewRootForTest ?: return false
            return describeCompose(composeRoot, x, y) != null
        }

        fun describeElement(root: View, x: Float, y: Float): String {
            val target = findTouchTarget(root, x, y) ?: return "알 수 없음"
            if (target is ViewRootForTest) return describeCompose(target, x, y) ?: target.javaClass.simpleName
            return displayText(target)?.let { "\"$it\" ${target.javaClass.simpleName}" } ?: target.javaClass.simpleName
        }

        /**
         * 탭을 실제로 받을 뷰. 터치 분배처럼 위에 겹친 레이어부터 보되, 클릭을 받지 않는 레이어(빈 컨테이너, 디버그 오버레이,
         * 탭 위치에 요소가 없는 Compose 레이어)는 통과해 아래 형제 뷰를 찾는다.
         */
        private fun findTouchTarget(view: View, x: Float, y: Float): View? {
            if (view.visibility != View.VISIBLE || view.tag == DebugOverlay.OVERLAY_TAG || !view.containsWindowPoint(x, y)) return null
            if (view is ViewGroup) {
                for (index in view.childCount - 1 downTo 0) {
                    findTouchTarget(view.getChildAt(index), x, y)?.let { return it }
                }
            }
            return when {
                view is ViewRootForTest -> view.takeIf { describeCompose(view, x, y) != null }
                view.isClickable || view.isLongClickable -> view
                else -> null
            }
        }

        private fun View.containsWindowPoint(x: Float, y: Float): Boolean {
            val location = IntArray(2).also { getLocationInWindow(it) }
            return x >= location[0] && x < location[0] + width && y >= location[1] && y < location[1] + height
        }

        private fun displayText(view: View): String? {
            view.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
            (view as? TextView)?.text?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
            return firstText(view, depth = 0)
        }

        private fun firstText(view: View, depth: Int): String? {
            if (depth >= TEXT_SEARCH_DEPTH || view !is ViewGroup) return null
            for (index in 0 until view.childCount) {
                val child = view.getChildAt(index)
                if (child.visibility != View.VISIBLE) continue
                (child as? TextView)?.text?.toString()?.takeIf { it.isNotBlank() }?.let { return it }
                firstText(child, depth + 1)?.let { return it }
            }
            return null
        }

        /** Compose 화면은 뷰 하나라 semantics 트리에서 탭 위치의 가장 안쪽 요소를 찾는다. */
        private fun describeCompose(root: ViewRootForTest, x: Float, y: Float): String? = runCatching {
            val path = mutableListOf<SemanticsNode>()
            var node: SemanticsNode? = root.semanticsOwner.rootSemanticsNode
            while (node != null) {
                path.add(node)
                node = node.children.lastOrNull { child ->
                    val bounds = child.boundsInWindow
                    x >= bounds.left && x < bounds.right && y >= bounds.top && y < bounds.bottom
                }
            }
            path.asReversed().forEach { semanticsNode ->
                val config = semanticsNode.config
                val text = config.getOrNull(SemanticsProperties.Text)?.joinToString(" ") { it.text }
                    ?: config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString(" ")
                if (!text.isNullOrBlank()) return@runCatching "\"$text\" Compose"
                if (config.getOrNull(SemanticsActions.OnClick) != null) return@runCatching "Compose 클릭 요소"
            }
            null
        }.getOrNull()

        fun screenName(activity: Activity): String {
            val fragmentName = (activity as? FragmentActivity)?.supportFragmentManager?.let(::deepestVisibleFragment)
                ?.javaClass?.simpleName
            return listOfNotNull(activity.javaClass.simpleName, fragmentName).joinToString(" › ")
        }

        private fun deepestVisibleFragment(fragmentManager: FragmentManager): Fragment? {
            val visible = fragmentManager.primaryNavigationFragment?.takeIf { it.isVisible }
                ?: fragmentManager.fragments.lastOrNull { it.isVisible }
                ?: return null
            return deepestVisibleFragment(visible.childFragmentManager) ?: visible
        }

        private fun captureSnapshot(activity: Activity, x: Float, y: Float, onCaptured: (Bitmap) -> Unit) {
            val decorView = activity.window.decorView
            if (decorView.width <= 0 || decorView.height <= 0) return
            val markerRadius = MARKER_RADIUS_DP * activity.resources.displayMetrics.density
            val finish = { full: Bitmap -> onCaptured(full.withTapMarker(x, y, markerRadius)) }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val bitmap = Bitmap.createBitmap(decorView.width, decorView.height, Bitmap.Config.ARGB_8888)
                runCatching {
                    PixelCopy.request(activity.window, bitmap, { result ->
                        if (result == PixelCopy.SUCCESS) finish(bitmap)
                    }, Handler(Looper.getMainLooper()))
                }
            } else {
                runCatching { decorView.drawToBitmap() }.getOrNull()?.let(finish)
            }
        }

        private fun Bitmap.withTapMarker(x: Float, y: Float, radius: Float): Bitmap {
            val marked = Bitmap.createBitmap(
                (width * SNAPSHOT_SCALE).toInt().coerceAtLeast(1),
                (height * SNAPSHOT_SCALE).toInt().coerceAtLeast(1),
                Bitmap.Config.ARGB_8888,
            )
            val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = radius / 3
                color = Color.parseColor(MARKER_COLOR)
            }
            Canvas(marked).apply {
                scale(SNAPSHOT_SCALE, SNAPSHOT_SCALE)
                drawBitmap(this@withTapMarker, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
                drawCircle(x, y, radius, markerPaint)
            }
            recycle()
            return marked
        }
    }
}
