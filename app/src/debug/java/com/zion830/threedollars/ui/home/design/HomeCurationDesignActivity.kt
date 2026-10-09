package com.zion830.threedollars.ui.home.design

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import base.compose.AppTheme
import base.compose.Gray10
import base.compose.Gray20
import base.compose.Gray60
import base.compose.Gray100
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDAdMobCardModel
import com.threedollar.common.sdui.model.component.SDUnknownCardModel
import com.threedollar.common.sdui.model.element.SDChipModel
import com.threedollar.common.sdui.model.section.home.SDHomeBottomSheetTabsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationCardsModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationItemModel
import com.threedollar.common.sdui.model.section.home.SDHomeCurationSectionModel
import com.zion830.threedollars.R
import com.zion830.threedollars.core.ui.sdui.section.home.SDHomeCurationView
import com.zion830.threedollars.core.ui.sdui.preview.HomeCurationPreviewFixtures

/**
 * TH-1401의 UI 확인용 debug 진입점. 서버 조회·내비게이션·로그 전송은 연결하지 않는다.
 * `scenario` intent extra로 화면 입력을 선택하며 기본값은 Figma Preview fixture다.
 */
class HomeCurationDesignActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()

        val scenario = intent.getStringExtra(EXTRA_SCENARIO) ?: SCENARIO_DEFAULT
        val fixture = designFixture(scenario)
        val tabs = when (scenario) {
            "empty_tabs" -> SDHomeBottomSheetTabsModel()
            "unknown_tabs" -> SDHomeBottomSheetTabsModel(
                HomeCurationPreviewFixtures.tabs.tabs.map { it.copy(viewType = "DESIGN_UNKNOWN_VIEW") },
            )
            else -> HomeCurationPreviewFixtures.tabs
        }
        val initialTabId = tabs.tabs.firstOrNull { it.defaultSelected }?.tabId ?: tabs.tabs.firstOrNull()?.tabId

        setContent {
            AppTheme {
                var selectedTabId by rememberSaveable { mutableStateOf(initialTabId) }
                var selectedCategoryIds by rememberSaveable(stateSaver = CategorySelectionsSaver) {
                    mutableStateOf<Map<String, String>>(emptyMap())
                }

                Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    SheetHandle()
                    SDHomeCurationView(
                        tabs = tabs,
                        selectedTabId = selectedTabId,
                        section = fixture.section,
                        cardsByCarousel = fixture.cardsByCarousel,
                        selectedCategoryIds = selectedCategoryIds,
                        onTabClick = { tab ->
                            selectedTabId = tab.tabId
                            Log.d(TAG, "tab=${tab.tabId}")
                        },
                        onCategoryClick = { carousel, category ->
                            selectedCategoryIds = selectedCategoryIds + (carousel.carouselId to category.categoryId)
                            Log.d(TAG, "carousel=${carousel.carouselId}, category=${category.categoryId}")
                        },
                        onCardClick = { card ->
                            Log.d(TAG, "card=${card.cardId}")
                            Toast.makeText(this@HomeCurationDesignActivity, "UI 확인용 카드 클릭: ${card.cardId}", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        nearbyContent = { NearbyDesignFixture() },
                        adMobContent = { AdMobDesignFixture() },
                        cardImageContentScale = { card ->
                            if ((card.image?.style?.width ?: 100f) < 100f) ContentScale.Fit else ContentScale.Crop
                        },
                    )
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        const val EXTRA_SCENARIO = "scenario"
        private const val TAG = "HomeCurationDesign"
        private const val SCENARIO_DEFAULT = "default"
    }
}

private data class HomeCurationDesignFixture(
    val section: SDHomeCurationSectionModel,
    val cardsByCarousel: Map<String, SDHomeCurationCardsModel>,
)

private val CategorySelectionsSaver = mapSaver<Map<String, String>>(
    save = { it },
    restore = { values -> values.mapValues { (_, value) -> value as String } },
)

private fun Context.designFixture(scenario: String): HomeCurationDesignFixture {
    val section = HomeCurationPreviewFixtures.section
    val cards = HomeCurationPreviewFixtures.cardsByCarousel.mapValues { (_, value) ->
        value.copy(cards = value.cards.mapIndexed { index, card ->
            val imageResource = when (index) {
                0 -> R.drawable.design_curation_image_1
                1 -> R.drawable.design_curation_image_2
                else -> R.drawable.design_curation_placeholder
            }
            if (card is ImagePreviewCardModel) {
                card.copy(
                    image = card.image?.copy(url = drawableUri(imageResource)),
                    metricLabel = localChips(card.metricLabel, listOf(R.drawable.design_curation_location, R.drawable.design_curation_review)),
                    contextLabel = localChips(card.contextLabel, listOf(R.drawable.design_curation_star)),
                )
            } else card
        })
    }
    return when (scenario) {
        "empty", "empty_sections" -> HomeCurationDesignFixture(section.copy(items = emptyList()), emptyMap())
        "empty_cards" -> HomeCurationDesignFixture(section, cards.mapValues { (_, value) -> value.copy(cards = emptyList()) })
        "admob" -> HomeCurationDesignFixture(
            section.copy(items = section.items + SDHomeCurationItemModel.AdMob(SDAdMobCardModel(cardId = "design-section-ad", height = 80))),
            cards.mapValues { (carouselId, _) ->
                SDHomeCurationCardsModel(cards = listOf(SDAdMobCardModel(cardId = "design-$carouselId-ad", height = 100)))
            },
        )
        "unknown" -> HomeCurationDesignFixture(
            section.copy(items = section.items + SDHomeCurationItemModel.Unknown(rawType = "DESIGN_UNKNOWN_SECTION")),
            cards.mapValues { (carouselId, value) ->
                value.copy(cards = value.cards + SDUnknownCardModel(cardId = "design-$carouselId-unknown"))
            },
        )
        "mixed" -> HomeCurationDesignFixture(
            section.copy(items = listOf(SDHomeCurationItemModel.Unknown(rawType = "DESIGN_UNKNOWN_SECTION")) +
                section.items.flatMapIndexed { index, item ->
                    if (index == 0) listOf(item, SDHomeCurationItemModel.AdMob(SDAdMobCardModel(cardId = "design-section-ad", height = 80))) else listOf(item)
                }),
            cards.mapValues { (carouselId, value) ->
                value.copy(cards = listOf(SDUnknownCardModel(cardId = "design-$carouselId-unknown")) +
                    value.cards.flatMapIndexed { index, card ->
                        if (index == 0) listOf(card, SDAdMobCardModel(cardId = "design-$carouselId-ad", height = 100)) else listOf(card)
                    })
            },
        )
        "long_text" -> HomeCurationDesignFixture(
            section.copy(items = section.items.map { item ->
                if (item is SDHomeCurationItemModel.Carousel) {
                    item.copy(header = item.header?.let { header ->
                        header.copy(title = header.title?.copy(
                            text = "<span style=\"font-size:20px; font-weight:700\">아주 긴 큐레이션 제목으로 작은 화면에서도 레이아웃 확인하기</span>",
                            isHtml = true,
                        ))
                    })
                } else item
            }),
            cards.mapValues { (_, value) ->
                value.copy(cards = value.cards.map { card ->
                    if (card is ImagePreviewCardModel) {
                        card.copy(title = card.title?.copy(text = "아주 긴 이름을 가진 UI 확인용 붕어빵 가게", isHtml = false))
                    } else card
                })
            },
        )
        else -> HomeCurationDesignFixture(section, cards)
    }
}

private fun Context.localChips(chips: List<SDChipModel>?, resources: List<Int>): List<SDChipModel>? =
    chips?.mapIndexed { index, chip ->
        val resource = resources.getOrNull(index)
        if (resource != null) chip.copy(image = chip.image?.copy(url = drawableUri(resource))) else chip
    }

private fun Context.drawableUri(@DrawableRes resource: Int): String = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .appendPath(resource.toString())
    .build()
    .toString()

@Composable
private fun SheetHandle() {
    Box(modifier = Modifier.fillMaxWidth().height(12.dp), contentAlignment = Alignment.BottomCenter) {
        Box(modifier = Modifier.size(width = 40.dp, height = 5.dp).background(Gray20, RoundedCornerShape(2.dp)))
    }
}

@Composable
private fun AdMobDesignFixture() {
    Box(modifier = Modifier.fillMaxSize().background(Gray10), contentAlignment = Alignment.Center) {
        Text(text = "AdMob · UI slot", color = Gray60)
    }
}

@Composable
private fun NearbyDesignFixture() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "주변 가게 UI 슬롯", color = Gray100)
        Text(text = "UI 확인용 fixture · 주변 가게 API 미연동", color = Gray60)
    }
}
