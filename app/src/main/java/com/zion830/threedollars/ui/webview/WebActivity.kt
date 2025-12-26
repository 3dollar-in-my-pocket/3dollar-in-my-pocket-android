package com.zion830.threedollars.ui.webview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.threedollar.common.R as CommonR
import com.threedollar.common.base.BaseComposeActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.compose.ColorBlack

@AndroidEntryPoint
class WebActivity : BaseComposeActivity<WebViewModel>() {

    override val viewModel: WebViewModel by viewModels()

    private val url: String by lazy {
        intent.getStringExtra(EXTRA_URL) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WebViewScreen(
                    url = url,
                    viewModel = viewModel,
                    onClose = { finish() },
                    onCopyUrl = { currentUrl ->
                        copyToClipboard(currentUrl)
                        showToast(CommonR.string.copy)
                    }
                )
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("URL", text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        private const val EXTRA_URL = "extra_url"

        fun getIntent(context: Context, url: String): Intent {
            return Intent(context, WebActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewTopBar(
    title: String,
    onClose: () -> Unit,
    onCopyUrl: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 1.dp)
            .background(Color.White)
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = com.zion830.threedollars.core.designsystem.R.drawable.ic_close_white),
                contentDescription = "Close",
                tint = ColorBlack
            )
        }

        Text(
            text = title.ifEmpty { "웹페이지" },
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 56.dp)
        )

        IconButton(
            onClick = onCopyUrl,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_copy_18),
                contentDescription = "Copy URL",
                tint = Color.Black
            )
        }
    }
}

@Composable
fun WebViewScreen(
    url: String,
    viewModel: WebViewModel,
    onClose: () -> Unit,
    onCopyUrl: (String) -> Unit
) {
    val title by viewModel.title.collectAsState()
    val currentUrl by viewModel.currentUrl.collectAsState()
    var webView by remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onClose()
        }
    }

    Scaffold(
        topBar = {
            WebViewTopBar(
                title = title,
                onClose = onClose,
                onCopyUrl = { onCopyUrl(currentUrl) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            setSupportZoom(true)
                            builtInZoomControls = false
                            loadWithOverviewMode = true
                            useWideViewPort = true
                        }

                        webViewClient = WebViewClient()

                        webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                title?.let { viewModel.updateTitle(it) }
                            }
                        }

                        webView = this
                        viewModel.updateCurrentUrl(url)
                        loadUrl(url)
                    }
                },
                update = { view ->
                    view.url?.let { viewModel.updateCurrentUrl(it) }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WebViewTopBarPreview() {
    MaterialTheme {
        WebViewTopBar(
            title = "예제 웹페이지 타이틀",
            onClose = {},
            onCopyUrl = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WebViewTopBarEmptyTitlePreview() {
    MaterialTheme {
        WebViewTopBar(
            title = "",
            onClose = {},
            onCopyUrl = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WebViewTopBarLongTitlePreview() {
    MaterialTheme {
        WebViewTopBar(
            title = "이것은 매우 긴 웹페이지 타이틀입니다. 오버플로우 처리를 확인하기 위한 예제입니다.",
            onClose = {},
            onCopyUrl = {}
        )
    }
}
