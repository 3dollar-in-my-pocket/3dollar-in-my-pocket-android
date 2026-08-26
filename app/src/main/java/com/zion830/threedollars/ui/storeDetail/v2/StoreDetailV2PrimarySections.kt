package com.zion830.threedollars.ui.storeDetail.v2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray20
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray100
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import coil3.compose.AsyncImage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.R as CommonR

@Composable
internal fun StoreDetailCalloutSection(
    section: StoreDetailSectionModel.Callout,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray10)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(section.content.title.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(15))
        section.content.subTitle?.let {
            Text(it.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(13))
        }
        section.content.footerLeftButton?.let { button ->
            StoreDetailTextButton(button = button) {
                onAction(syntheticActionBar(button, "CALLOUT"))
            }
        }
    }
}

@Composable
internal fun StoreDetailPreviewSection(
    section: StoreDetailSectionModel.Preview,
    onAction: (StoreActionBarModel) -> Unit,
    onFavoriteToggle: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = section.header.title?.text.orEmpty(),
                color = Gray100,
                fontFamily = PretendardFontFamily,
                fontSize = dpToSp(22),
                modifier = Modifier.weight(1f),
            )
            section.header.badge?.let { StoreDetailImage(it, Modifier.size(20.dp), ContentScale.Fit) }
            Text(
                text = stringResource(
                    if (section.additionalInfos.isSubscriber) CommonR.string.store_detail_saved else CommonR.string.save
                ),
                color = if (section.additionalInfos.isSubscriber) Pink else Gray70,
                fontFamily = PretendardFontFamily,
                fontSize = dpToSp(13),
                modifier = Modifier.clip(CircleShape).background(Gray10).clickable {
                    onFavoriteToggle(section.additionalInfos.isSubscriber)
                }.padding(horizontal = 10.dp, vertical = 8.dp),
            )
        }
        section.metadata.primary.takeIf(List<*>::isNotEmpty)?.let { StoreDetailChipRow(it) }
        section.metadata.secondary.takeIf(List<*>::isNotEmpty)?.let { StoreDetailChipRow(it) }
        section.contributorActionBar?.let { action ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Gray10)
                    .clickable { onAction(action) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(action.button.text.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(13))
            }
        }
        if (section.actionBars.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                section.actionBars.forEach { action ->
                    StoreDetailActionButton(
                        action = action,
                        onClick = { onAction(action) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (section.images.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                section.images.forEach { image ->
                    StoreDetailImage(
                        image = image,
                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        section.bodies.forEach { body ->
            Text(
                text = body.text.text,
                color = Gray70,
                fontFamily = PretendardFontFamily,
                fontSize = dpToSp(13),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Gray10).padding(12.dp),
            )
        }
    }
}

@Composable
internal fun StoreDetailTabSection(
    section: StoreDetailSectionModel.Tab,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        section.tabs.forEach { action ->
            StoreDetailTextButton(button = action.button) { onAction(action) }
        }
    }
}

@Composable
internal fun StoreDetailMapSection(
    section: StoreDetailSectionModel.Map,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
        StoreDetailNaverMap(
            latitude = section.location.latitude,
            longitude = section.location.longitude,
            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            section.footerLeft?.let { action ->
                StoreDetailActionButton(action, { onAction(action) }, Modifier.weight(1f))
            }
            StoreDetailActionButton(section.footerRight, { onAction(section.footerRight) }, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StoreDetailNaverMap(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember(context) { runCatching { MapView(context) }.getOrNull() } ?: return

    DisposableEffect(mapView, lifecycle) {
        var destroyed = false
        runCatching { mapView.onCreate(null) }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> runCatching { mapView.onStart() }
                Lifecycle.Event.ON_RESUME -> runCatching { mapView.onResume() }
                Lifecycle.Event.ON_PAUSE -> runCatching { mapView.onPause() }
                Lifecycle.Event.ON_STOP -> runCatching { mapView.onStop() }
                Lifecycle.Event.ON_DESTROY -> {
                    runCatching { mapView.onDestroy() }
                    destroyed = true
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        runCatching {
            mapView.getMapAsync { map ->
                val position = LatLng(latitude, longitude)
                map.uiSettings.isZoomControlEnabled = false
                map.uiSettings.isScaleBarEnabled = false
                map.moveCamera(CameraUpdate.scrollTo(position))
                Marker().apply {
                    this.position = position
                    icon = OverlayImage.fromResource(DesignSystemR.drawable.ic_mappin_focused_on)
                    this.map = map
                }
            }
        }
        onDispose {
            lifecycle.removeObserver(observer)
            if (!destroyed) runCatching { mapView.onDestroy() }
        }
    }
    AndroidView(factory = { mapView }, modifier = modifier)
}

@Composable
internal fun StoreDetailEditSection(
    section: StoreDetailSectionModel.Edit,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        section.actionBars.forEach { action ->
            StoreDetailActionButton(action, { onAction(action) }, Modifier.weight(1f))
        }
    }
}

@Composable
internal fun StoreDetailVisitSection(section: StoreDetailSectionModel.Visit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(section.summary.title.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
            Text(section.summary.rating.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(20))
        }
        StoreDetailChipRow(section.history.items)
        section.history.moreText?.let {
            Text(it.text, color = Gray50, fontFamily = PretendardFontFamily, fontSize = dpToSp(12))
        }
    }
}

@Composable
internal fun StoreDetailAppearanceDaySection(
    section: StoreDetailSectionModel.AppearanceDay,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StoreDetailSectionHeader(
            title = section.header.title,
            subTitle = section.header.subTitle,
            trailingButton = section.header.trailingAction,
            onAction = onAction,
        )
        section.items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(item.leadingText.text, color = Pink, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
                Column(Modifier.weight(1f)) {
                    Text(item.primaryText.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
                    item.secondaryText?.let { Text(it.text, color = Gray50, fontFamily = PretendardFontFamily, fontSize = dpToSp(12)) }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailActionButton(
    action: StoreActionBarModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = action.button.text.text.contains("방문") || action.button.text.text.contains("발급")
    Box(
        modifier = modifier.height(44.dp).clip(RoundedCornerShape(12.dp))
            .background(if (primary) Pink else ColorWhite)
            .then(if (primary) Modifier else Modifier.border(BorderStroke(1.dp, Gray20), RoundedCornerShape(12.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = action.button.text.text,
            color = if (primary) ColorWhite else Gray100,
            fontFamily = PretendardFontFamily,
            fontSize = dpToSp(14),
        )
    }
}

@Composable
internal fun StoreDetailTextButton(
    button: SDButtonModel,
    onClick: () -> Unit,
) {
    Text(
        text = button.text.text,
        color = Pink,
        fontFamily = PretendardFontFamily,
        fontSize = dpToSp(14),
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(8.dp),
    )
}

@Composable
internal fun StoreDetailChipRow(chips: List<SDChipModel>) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        chips.forEach { chip ->
            Text(
                text = listOfNotNull(chip.text.text, chip.additionalText?.text).joinToString(" "),
                color = Gray70,
                fontFamily = PretendardFontFamily,
                fontSize = dpToSp(13),
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Gray10).padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
internal fun StoreDetailImage(
    image: SDImageModel,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    AsyncImage(
        model = image.url,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier.background(Gray10),
    )
}
