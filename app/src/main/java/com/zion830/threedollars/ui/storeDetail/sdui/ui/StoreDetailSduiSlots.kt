package com.zion830.threedollars.ui.storeDetail.sdui.ui

import android.os.Bundle
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
 */
fun storeDetailSduiSlots(onAdClick: (SDLogModel?) -> Unit): SDStoreSectionSlots = SDStoreSectionSlots(
    mapContent = { latitude, longitude, modifier -> StoreLocationMap(latitude, longitude, modifier) },
    adContent = { card, modifier -> StoreDetailBanner(card, onAdClick, modifier) },
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
 */
@Composable
private fun StoreDetailBanner(
    card: SDStoreAdmobSectionModel.Card,
    onAdClick: (SDLogModel?) -> Unit,
    modifier: Modifier,
) {
    var isFailed by remember { mutableStateOf(false) }
    if (isFailed) return
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
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
    )
}
