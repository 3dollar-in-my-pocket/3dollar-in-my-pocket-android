package com.zion830.threedollars.ui.popup

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.home.presentation.data.HomeStoreType
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.getCurrentDate
import com.threedollar.common.ext.loadImage
import com.threedollar.common.utils.SharedPrefUtils
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.FragmentPopupBinding
import dagger.hilt.android.AndroidEntryPoint
import java.net.URISyntaxException
import javax.inject.Inject

@AndroidEntryPoint
class PopupFragment : BaseFragment<FragmentPopupBinding, PopupViewModel>() {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: PopupViewModel by activityViewModels()

    override fun initView() {
        binding.run {
            viewModel.popups.value.firstOrNull()?.let {
                ivPopup.loadImage(it.imageUrl)
            }

            tvClose.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("screen", "main_ad_banner")
                }
                EventTracker.logEvent(Constants.CLICK_CLOSE, bundle)
                it.findNavController().navigateUp()
            }
            tvTodayNotPopup.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("screen", "main_ad_banner")
                }
                EventTracker.logEvent(Constants.CLICK_NOT_SHOW_TODAY, bundle)
                sharedPrefUtils.setTodayNotPopupDate(getCurrentDate())
                it.findNavController().navigateUp()
            }
            ivPopup.setOnClickListener {
                ivPopup.isVisible = false
                webView.isVisible = true
                viewModel.popups.value.let { popups ->
                    popups.firstOrNull()?.let { popup ->
                        val bundle = Bundle().apply {
                            putString("screen", "main_ad_banner")
                            putString("advertisement_id", popup.advertisementId.toString())
                        }
                        EventTracker.logEvent(Constants.CLICK_AD_BANNER, bundle)
                        popup.linkUrl?.let { linkUrl ->
                            webView.loadUrl(linkUrl)

                        }
                    }
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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "PopupFragment", screenName = "main_ad_banner")
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

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPopupBinding =
        FragmentPopupBinding.inflate(inflater, container, false)
}