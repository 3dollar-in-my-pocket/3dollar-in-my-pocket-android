package com.zion830.threedollars.ui.storeDetail.v2

import android.content.Context
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import base.compose.Gray50
import base.compose.Gray70
import base.compose.Gray100
import base.compose.Pink
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.threedollar.common.analytics.SDClickLogger
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailAdMobCardModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.zion830.threedollars.core.ui.serverdriven.SDChipRenderer
import com.zion830.threedollars.core.ui.serverdriven.SDTextRenderer
import com.zion830.threedollars.core.ui.serverdriven.serverDrivenSurface
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
                modifier = Modifier
                    .fillMaxWidth()
                    .serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    card.badge?.let { SDChipRenderer(it) }
                    SDTextRenderer(card.title, color = Gray100, fontSizeDp = 15, lineHeightDp = 21)
                    SDTextRenderer(card.subTitle, color = Gray50, fontSizeDp = 12, lineHeightDp = 18)
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
                modifier = Modifier.fillMaxWidth().serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
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
                SDChipRenderer(card.header)
                if (card.images.isNotEmpty()) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        card.images.forEach { image ->
                            StoreDetailImage(image, Modifier.size(160.dp).clip(RoundedCornerShape(8.dp)), ContentScale.Crop)
                        }
                    }
                }
                SDTextRenderer(card.body, color = Gray70, fontSizeDp = 14, lineHeightDp = 20)
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
                    modifier = Modifier
                        .size(width = 132.dp, height = 196.dp)
                        .serverDrivenSurface(card.style, RoundedCornerShape(10.dp), Gray10)
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
                    card.title?.let {
                        SDTextRenderer(it, color = Gray100, fontSizeDp = 12, lineHeightDp = 18, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                    card.subTitle?.let {
                        SDTextRenderer(it, color = Gray50, fontSizeDp = 11, lineHeightDp = 17, modifier = Modifier.padding(horizontal = 8.dp))
                    }
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
                    modifier = Modifier
                        .size(width = 176.dp, height = 248.dp)
                        .serverDrivenSurface(card.style, RoundedCornerShape(12.dp), Gray10)
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
                    SDTextRenderer(card.title, color = Gray100, fontSizeDp = 14, lineHeightDp = 20, modifier = Modifier.padding(horizontal = 10.dp))
                    Column(Modifier.padding(horizontal = 10.dp)) {
                        StoreDetailChipRow(card.metricLabel)
                        StoreDetailChipRow(card.contextLabel)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .serverDrivenSurface(section.summary.style, RoundedCornerShape(12.dp), ColorWhite)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SDTextRenderer(section.summary.title, color = Gray70, fontSizeDp = 14, lineHeightDp = 20)
            StoreDetailRating(section.summary.stars)
            SDTextRenderer(section.summary.rating, color = Gray100, fontSizeDp = 22, lineHeightDp = 30)
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
                StoreDetailRating(card.stars)
                StoreDetailChipRow(card.metadata)
                if (card.images.isNotEmpty()) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        card.images.forEach { StoreDetailImage(it, Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), ContentScale.Crop) }
                    }
                }
                SDTextRenderer(card.body, color = Gray70, fontSizeDp = 14, lineHeightDp = 20)
                card.like?.let { like ->
                    val button = if (like.isSelected) like.selected else like.unselected
                    StoreDetailTextButton(button) { onAction(syntheticActionBar(button, "REVIEW_LIKE")) }
                }
                card.reply?.let { reply ->
                    Column(Modifier.fillMaxWidth().serverDrivenSurface(reply.style, RoundedCornerShape(10.dp), Gray10).padding(12.dp)) {
                        SDTextRenderer(reply.header.title, color = Gray100, fontSizeDp = 13, lineHeightDp = 19)
                        SDTextRenderer(reply.body, color = Gray70, fontSizeDp = 13, lineHeightDp = 19)
                    }
                }
            }
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
            SDTextRenderer(section.content.title, color = Gray100, fontSizeDp = 15, lineHeightDp = 21)
            section.content.subTitle?.let { SDTextRenderer(it, color = Gray50, fontSizeDp = 12, lineHeightDp = 18) }
        }
        section.content.footerLeftButton?.let { button ->
            StoreDetailTextButton(button) { onAction(syntheticActionBar(button, "CTA")) }
        }
    }
}

@Composable
internal fun rememberStoreDetailAdMobStates(
    sections: List<StoreDetailSectionModel>,
    onImpression: (String, SDImpressionLogModel) -> Unit,
): Map<String, StoreDetailAdMobState> {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val cards = sections
        .filterIsInstance<StoreDetailSectionModel.AdMob>()
        .mapNotNull { it.cards.firstOrNull() }
    val storeId = sections
        .filterIsInstance<StoreDetailSectionModel.Preview>()
        .firstOrNull()
        ?.additionalInfos
        ?.storeId
    val cardIds = cards.map(StoreDetailAdMobCardModel::cardId)
    val adStates = remember(context, storeId, cardIds) {
        cards.associate { card ->
            card.cardId to StoreDetailAdMobState(
                card = card,
                onImpression = onImpression,
            )
        }
    }

    SideEffect {
        cards.forEach { card -> adStates[card.cardId]?.update(card, onImpression) }
    }

    DisposableEffect(adStates, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adStates.values.forEach { it.resume() }
                Lifecycle.Event.ON_PAUSE -> adStates.values.forEach { it.pause() }
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            adStates.values.forEach { it.destroy() }
        }
    }

    return adStates
}

@Composable
internal fun StoreDetailAdMobSection(
    section: StoreDetailSectionModel.AdMob,
    adState: StoreDetailAdMobState?,
) {
    val card = section.cards.firstOrNull() ?: return
    val state = adState?.takeIf { it.cardId == card.cardId } ?: return
    if (!state.loadState.shouldRender) return
    AndroidView(
        factory = { context ->
            state.obtain(context).also { adView ->
                (adView.parent as? ViewGroup)?.removeView(adView)
            }
        },
        modifier = Modifier.fillMaxWidth().height(72.dp).background(ColorWhite).padding(vertical = 8.dp),
        onReset = { reusableAdView ->
            reusableAdView.resume()
        },
        onRelease = { releasedAdView ->
            releasedAdView.pause()
        },
        update = { attachedAdView -> attachedAdView.resume() },
    )
}

internal class StoreDetailAdMobState(
    card: StoreDetailAdMobCardModel,
    onImpression: (String, SDImpressionLogModel) -> Unit,
) {
    var loadState by mutableStateOf(StoreDetailAdLoadState.Loading)
        private set

    val cardId: String = card.cardId
    private var currentCard = card
    private var currentOnImpression = onImpression
    private var isDestroyed = false
    private var managedAdView: AdView? = null

    fun obtain(context: Context): AdView = managedAdView ?: AdView(context).apply {
        setAdSize(AdSize.BANNER)
        adUnitId = context.getString(CommonR.string.admob_list_banner)
        adListener = object : AdListener() {
            override fun onAdLoaded() {
                loadState = StoreDetailAdLoadState.Loaded
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loadState = loadState.afterLoadFailure()
            }

            override fun onAdClicked() {
                runCatching { SDClickLogger.send(currentCard.clickLog) }
            }

            override fun onAdImpression() {
                currentOnImpression("AD_MOB:$cardId", currentCard.impressionLog)
            }
        }
        loadAd(AdRequest.Builder().build())
    }.also { managedAdView = it }

    fun update(
        card: StoreDetailAdMobCardModel,
        onImpression: (String, SDImpressionLogModel) -> Unit,
    ) {
        currentCard = card
        currentOnImpression = onImpression
    }

    fun pause() {
        if (!isDestroyed) runCatching { managedAdView?.pause() }
    }

    fun resume() {
        if (!isDestroyed) runCatching { managedAdView?.resume() }
    }

    fun destroy() {
        if (isDestroyed) return
        isDestroyed = true
        runCatching { managedAdView?.destroy() }
        managedAdView = null
    }
}
