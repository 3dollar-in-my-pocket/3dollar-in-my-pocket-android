package com.zion830.threedollars.ui.home.design

import android.content.ContentResolver
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import base.compose.AppTheme
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDActionBarModel
import com.threedollar.common.serverdriven.model.SDHeaderModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSectionModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDImpressionLogModel
import com.threedollar.common.serverdriven.model.StoreDetailAdMobCardModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.zion830.threedollars.R
import com.zion830.threedollars.core.ui.serverdriven.SDChipRenderer
import com.zion830.threedollars.core.ui.serverdriven.SDImageRenderer
import com.zion830.threedollars.core.ui.serverdriven.SDSectionRenderer
import com.zion830.threedollars.ui.storeDetail.v2.rememberStoreDetailAdMobStates
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailAdMobSection
import com.zion830.threedollars.ui.storeDetail.contributor.ui.BottomActionBar
import com.zion830.threedollars.ui.storeDetail.contributor.ui.StoreContributorActivity
import kotlin.math.roundToInt

/** Default deterministic renderer checks; the explicit ads scenario loads Google demo banners. */
class ServerDrivenContractPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra("scenario") == "contributor-error") {
            startActivity(StoreContributorActivity.getIntent(this, ""))
            finish()
            return
        }
        if (intent.getStringExtra("scenario") == "ads") {
            val section = StoreDetailSectionModel.AdMob("AD_MOB", listOf("first", "second").map { id ->
                StoreDetailAdMobCardModel(
                    id,
                    SDClickLogModel(screenName = "probe", objectType = "ad", objectId = id),
                    SDImpressionLogModel("IMPRESSION", "probe", "ad", id),
                )
            })
            setContent {
                AppTheme {
                    val states = rememberStoreDetailAdMobStates(listOf(section), { _, _ -> }, {})
                    val density = LocalDensity.current.density
                    var adSize by remember { mutableStateOf(IntSize.Zero) }
                    Column(Modifier.background(Color.White).padding(top = 64.dp)) {
                        Text("SDUI Google demo banners")
                        Box(Modifier.onGloballyPositioned { adSize = it.size }) {
                            StoreDetailAdMobSection(section, states)
                        }
                        Text("ad-height-dp:${(adSize.height / density).roundToInt()}")
                        states.forEach { (id, state) -> Text("ad-$id:${state.loadState}") }
                    }
                }
            }
            return
        }
        val icon = SDImageModel(
            url = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.drawable.sdui_contract_probe_icon}",
            style = SDImageStyleModel(16.0, 16.0),
        )
        setContent {
            AppTheme {
                val density = LocalDensity.current.density
                var imageSize by remember { mutableStateOf(IntSize.Zero) }
                var emptyChipSize by remember { mutableStateOf(IntSize.Zero) }
                var zeroImageSize by remember { mutableStateOf(IntSize(-1, -1)) }
                var headerAction by remember { mutableStateOf("none") }
                var contributorClicks by remember { mutableStateOf(emptyList<String>()) }
                val adStates = rememberStoreDetailAdMobStates(
                    sections = listOf(StoreDetailSectionModel.AdMob("AD_MOB", listOf("first", "second").map { id ->
                        StoreDetailAdMobCardModel(
                            id,
                            SDClickLogModel(screenName = "probe", objectType = "ad", objectId = id),
                            SDImpressionLogModel("IMPRESSION", "probe", "ad", id),
                        )
                    })),
                    onImpression = { _, _ -> },
                )
                Column(Modifier.background(Color.White).padding(top = 64.dp, start = 24.dp, end = 24.dp)) {
                    Text("SDUI contract preview")
                    Spacer(Modifier.height(16.dp))
                    SDChipRenderer(
                        chip = SDChipModel(text = SDTextModel("CHIP", false), image = icon, imageAlignment = "END", contentSpacing = 8.0),
                        modifier = Modifier.semantics { contentDescription = "probe-chip-end" },
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier.width(120.dp)) {
                        SDImageRenderer(
                            image = icon.copy(style = SDImageStyleModel(288.0, 180.0)),
                            modifier = Modifier.onGloballyPositioned { imageSize = it.size },
                        )
                    }
                    Text("image-dp:${(imageSize.width / density).roundToInt()}x${(imageSize.height / density).roundToInt()}")
                    Spacer(Modifier.height(16.dp))
                    SDChipRenderer(
                        chip = SDChipModel(text = SDTextModel("", false)),
                        modifier = Modifier.onGloballyPositioned { emptyChipSize = it.size },
                    )
                    Text("empty-chip-dp:${(emptyChipSize.width / density).roundToInt()}x${(emptyChipSize.height / density).roundToInt()}")
                    Spacer(Modifier.height(16.dp))
                    SDSectionRenderer(
                        section = SDSectionModel.HeaderSection(
                            type = "SCREEN_HEADER",
                            header = SDHeaderModel(
                                title = SDTextModel("HEADER TITLE", false),
                                subTitle = SDTextModel("HEADER SUBTITLE", false),
                                trailingAction = SDButtonModel(
                                    text = SDTextModel("HEADER MORE", false),
                                    link = SDLinkModel("APP_SCHEME", "/preview-test"),
                                ),
                            ),
                        ),
                        onAction = { headerAction = it.link },
                    )
                    Text("header-action:$headerAction")
                    Text("ad-states:${adStates.size}")
                    Box(Modifier.onGloballyPositioned { zeroImageSize = it.size }) {
                        SDImageRenderer(icon.copy(style = SDImageStyleModel(0.0, -1.0)))
                    }
                    Text("zero-image-dp:${(zeroImageSize.width / density).roundToInt()}x${(zeroImageSize.height / density).roundToInt()}")
                    BottomActionBar(
                        SDActionBarModel(
                            button = SDButtonModel(
                                text = SDTextModel("CONTRIBUTOR ACTION", false),
                                link = SDLinkModel("APP_SCHEME", "/storeUpdate?storeId=1"),
                                clickLog = SDClickLogModel(screenName = "probe", objectType = "button", objectId = "inner"),
                            ),
                            clickLog = SDClickLogModel(screenName = "probe", objectType = "button", objectId = "outer"),
                        ),
                        onButtonAction = { contributorClicks = contributorClicks + it.clickLog!!.objectId },
                    )
                    Text("contributor-clicks:${contributorClicks.joinToString()}")
                }
            }
        }
    }
}
