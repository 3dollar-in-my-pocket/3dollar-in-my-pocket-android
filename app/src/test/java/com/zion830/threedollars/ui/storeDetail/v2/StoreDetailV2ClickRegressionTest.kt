package com.zion830.threedollars.ui.storeDetail.v2

import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDHeaderModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.StoreActionBarModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationCategoryArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.storeCertificationCategories
import org.junit.Assert.assertEquals
import org.junit.Test

class StoreDetailV2ClickRegressionTest {
    @Test
    fun `consumed tab scrolls and logs once without opening another screen`() {
        val log = SDClickLogModel(screenName = "store_detail", objectType = "bar", objectId = "tab")
        val action = tab("/stores/120024#info").copy(clickLog = log)
        val events = mutableListOf<Any>()

        handleStoreDetailTabClick(action, infoSections(),
            onScrollToSection = { events += it },
            onAction = { error("An in-page tab must not launch an Activity") },
            onClickLog = { events += it },
        )

        assertEquals(listOf(log, 0), events)
    }

    @Test
    fun `button log is used when consumed tab has no outer log`() {
        val log = SDClickLogModel(screenName = "store_detail", objectType = "bar", objectId = "info")
        val original = tab("/stores/120024#info")
        val action = original.copy(button = original.button.copy(clickLog = log))
        val logs = mutableListOf<SDClickLogModel>()

        handleStoreDetailTabClick(action, infoSections(),
            onScrollToSection = { assertEquals(0, it) },
            onAction = { error("Unexpected platform action") },
            onClickLog = { logs += it },
        )

        assertEquals(listOf(log), logs)
    }

    @Test
    fun `missing local section does not delegate to an external screen`() {
        val action = tab("/stores/120024#reviews").copy(
            clickLog = SDClickLogModel(screenName = "store_detail", objectType = "bar", objectId = "tab"),
        )
        val actions = mutableListOf<StoreActionBarModel>()

        handleStoreDetailTabClick(action, infoSections(),
            onScrollToSection = { error("No review section exists") },
            onAction = { actions += it },
            onClickLog = { error("The normal action path owns this log") },
        )

        assertEquals(emptyList<StoreActionBarModel>(), actions)
    }

    @Test
    fun `deployed post tab scrolls to the post section`() {
        val post = StoreDetailSectionModel.Post("POST", SDHeaderModel(SDTextModel("소식", false)), emptyList())
        val sections = infoSections() + post
        val scrolled = mutableListOf<Int>()
        val delegated = mutableListOf<StoreActionBarModel>()
        handleStoreDetailTabClick(tab("/stores/120024#post"), sections,
            onScrollToSection = { scrolled += it },
            onAction = { delegated += it },
            onClickLog = {},
        )
        assertEquals(listOf(1), scrolled)
        assertEquals(emptyList<StoreActionBarModel>(), delegated)
    }

    @Test
    fun `certification categories exclude the rating chip and decode the category text`() {
        val primary = listOf(
            SDChipModel(text = SDTextModel("<span>계란빵, 와플</span>", true)),
            SDChipModel(image = SDImageModel("https://example.com/star.png"), text = SDTextModel("5.0 (2)", false)),
        )

        assertEquals(listOf(StoreCertificationCategoryArgs("계란빵, 와플")), primary.storeCertificationCategories())
        assertEquals(emptyList<StoreCertificationCategoryArgs>(), emptyList<SDChipModel>().storeCertificationCategories())
        assertEquals(emptyList<StoreCertificationCategoryArgs>(), listOf(SDChipModel(text = SDTextModel("", false))).storeCertificationCategories())
    }

    private fun infoSections() = listOf(
        StoreDetailSectionModel.InfoV1(
            type = "INFO_V1", header = SDHeaderModel(title = SDTextModel("가게 정보", false)),
            informationCard = null, menuCard = null,
        ),
    )

    private fun tab(link: String) = StoreActionBarModel(
        type = "ACTION_BAR",
        button = SDButtonModel(text = SDTextModel("tab", false), link = SDLinkModel("APP_SCHEME", link)),
    )
}
