package com.zion830.threedollars.ui.home.design

import android.content.ContentResolver
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import base.compose.AppTheme
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.threedollar.common.serverdriven.model.HomeListSectionModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.serverdriven.model.StoreScreenModel
import com.threedollar.common.serverdriven.model.StoreSectionModel
import com.threedollar.data.screen.asStoreDetailModelOrNull
import com.threedollar.network.data.screen.StoreDetailScreenResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.home.ui.compose.HomeBottomSheetContent
import com.zion830.threedollars.ui.storeDetail.v2.StoreDetailV2Content

/** Renders the checked-in anonymous server contracts with real production composables. */
class ServerDrivenStoreFixtureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val file = when (intent.getStringExtra("fixture")) {
            "boss" -> "prod-store-106775-anonymized.json"
            "verified-user" -> "prod-store-121173-anonymized.json"
            "verified-boss" -> "prod-store-525611-anonymized.json"
            "all-types" -> "source-derived-all-types.json"
            else -> "prod-store-120024-anonymized.json"
        }
        val root = assets.open("store-detail/$file").bufferedReader().use { JsonParser.parseReader(it) }.asJsonObject
        val data = root.getAsJsonObject("data") ?: root
        replaceFixtureImages(data)
        val baseScreen = requireNotNull(Gson().fromJson(data, StoreDetailScreenResponse::class.java).asStoreDetailModelOrNull())
        val postScreen = if (intent.getBooleanExtra("include_post", false)) {
            val sourceContract = assets.open("store-detail/source-derived-all-types.json").bufferedReader().use {
                Gson().fromJson(it, StoreDetailScreenResponse::class.java).asStoreDetailModelOrNull()
            }
            val post = requireNotNull(sourceContract).sections.filterIsInstance<StoreDetailSectionModel.Post>().single()
            val imageIndex = baseScreen.sections.indexOfFirst { it is StoreDetailSectionModel.Image }
                .takeIf { it >= 0 } ?: baseScreen.sections.size
            baseScreen.copy(sections = baseScreen.sections.toMutableList().apply { add(imageIndex, post) })
        } else baseScreen
        val screen = if (intent.getBooleanExtra("include_reply", false)) {
            val replyScreen = assets.open("store-detail/source-derived-review-reply.json").bufferedReader().use {
                Gson().fromJson(it, StoreDetailScreenResponse::class.java).asStoreDetailModelOrNull()
            }
            val review = requireNotNull(replyScreen).sections.single() as StoreDetailSectionModel.Review
            postScreen.copy(sections = postScreen.sections.map { if (it is StoreDetailSectionModel.Review) review else it })
        } else postScreen
        val preview = screen.sections.filterIsInstance<StoreDetailSectionModel.Preview>().firstOrNull()
        val homeScreen = preview?.let {
            StoreScreenModel(listOf(StoreSectionModel.Preview(
                type = "PREVIEW",
                header = it.header,
                metadata = it.metadata,
                additionalInfos = it.additionalInfos,
                actionBars = it.actionBars,
                images = it.images,
                bodies = it.bodies.map { body -> body.text.copy(style = body.style) },
                style = it.style,
            )))
        }
        val homeContainer = intent.getStringExtra("container") == "home"
        setContent {
            AppTheme {
                if (homeContainer && homeScreen != null) {
                    HomeBottomSheetContent(
                        homeListSection = HomeListSectionModel(),
                        storeScreen = homeScreen,
                        storeDetailScreen = screen,
                        selectedStoreExpanded = true,
                        onCardClick = {}, onLoadNextPage = {}, onClosePreview = {}, onActionClick = {},
                        fullListTopPx = 0,
                        modifier = Modifier.fillMaxSize().systemBarsPadding(),
                    )
                } else {
                    StoreDetailV2Content(screen, onAction = {}, onClickLog = {}, modifier = Modifier.fillMaxSize().systemBarsPadding())
                }
            }
        }
    }

    private fun replaceFixtureImages(element: JsonElement) {
        when {
            element.isJsonArray -> element.asJsonArray.forEach(::replaceFixtureImages)
            element.isJsonObject -> {
                val node = element.asJsonObject
                val url = node.get("url")?.takeIf { it.isJsonPrimitive }?.asString
                if (url != null && !url.startsWith("https://storage.threedollars.co.kr/")) {
                    node.addProperty("url", "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.drawable.design_store_preview_image_1}")
                }
                node.entrySet().forEach { replaceFixtureImages(it.value) }
            }
        }
    }
}
