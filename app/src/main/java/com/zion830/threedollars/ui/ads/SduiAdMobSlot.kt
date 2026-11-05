package com.zion830.threedollars.ui.ads

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * 서버(SDUI)가 정한 높이의 애드몹 슬롯.
 *
 * 슬롯은 [heightDp] 로 고정하고, 광고는 슬롯 너비·높이를 최대로 하는 인라인 적응형 배너로 요청해 슬롯 밖으로 넘치지 않게 한다.
 * 광고 WebView 가 포커스를 가져가면 목록이 광고 쪽으로 튀므로 하위 뷰 포커스를 막는다.
 * [adUnitId] 로 실패하면 [fallbackAdUnitId] 로 한 번 더 요청하고, [adListener] 에는 마지막 실패만 알린다.
 * AdView 는 단위 ID 를 한 번만 정할 수 있어서 재시도는 새 AdView 로 한다.
 *
 * @param adVisibility AdView 의 visibility. 미리 요청만 하고 노출은 막아야 하면 [View.INVISIBLE] 로 둔다.
 */
@Composable
fun SduiAdMobSlot(
    adUnitId: String,
    fallbackAdUnitId: String,
    heightDp: Int,
    adListener: AdListener,
    modifier: Modifier = Modifier,
    adVisibility: Int = View.VISIBLE,
) {
    var requestUnitId by remember(adUnitId) { mutableStateOf(adUnitId) }
    val currentAdListener by rememberUpdatedState(adListener)
    val retryingAdListener = remember(requestUnitId, fallbackAdUnitId) {
        object : AdListener() {
            override fun onAdLoaded() = currentAdListener.onAdLoaded()
            override fun onAdImpression() = currentAdListener.onAdImpression()
            override fun onAdClicked() = currentAdListener.onAdClicked()
            override fun onAdOpened() = currentAdListener.onAdOpened()
            override fun onAdClosed() = currentAdListener.onAdClosed()

            override fun onAdFailedToLoad(error: LoadAdError) {
                val retryUnitId = AdMobUnitId.retryUnitId(failedUnitId = requestUnitId, fallbackUnitId = fallbackAdUnitId)
                if (retryUnitId != null) {
                    requestUnitId = retryUnitId
                } else {
                    currentAdListener.onAdFailedToLoad(error)
                }
            }
        }
    }
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        val widthDp = maxWidth.value.toInt()
        key(widthDp, requestUnitId) {
            AdMobSlotView(
                adUnitId = requestUnitId,
                widthDp = widthDp,
                heightDp = heightDp,
                adListener = retryingAdListener,
                adVisibility = adVisibility,
            )
        }
    }
}

@Composable
private fun AdMobSlotView(adUnitId: String, widthDp: Int, heightDp: Int, adListener: AdListener, adVisibility: Int) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).apply {
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                addView(
                    AdView(context).apply {
                        visibility = adVisibility
                        setAdSize(AdSize.getInlineAdaptiveBannerAdSize(widthDp, heightDp))
                        this.adUnitId = adUnitId
                        this.adListener = adListener
                        loadAd(AdRequest.Builder().build())
                    },
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER,
                    ),
                )
            }
        },
        update = { container -> container.getChildAt(0)?.visibility = adVisibility },
        onRelease = { container -> (container.getChildAt(0) as? AdView)?.destroy() },
    )
}
