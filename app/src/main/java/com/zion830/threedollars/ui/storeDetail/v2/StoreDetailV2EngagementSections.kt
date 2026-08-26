package com.zion830.threedollars.ui.storeDetail.v2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray20
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray100
import base.compose.Pink
import base.compose.PretendardFontFamily
import base.compose.dpToSp
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.R as CommonR

@Composable
internal fun StoreDetailCouponSection(
    section: StoreDetailSectionModel.Coupon,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        section.header?.let { StoreDetailSectionHeader(it.title, it.subTitle, it.trailingAction, onAction) }
        section.cards.forEach { card ->
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Gray10).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    card.badge?.let { Text(it.text.text, color = Pink, fontFamily = PretendardFontFamily, fontSize = dpToSp(11)) }
                    Text(card.title.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(15))
                    Text(card.subTitle.text, color = Gray50, fontFamily = PretendardFontFamily, fontSize = dpToSp(12))
                }
                StoreDetailTextButton(card.trailingButton) {
                    onAction(StoreActionBarModel(type = "COUPON", button = card.trailingButton, clickLog = card.clickLog))
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailPostSection(
    section: StoreDetailSectionModel.Post,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        section.cards.forEach { card ->
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Gray10)
                    .then(
                        card.link?.let { link ->
                            Modifier.clickable {
                                onAction(
                                    StoreActionBarModel(
                                        type = "POST_CARD",
                                        button = SDButtonModel(text = card.body, link = link),
                                        clickLog = card.clickLog,
                                    )
                                )
                            }
                        } ?: Modifier
                    )
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(card.header.text.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(13))
                if (card.images.isNotEmpty()) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        card.images.forEach { image ->
                            StoreDetailImage(image, Modifier.size(160.dp).clip(RoundedCornerShape(8.dp)), ContentScale.Crop)
                        }
                    }
                }
                Text(card.body.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
                card.like?.let { like ->
                    val button = if (like.isSelected) like.selected else like.unselected
                    StoreDetailTextButton(button) { onAction(syntheticActionBar(button, "POST_LIKE")) }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailImageSection(
    section: StoreDetailSectionModel.Image,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            section.cards.forEach { card ->
                Column(
                    modifier = Modifier.size(width = 132.dp, height = 168.dp).clip(RoundedCornerShape(10.dp)).background(Gray10)
                        .clickable {
                            onAction(
                                StoreActionBarModel(
                                    type = "IMAGE_CARD",
                                    button = SDButtonModel(
                                        text = card.title ?: SDTextModel(text = "", isHtml = false),
                                        link = card.link,
                                        customAction = if (card.link == null) card.customAction else null,
                                    ),
                                    clickLog = card.clickLog,
                                )
                            )
                        },
                ) {
                    StoreDetailImage(card.image, Modifier.fillMaxWidth().height(132.dp), ContentScale.Crop)
                    card.title?.let { Text(it.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(12), modifier = Modifier.padding(8.dp)) }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailRelatedStoresSection(
    section: StoreDetailSectionModel.RelatedStores,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            section.cards.forEach { card ->
                Column(
                    modifier = Modifier.size(width = 176.dp, height = 220.dp).clip(RoundedCornerShape(12.dp)).background(Gray10)
                        .clickable {
                            card.link?.let { link ->
                                onAction(
                                    StoreActionBarModel(
                                        type = "RELATED_STORE",
                                        button = SDButtonModel(text = card.title, link = link),
                                        clickLog = card.clickLog,
                                    )
                                )
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StoreDetailImage(card.image, Modifier.fillMaxWidth().height(136.dp), ContentScale.Crop)
                    Text(card.title.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(14), modifier = Modifier.padding(horizontal = 10.dp))
                    Column(Modifier.padding(horizontal = 10.dp)) {
                        StoreDetailChipRow(card.metricLabel)
                    }
                }
            }
        }
    }
}

@Composable
internal fun StoreDetailReviewSection(
    section: StoreDetailSectionModel.Review,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StoreDetailSectionHeader(section.header.title, section.header.subTitle, section.header.trailingAction, onAction)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(section.summary.title.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
            Text(section.summary.rating.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(22))
        }
        section.cards.forEach { card ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    .then(card.link?.let { link -> Modifier.clickable {
                        onAction(
                            StoreActionBarModel(
                                type = "REVIEW_CARD",
                                button = SDButtonModel(text = card.body, link = link),
                                clickLog = card.clickLog,
                            )
                        )
                    } } ?: Modifier),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StoreDetailSectionHeader(card.header.title, card.header.subTitle, card.header.trailingAction, onAction)
                StoreDetailChipRow(card.metadata)
                if (card.images.isNotEmpty()) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        card.images.forEach { StoreDetailImage(it, Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), ContentScale.Crop) }
                    }
                }
                Text(card.body.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(14))
                card.like?.let { like ->
                    val button = if (like.isSelected) like.selected else like.unselected
                    StoreDetailTextButton(button) { onAction(syntheticActionBar(button, "REVIEW_LIKE")) }
                }
                card.reply?.let { reply ->
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Gray10).padding(12.dp)) {
                        Text(reply.header.title.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(13))
                        Text(reply.body.text, color = Gray70, fontFamily = PretendardFontFamily, fontSize = dpToSp(13))
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Gray20))
        }
        section.more?.let { action -> StoreDetailActionButton(action, { onAction(action) }, Modifier.fillMaxWidth()) }
    }
}

@Composable
internal fun StoreDetailCtaSection(
    section: StoreDetailSectionModel.Cta,
    onAction: (StoreActionBarModel) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(section.content.title.text, color = Gray100, fontFamily = PretendardFontFamily, fontSize = dpToSp(15))
            section.content.subTitle?.let { Text(it.text, color = Gray50, fontFamily = PretendardFontFamily, fontSize = dpToSp(12)) }
        }
        section.content.footerLeftButton?.let { button ->
            StoreDetailTextButton(button) { onAction(syntheticActionBar(button, "CTA")) }
        }
    }
}

@Composable
internal fun StoreDetailAdMobSection(section: StoreDetailSectionModel.AdMob) {
    val card = section.cards.firstOrNull() ?: return
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val adView = remember(card.cardId, context) {
        runCatching { AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = context.getString(CommonR.string.admob_list_banner)
            adListener = object : AdListener() {
                override fun onAdClicked() {
                    SDClickLogger.send(card.clickLog)
                }

                override fun onAdImpression() {
                    SDClickLogger.send(card.impressionLog)
                }
            }
            loadAd(AdRequest.Builder().build())
        } }.getOrNull()
    } ?: return
    DisposableEffect(adView, lifecycle) {
        var destroyed = false
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> runCatching { adView.resume() }
                Lifecycle.Event.ON_PAUSE -> runCatching { adView.pause() }
                Lifecycle.Event.ON_DESTROY -> {
                    runCatching { adView.destroy() }
                    destroyed = true
                }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            if (!destroyed) runCatching { adView.destroy() }
        }
    }
    AndroidView(
        factory = { adView },
        modifier = Modifier.fillMaxWidth().height(72.dp).background(ColorWhite).padding(vertical = 8.dp),
    )
}
