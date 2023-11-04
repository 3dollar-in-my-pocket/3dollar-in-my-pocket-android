package com.zion830.threedollars.ui.popup

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentPopupBinding
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import java.net.URISyntaxException

@AndroidEntryPoint
class PopupFragment : LegacyBaseFragment<FragmentPopupBinding, PopupViewModel>(R.layout.fragment_popup) {

    override val viewModel: PopupViewModel by activityViewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun initView() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding.run {
            tvClose.setOnClickListener {
                it.findNavController().navigateUp()
            }
            tvTodayNotPopup.setOnClickListener {
                viewModel?.popups?.value?.let { popup ->
                    LegacySharedPrefUtils.setPopupUrl(popup[0].linkUrl!!)
                }
                it.findNavController().navigateUp()
            }
            ivPopup.setOnClickListener {
                EventTracker.logEvent(Constants.SPLASH_POPUP_CLICKED)

                ivPopup.isVisible = false
                webView.isVisible = true
                viewModel?.popups?.value?.let { popups ->
                    webView.loadUrl(popups[0].linkUrl!!)
                }
            }
            webView.apply {
                settings.apply {
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    javaScriptEnabled = true // 웹페이지 자바스크립트 허용 여부
                    setSupportMultipleWindows(false) // 새창 띄우기 허용 여부
                    javaScriptCanOpenWindowsAutomatically = false // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
                    loadWithOverviewMode = true // 메타태그 허용 여부
                    useWideViewPort = true // 화면 사이즈 맞추기 허용 여부
                    setSupportZoom(false) // 화면 줌 허용 여부
                    builtInZoomControls = false // 화면 확대 축소 허용 여부
                    displayZoomControls = false // 화면 확대/축소 허용
                    layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING // 컨텐츠 사이즈 맞추기
                    cacheMode = WebSettings.LOAD_NO_CACHE // 브라우저 캐시 허용 여부
                    domStorageEnabled = true // 로컬저장소 허용 여부
                    databaseEnabled = true
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
            }
        }
    }

    inner class WebViewClient : android.webkit.WebViewClient() {
        @SuppressWarnings("deprecation")
        @Override
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val intent = parse(url)
            return if (isIntent(url)) {
                if (isExistInfo(intent) or isExistPackage(intent))
                    start(intent)
                else
                    gotoMarket(intent)
            } else if (isMarket(url))
                start(intent)
            else
                false
        }


        @TargetApi(Build.VERSION_CODES.N)
        @Override
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()

            val intent = parse(url)
            return if (isIntent(url)) {
                if (isExistInfo(intent) or isExistPackage(intent))
                    start(intent)
                else
                    gotoMarket(intent)
            } else if (isMarket(url))
                start(intent)
            else
                false
        }

        private fun isIntent(url: String?) = url?.matches(Regex("^intent:?\\w*://\\S+$")) ?: false

        private fun isMarket(url: String?) = url?.matches(Regex("^market://\\S+$")) ?: false

        private fun isExistInfo(intent: Intent?): Boolean {
            return try {
                intent != null && requireActivity().packageManager.getPackageInfo(
                    intent.`package`.toString(),
                    PackageManager.GET_ACTIVITIES
                ) != null
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

        }

        private fun isExistPackage(intent: Intent?): Boolean =
            intent != null && requireActivity().packageManager.getLaunchIntentForPackage(intent.`package`.toString()) != null

        private fun parse(url: String): Intent? {
            return try {
                Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            } catch (e: URISyntaxException) {
                null
            }

        }

        private fun start(intent: Intent?): Boolean {
            intent?.let { startActivity(it) }
            return true
        }

        private fun gotoMarket(intent: Intent?): Boolean {
            intent?.let {
                start(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${it.`package`}")
                })
            }
            return true
        }
    }
}