package com.zion830.threedollars.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.ui.webview.WebViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 서버가 내려준 웹 링크를 하단 탭 안에서 보여준다(TH-1650 이벤트 탭).
 * 로그인 토큰·UA 는 인앱 브라우저([com.zion830.threedollars.ui.webview.WebActivity])와 같은 규칙으로 신뢰 도메인에만 보낸다.
 */
@AndroidEntryPoint
class WebTabFragment : Fragment() {

    private val viewModel: WebViewModel by viewModels()
    private var webView: WebView? = null

    private val url: String by lazy { requireArguments().getString(ARG_URL).orEmpty() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val webView = WebView(requireContext()).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webChromeClient = WebChromeClient()
        }
        this.webView = webView
        return FrameLayout(requireContext()).apply {
            setBackgroundColor(Color.WHITE)
            addView(webView, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = webView ?: return
        val webBackCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                webView.goBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, webBackCallback)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val target = request.url
                if (target.scheme.orEmpty().startsWith(APP_SCHEME_PREFIX)) {
                    DynamicLinkActivity.launch(requireContext(), target.toString())
                    return true
                }
                if (request.isForMainFrame) {
                    view.settings.userAgentString = viewModel.browserRequest(target.toString()).userAgent
                }
                return false
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                webBackCallback.isEnabled = view.canGoBack()
            }
        }

        val request = viewModel.browserRequest(url)
        webView.settings.userAgentString = request.userAgent
        webView.loadUrl(url, request.headers)
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onPause() {
        webView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        webView?.let { view ->
            (view.parent as? ViewGroup)?.removeView(view)
            view.destroy()
        }
        webView = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_URL = "url"
        private const val ARG_TAB_ID = "tabId"

        /** 웹 페이지 안의 앱 링크(`dollars://`, `dollars-dev://`)는 웹뷰 대신 앱 화면으로 연다. */
        private const val APP_SCHEME_PREFIX = "dollars"

        fun arguments(tabId: String, url: String): Bundle = bundleOf(ARG_TAB_ID to tabId, ARG_URL to url)

        fun tabIdOf(arguments: Bundle?): String? = arguments?.getString(ARG_TAB_ID)
    }
}
