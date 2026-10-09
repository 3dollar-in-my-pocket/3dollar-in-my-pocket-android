package com.zion830.threedollars.ui.home.ui.compose

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.threedollar.common.analytics.SDLogSender
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.ui.home.data.HomeCurationAdEventGate
import com.zion830.threedollars.ui.home.data.HomeCurationAdPolicy
import com.threedollar.common.R as CommonR

private const val CurationAdTag = "HomeCurationAdMob"
// Google의 고정 배너 테스트 단위. release는 기존 앱 리소스를 사용한다.
private const val TestBannerAdUnitId = "ca-app-pub-3940256099942544/6300978111"

@Composable
internal fun HomeCurationAdMobCard(
    card: SDAdMobCardModel,
    availableWidthDp: Int,
    onLoadFailed: (String) -> Unit,
) {
    val dimensions = HomeCurationAdPolicy.dimensions(card.height, availableWidthDp) ?: return
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentCard by rememberUpdatedState(card)
    val currentOnLoadFailed by rememberUpdatedState(onLoadFailed)
    val adView = remember(context, card.cardId, dimensions) {
        AdView(context).apply {
            setAdSize(if (dimensions.heightDp == 100) AdSize.LARGE_BANNER else AdSize.BANNER)
            adUnitId = if (BuildConfig.DEBUG) TestBannerAdUnitId else context.getString(CommonR.string.admob_list_banner)
        }
    }
    val eventGate = remember(adView) { HomeCurationAdEventGate() }
    var isDisposed by remember(adView) { mutableStateOf(false) }

    DisposableEffect(adView, lifecycle) {
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                if (isDisposed) return
                eventGate.onLoaded()
                Log.d(CurationAdTag, "loaded card=${currentCard.cardId} size=${dimensions.widthDp}x${dimensions.heightDp}")
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                if (isDisposed) return
                eventGate.onFailed()
                Log.d(CurationAdTag, "failed card=${currentCard.cardId} code=${error.code}")
                currentOnLoadFailed(currentCard.cardId)
            }

            override fun onAdImpression() {
                if (isDisposed || !eventGate.recordImpression()) return
                SDLogSender.sendImpression(currentCard.impressionLog)
                Log.d(CurationAdTag, "impression card=${currentCard.cardId}")
            }

            override fun onAdClicked() {
                if (isDisposed || !eventGate.recordClick()) return
                SDLogSender.sendClick(currentCard.clickLog)
                Log.d(CurationAdTag, "click card=${currentCard.cardId}")
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adView.resume()
                Lifecycle.Event.ON_PAUSE -> adView.pause()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        Log.d(CurationAdTag, "request card=${card.cardId} size=${dimensions.widthDp}x${dimensions.heightDp}")
        adView.loadAd(AdRequest.Builder().build())
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) adView.pause()
        onDispose {
            isDisposed = true
            eventGate.dispose()
            lifecycle.removeObserver(observer)
            adView.adListener = object : AdListener() {}
            (adView.parent as? ViewGroup)?.removeView(adView)
            adView.destroy()
            Log.d(CurationAdTag, "released card=${card.cardId}")
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { adView },
            modifier = Modifier.width(dimensions.widthDp.dp).height(dimensions.heightDp.dp),
        )
    }
}
