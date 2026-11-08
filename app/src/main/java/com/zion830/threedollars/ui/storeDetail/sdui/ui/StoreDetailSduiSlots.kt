package com.zion830.threedollars.ui.storeDetail.sdui.ui

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.naver.maps.geometry.LatLng
import com.threedollar.common.sdui.model.element.SDLogModel
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.zion830.threedollars.core.ui.sdui.section.store.SDStoreSectionSlots
import com.zion830.threedollars.ui.ads.StoreAdMobBanner
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailAdLoadPolicy
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private const val STORE_MAP_ZOOM = 16.0

/**
 * 가게 상세 섹션 중 앱 모듈 SDK 가 필요한 부분(EDIT 지도, AD_MOB 배너).
 * 홈 시트 tip 에서 미리 그려 둔 상세에서도 배너를 바로 요청해 올렸을 때 이미 떠 있게 하고, 보이기 전에는 INVISIBLE 로 둔다.
 *
 * @param isDisplayed 상세가 지금 보이는지. 홈 시트 tip 처럼 상세가 뒤에 미리 그려져 있을 때는 false 라 배너를 숨긴다.
 * @param canLoadThreadPost 쓰레드 임베드를 로드해도 되는지.
 * 둘 다 Compose State 를 읽는 람다로 넘겨야 값이 바뀔 때 다시 그려진다. `StoreDetailAdLoadPolicy` 참고.
 */
fun storeDetailSduiSlots(
    onAdClick: (SDLogModel?) -> Unit,
    isDisplayed: () -> Boolean = { true },
    canLoadThreadPost: () -> Boolean = { true },
): SDStoreSectionSlots = SDStoreSectionSlots(
    mapContent = { latitude, longitude, modifier -> StoreLocationMap(latitude, longitude, modifier) },
    adContent = { card, modifier ->
        StoreAdMobBanner(
            card = card,
            onAdClick = onAdClick,
            modifier = modifier,
            adVisibility = StoreDetailAdLoadPolicy.bannerVisibility(isDisplayed()),
        )
    },
    canLoadThreadPost = canLoadThreadPost,
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
