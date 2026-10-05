package com.zion830.threedollars.ui.ads

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * 서버(SDUI)가 정한 높이의 애드몹 슬롯.
 *
 * 슬롯은 [heightDp] 로 고정하고, 광고는 슬롯 너비·높이를 최대로 하는 인라인 적응형 배너로 요청해 슬롯 밖으로 넘치지 않게 한다.
 * 광고 WebView 가 포커스를 가져가면 목록이 광고 쪽으로 튀므로 하위 뷰 포커스를 막는다.
 */
@Composable
fun SduiAdMobSlot(
    adUnitId: String,
    heightDp: Int,
    adListener: AdListener,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp),
        contentAlignment = Alignment.Center,
    ) {
        val widthDp = maxWidth.value.toInt()
        key(widthDp) {
            AdMobSlotView(adUnitId = adUnitId, widthDp = widthDp, heightDp = heightDp, adListener = adListener)
        }
    }
}

@Composable
private fun AdMobSlotView(adUnitId: String, widthDp: Int, heightDp: Int, adListener: AdListener) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).apply {
                descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                addView(
                    AdView(context).apply {
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
        onRelease = { container -> (container.getChildAt(0) as? AdView)?.destroy() },
    )
}
