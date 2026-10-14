package com.zion830.threedollars.core.ui.sdui.section.store

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import base.compose.ColorWhite
import base.compose.Pink
import com.threedollar.common.sdui.model.element.SDActionEvent
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.common.sdui.model.section.SDStoreThreadPostSectionModel
import com.zion830.threedollars.core.ui.sdui.component.SDSectionDefaults
import com.zion830.threedollars.core.ui.sdui.component.SDSectionHeader
import com.zion830.threedollars.core.ui.sdui.foundation.sdSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

object SDStoreThreadPostSectionDefaults {
    val PlaceholderHeight = 400.dp
    val CardSpacing = 12.dp
    val PeekWidth = 36.dp
    val LoadingIndicatorSize = 24.dp
    const val HEIGHT_SETTLE_DELAY_MS = 300L
    const val RENDER_TIMEOUT_MS = 10_000L
}

private const val EMBED_BASE_URL = "https://threedollars.co.kr"
private const val JS_BRIDGE_NAME = "ThreeDollarsThreads"
private val THREADS_ORIGINS = listOf("https://www.threads.com", "https://www.threads.net")

@Composable
fun SDStoreThreadPostSection(
    model: SDStoreThreadPostSectionModel,
    onAction: (SDActionEvent) -> Unit,
    modifier: Modifier = Modifier,
    canLoad: Boolean = true,
    onRendered: () -> Unit = {},
) {
    val embeds = model.embeds
    if (embeds?.isEmpty() == true) return

    val permalinks = embeds?.map { it.permalink }.orEmpty()
    val cardHeights = remember(permalinks) { mutableStateMapOf<String, Int>() }
    val failedPermalinks = remember(permalinks) { mutableStateMapOf<String, Boolean>() }
    var settledHeight by rememberSaveable(permalinks) { mutableStateOf<Int?>(null) }
    val currentOnRendered by rememberUpdatedState(onRendered)

    LaunchedEffect(permalinks) {
        if (permalinks.isEmpty()) return@LaunchedEffect
        snapshotFlow {
            val isAllFinished = permalinks.all { it in cardHeights || it in failedPermalinks }
            if (isAllFinished) cardHeights.values.maxOrNull() ?: 0 else null
        }.collectLatest { maxHeight ->
            maxHeight ?: return@collectLatest
            delay(SDStoreThreadPostSectionDefaults.HEIGHT_SETTLE_DELAY_MS)
            settledHeight = maxHeight
        }
    }
    LaunchedEffect(settledHeight) {
        if ((settledHeight ?: 0) > 0) currentOnRendered()
    }

    if (settledHeight == 0) return

    val visibleEmbeds = embeds?.filter { it.permalink !in failedPermalinks }.orEmpty()
    val carouselHeight = settledHeight?.dp ?: SDStoreThreadPostSectionDefaults.PlaceholderHeight

    Column(
        modifier = modifier
            .fillMaxWidth()
            .sdSurface(style = model.style, defaultBackground = ColorWhite)
            .padding(vertical = SDSectionDefaults.VerticalPadding),
        verticalArrangement = Arrangement.spacedBy(SDSectionDefaults.HeaderContentSpacing)
    ) {
        SDSectionHeader(model = model.header, onAction = onAction)
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cardWidth = maxWidth - SDSectionDefaults.HorizontalPadding * 2 - SDStoreThreadPostSectionDefaults.PeekWidth
            if (!canLoad || embeds == null) {
                LoadingCard(
                    modifier = Modifier
                        .padding(horizontal = SDSectionDefaults.HorizontalPadding)
                        .width(cardWidth)
                        .height(carouselHeight)
                )
                return@BoxWithConstraints
            }
            val pagerState = rememberPagerState(pageCount = { visibleEmbeds.size })
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(cardWidth),
                contentPadding = PaddingValues(horizontal = SDSectionDefaults.HorizontalPadding),
                pageSpacing = SDStoreThreadPostSectionDefaults.CardSpacing,
                beyondViewportPageCount = visibleEmbeds.size,
                verticalAlignment = Alignment.Top,
                key = { visibleEmbeds[it].permalink },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(carouselHeight),
            ) { page ->
                val embed = visibleEmbeds[page]
                ThreadPostEmbedCard(
                    html = embed.html,
                    measuredHeight = cardHeights[embed.permalink]?.dp,
                    onHeightChanged = { if (embed.permalink !in failedPermalinks) cardHeights[embed.permalink] = it },
                    onLoadFailed = {
                        failedPermalinks[embed.permalink] = true
                        cardHeights.remove(embed.permalink)
                    },
                    onOpenUrl = { url ->
                        onAction(SDActionEvent(link = SDLink(type = SDLinkType.WEB, link = url), clickLog = embed.clickLog))
                    },
                )
            }
        }
    }
}

@Composable
private fun LoadingCard(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = Pink,
            modifier = Modifier.size(SDStoreThreadPostSectionDefaults.LoadingIndicatorSize)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun ThreadPostEmbedCard(
    html: String,
    measuredHeight: Dp?,
    onHeightChanged: (Int) -> Unit,
    onLoadFailed: () -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    var hasRendered by remember(html) { mutableStateOf(false) }
    val currentOnHeightChanged by rememberUpdatedState(onHeightChanged)
    val currentOnLoadFailed by rememberUpdatedState(onLoadFailed)
    val currentOnOpenUrl by rememberUpdatedState(onOpenUrl)

    LaunchedEffect(html) {
        delay(SDStoreThreadPostSectionDefaults.RENDER_TIMEOUT_MS)
        if (!hasRendered) currentOnLoadFailed()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(measuredHeight ?: SDStoreThreadPostSectionDefaults.PlaceholderHeight)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    isVerticalScrollBarEnabled = false
                    isHorizontalScrollBarEnabled = false
                    overScrollMode = View.OVER_SCROLL_NEVER
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.setSupportMultipleWindows(true)
                    settings.mediaPlaybackRequiresUserGesture = true
                    addJavascriptInterface(
                        ThreadPostHeightBridge { height ->
                            hasRendered = true
                            currentOnHeightChanged(height)
                        },
                        JS_BRIDGE_NAME
                    )
                    webViewClient = ExternalLinkWebViewClient(
                        onOpenUrl = { currentOnOpenUrl(it) },
                        onRenderProcessGone = { currentOnLoadFailed() },
                    )
                    webChromeClient = ExternalWindowChromeClient { currentOnOpenUrl(it) }
                    loadDataWithBaseURL(EMBED_BASE_URL, embedDocument(html), "text/html", "utf-8", null)
                }
            },
            onRelease = { it.destroy() },
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (hasRendered) 1f else 0f),
        )
        if (!hasRendered) {
            LoadingCard(modifier = Modifier.fillMaxSize())
        }
    }
}

private class ThreadPostHeightBridge(private val onHeight: (Int) -> Unit) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onHeight(height: Int) {
        if (height <= 0) return
        mainHandler.post { onHeight.invoke(height) }
    }
}

private class ExternalLinkWebViewClient(
    private val onOpenUrl: (String) -> Unit,
    private val onRenderProcessGone: () -> Unit,
) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url?.toString() ?: return false
        if (!request.isForMainFrame || !url.startsWith("http")) return false
        if (request.hasGesture()) onOpenUrl(url)
        return true
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
        onRenderProcessGone()
        return true
    }
}

private class ExternalWindowChromeClient(private val onOpenUrl: (String) -> Unit) : WebChromeClient() {
    override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
        if (!isUserGesture) return false
        val transport = resultMsg.obj as? WebView.WebViewTransport ?: return false
        val mainHandler = Handler(Looper.getMainLooper())
        val popupView = WebView(view.context)
        popupView.webViewClient = object : WebViewClient() {
            private var hasOpened = false

            private fun open(view: WebView, url: String?) {
                if (hasOpened || url.isNullOrBlank() || !url.startsWith("http")) return
                hasOpened = true
                onOpenUrl(url)
                mainHandler.post { view.destroy() }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                open(view, request.url?.toString())
                return true
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                open(view, url)
            }
        }
        popupView.webChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(window: WebView) {
                mainHandler.post { window.destroy() }
            }
        }
        transport.webView = popupView
        resultMsg.sendToTarget()
        return true
    }
}

private fun embedDocument(body: String): String {
    val origins = THREADS_ORIGINS.joinToString(", ") { "'$it'" }
    return """
    <!doctype html>
    <html>
    <head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <style>html, body { margin: 0; padding: 0; background: transparent; }</style>
    </head>
    <body>
    <div id="root">$body</div>
    <script>
    (function() {
        var root = document.getElementById('root');
        var threadsOrigins = [$origins];
        var isEmbedRendered = false;
        var report = function() {
            if (!isEmbedRendered) { return; }
            window.$JS_BRIDGE_NAME.onHeight(Math.ceil(root.getBoundingClientRect().height));
        };
        window.addEventListener('message', function(event) {
            if (threadsOrigins.indexOf(event.origin) < 0) { return; }
            isEmbedRendered = true;
            requestAnimationFrame(report);
        });
        new ResizeObserver(report).observe(root);
    })();
    </script>
    </body>
    </html>
    """.trimIndent()
}
