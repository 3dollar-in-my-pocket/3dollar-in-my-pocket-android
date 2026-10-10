package com.zion830.threedollars.ui.storeDetail.sdui.ui

import android.os.Bundle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.naver.maps.geometry.LatLng
import com.threedollar.common.sdui.model.element.SDLogModel
import com.threedollar.common.sdui.model.section.SDStoreAdmobSectionModel
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSectionSlots
import com.zion830.threedollars.ui.ads.SduiAdMobSlot
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val STORE_MAP_ZOOM = 16.0

/**
 * 가게 상세 섹션 중 앱 모듈 SDK 가 필요한 부분(EDIT 지도, AD_MOB 배너).
 *
 * @param canLoadAd 지금 광고를 로드해도 되는지. 홈 시트 tip 처럼 상세가 뒤에 미리 그려져 있을 때는 false 다.
 * Compose State 를 읽는 람다로 넘겨야 값이 바뀔 때 배너가 다시 그려진다. `StoreDetailAdLoadPolicy` 참고.
 */
fun storeDetailSduiSlots(
    onAdClick: (SDLogModel?) -> Unit,
    canLoadAd: () -> Boolean = { true },
): SDStoreSectionSlots = SDStoreSectionSlots(
    mapContent = { latitude, longitude, modifier -> StoreLocationMap(latitude, longitude, modifier) },
    adContent = { card, modifier -> StoreDetailBanner(card, canLoadAd(), onAdClick, modifier) },
)

/**
 * 조작할 수 없는 가게 위치 지도. 확대는 지도 위 버튼(서버 액션)으로만 한다.
 */
@Composable
private fun StoreLocationMap(latitude: Double, longitude: Double, modifier: Modifier) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val position = LatLng(latitude, longitude)
    val mapView = remember {
        MapView(
            context,
            NaverMapOptions()
                .liteModeEnabled(true)
                .allGesturesEnabled(false)
                .zoomControlEnabled(false)
                .scaleBarEnabled(false)
                .locationButtonEnabled(false)
                .compassEnabled(false)
                .camera(CameraPosition(position, STORE_MAP_ZOOM))
        ).apply { onCreate(Bundle()) }
    }
    val marker = remember {
        Marker().apply { icon = OverlayImage.fromResource(DesignSystemR.drawable.ic_mappin_focused_on) }
    }
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) mapView.onStart()
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) mapView.onResume()
        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }
    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            view.getMapAsync { map ->
                map.cameraPosition = CameraPosition(position, STORE_MAP_ZOOM)
                marker.position = position
                if (marker.map != map) marker.map = map
            }
        }
    )
}

/**
 * 가게 상세 배너 광고. 슬롯 높이는 서버가 내려준 카드 높이를 따르고, 로드에 실패하면 자리를 접는다.
 *
 * [canLoad] 가 false 면 광고를 요청하지 않고 같은 크기의 빈 자리만 둔다. 상세가 보여 true 가 되는 순간 로드해서,
 * 미리 그려 둔 상세가 올라올 때 레이아웃이 튀지 않는다.
 */
@Composable
private fun StoreDetailBanner(
    card: SDStoreAdmobSectionModel.Card,
    canLoad: Boolean,
    onAdClick: (SDLogModel?) -> Unit,
    modifier: Modifier,
) {
    var isFailed by remember { mutableStateOf(false) }
    if (isFailed) return
    if (!canLoad) {
        Spacer(
            modifier = modifier
                .bannerSlotPadding()
                .fillMaxWidth()
                .height(card.slotHeightDp.dp),
        )
        return
    }
    val context = LocalContext.current
    val currentCard by rememberUpdatedState(card)
    val currentOnAdClick by rememberUpdatedState(onAdClick)
    SduiAdMobSlot(
        adUnitId = context.getString(CommonR.string.admob_store_detail_banner),
        heightDp = card.slotHeightDp,
        adListener = remember {
            object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isFailed = true
                }

                override fun onAdClicked() {
                    currentOnAdClick(currentCard.clickLog)
                }
            }
        },
        modifier = modifier.bannerSlotPadding(),
    )
}

/** 배너와 로드 전 빈 자리가 같은 크기를 차지하도록 여백을 한 곳에서 정한다. */
private fun Modifier.bannerSlotPadding(): Modifier = padding(horizontal = 20.dp, vertical = 16.dp)
