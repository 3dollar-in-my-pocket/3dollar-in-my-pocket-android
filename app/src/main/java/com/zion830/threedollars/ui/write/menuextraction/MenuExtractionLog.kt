package com.zion830.threedollars.ui.write.menuextraction

import com.threedollar.common.analytics.ClickEvent
import com.threedollar.common.analytics.CustomEvent
import com.threedollar.common.analytics.EventName
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName

enum class MenuExtractionFlow(val value: String) {
    CREATE("create"),
    EDIT("edit"),
    AFTER_CREATE("after_create"),
}

object MenuExtractionLog {
    const val WRITE_DETAIL_COMPLETE_CLASS_NAME = "CompletionScreen"

    fun writeDetailCompleteParameters(menuExtractionUsed: Boolean): Map<ParameterName, Any> =
        mapOf(ParameterName.MENU_EXTRACTION_USED to menuExtractionUsed.toString())

    fun editStoreCompleteEvent(
        menuExtractionUsed: Boolean,
        editedMenu: Boolean,
        flow: MenuExtractionFlow,
    ): CustomEvent = CustomEvent(
        screen = ScreenName.EDIT_STORE,
        name = EventName.EDIT_STORE_COMPLETE,
        extraParameters = mapOf(
            ParameterName.MENU_EXTRACTION_USED to menuExtractionUsed.toString(),
            ParameterName.EDITED_MENU to editedMenu.toString(),
            ParameterName.FLOW to flow.value,
        ),
    )

    fun registerClickEvent(state: MenuExtractionState, flow: MenuExtractionFlow): ClickEvent = ClickEvent(
        screen = ScreenName.WRITE_DETAIL_MENU_EXTRACTION_RESULT,
        objectType = LogObjectType.BUTTON,
        objectId = LogObjectId.REGISTER,
        additionalParams = mapOf(
            ParameterName.RECOGNIZED_MENU_COUNT to state.recognizedMenuCount,
            ParameterName.FINAL_MENU_COUNT to state.categories.limitToMaxCategories().filledMenuCount(),
            ParameterName.FLOW to flow.value,
        ),
    )
}
