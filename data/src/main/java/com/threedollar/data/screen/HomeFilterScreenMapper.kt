package com.threedollar.data.screen

import com.google.gson.JsonElement
import com.threedollar.common.serverdriven.model.HomeFilterBar
import com.threedollar.common.serverdriven.model.HomeFilterBarType
import com.threedollar.common.serverdriven.model.HomeFilterConfiguration
import com.threedollar.common.serverdriven.model.HomeFilterCurrentCategory
import com.threedollar.common.serverdriven.model.HomeFilterRadioOption
import com.threedollar.common.serverdriven.model.HomeFilterScreenModel
import com.threedollar.common.serverdriven.model.HomeScreenSection
import com.threedollar.common.serverdriven.model.HomeScreenSectionType
import com.threedollar.common.serverdriven.model.SDBorderModel
import com.threedollar.common.serverdriven.model.SDButtonModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDClickLogModel
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDImageModel
import com.threedollar.common.serverdriven.model.SDImageStyleModel
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.serverdriven.model.SDSurfaceStyleModel
import com.threedollar.common.serverdriven.model.SDTextModel
import com.threedollar.common.serverdriven.model.SDViewLogModel
import com.threedollar.network.data.screen.HomeFilterBarResponse
import com.threedollar.network.data.screen.HomeFilterBorderResponse
import com.threedollar.network.data.screen.HomeFilterButtonResponse
import com.threedollar.network.data.screen.HomeFilterChipResponse
import com.threedollar.network.data.screen.HomeFilterClickLogResponse
import com.threedollar.network.data.screen.HomeFilterConfigurationResponse
import com.threedollar.network.data.screen.HomeFilterCurrentCategoryResponse
import com.threedollar.network.data.screen.HomeFilterImageResponse
import com.threedollar.network.data.screen.HomeFilterImageStyleResponse
import com.threedollar.network.data.screen.HomeFilterLinkResponse
import com.threedollar.network.data.screen.HomeFilterRadioOptionResponse
import com.threedollar.network.data.screen.HomeFilterScreenResponse
import com.threedollar.network.data.screen.HomeFilterSectionResponse
import com.threedollar.network.data.screen.HomeFilterSurfaceStyleResponse
import com.threedollar.network.data.screen.HomeFilterTextResponse
import com.threedollar.network.data.screen.HomeFilterViewLogResponse

fun HomeFilterScreenResponse.asModel(): HomeFilterScreenModel = HomeFilterScreenModel(
    sections = sections.orEmpty().map { it.asModel() },
    configuration = configuration?.asModel(),
    viewLog = viewLog?.asModelOrNull(),
)

private fun HomeFilterConfigurationResponse.asModel(): HomeFilterConfiguration = HomeFilterConfiguration(
    initialMapZoomLevel = initialMapZoomLevel,
)

private fun HomeFilterSectionResponse.asModel(): HomeScreenSection {
    val sectionType = HomeScreenSectionType.fromRaw(type)
    return when (sectionType) {
        HomeScreenSectionType.HOME_FILTER -> HomeScreenSection.HomeFilterSectionModel(
            type = sectionType,
            bars = bars.orEmpty().mapNotNull { it.asModelOrNull() },
        )

        HomeScreenSectionType.UNKNOWN -> HomeScreenSection.Unknown(type = sectionType)
    }
}

private fun HomeFilterBarResponse.asModelOrNull(): HomeFilterBar? {
    val barType = HomeFilterBarType.fromRaw(type)
    return when (barType) {
        HomeFilterBarType.CATEGORY_BAR -> {
            val chip = categoriesFilter?.asModel() ?: return null
            HomeFilterBar.CategoryBar(
                type = barType,
                categoriesFilter = chip,
                categoriesFilterClickLog = categoriesFilterClickLog?.asModel(),
                currentCategoryFilter = currentCategoryFilter?.asModel(),
            )
        }

        HomeFilterBarType.RADIO_BAR -> {
            val key = paramKey ?: return null
            HomeFilterBar.RadioBar(
                type = barType,
                paramKey = key,
                options = options.orEmpty().mapNotNull { it.asModelOrNull() },
            )
        }

        HomeFilterBarType.ACTION_BAR -> {
            val buttonModel = button?.asModel() ?: return null
            HomeFilterBar.ActionBar(
                type = barType,
                button = buttonModel,
                clickLog = clickLog?.asModel(),
            )
        }

        HomeFilterBarType.UNKNOWN -> null
    }
}

private fun HomeFilterRadioOptionResponse.asModelOrNull(): HomeFilterRadioOption? {
    val chipModel = chip?.asModel() ?: return null
    return HomeFilterRadioOption(
        chip = chipModel,
        paramValue = paramValue,
        clickLog = clickLog?.asModel(),
    )
}

private fun HomeFilterChipResponse.asModel(): SDChipModel = SDChipModel(
    image = image?.takeIf { !it.url.isNullOrBlank() }?.asModel(),
    text = text.asModel(),
    additionalText = additionalText?.asModel(),
    style = style?.let { SDSurfaceStyleModel(backgroundColor = it.backgroundColor, border = it.border?.asModel()) },
)

private fun HomeFilterButtonResponse.asModel(): SDButtonModel = SDButtonModel(
    text = text.asModel(),
    image = image?.takeIf { !it.url.isNullOrBlank() }?.asModel(),
    link = link?.takeIf { !it.link.isNullOrBlank() }?.asModel(),
    style = style?.let { SDSurfaceStyleModel(backgroundColor = it.backgroundColor, border = it.border?.asModel()) },
)

private fun HomeFilterCurrentCategoryResponse.asModel(): HomeFilterCurrentCategory = HomeFilterCurrentCategory(
    fontColor = fontColor,
    style = style?.asModel(),
    clickLog = clickLog?.asModel(),
)

private fun HomeFilterTextResponse?.asModel(): SDTextModel = SDTextModel(
    text = this?.text.orEmpty(),
    isHtml = this?.isHtml ?: false,
    fontColor = this?.fontColor,
)

private fun HomeFilterImageResponse.asModel(): SDImageModel = SDImageModel(
    url = url.orEmpty(),
    style = style?.asModel(),
)

private fun HomeFilterImageStyleResponse.asModel(): SDImageStyleModel = SDImageStyleModel(
    width = width,
    height = height,
)

private fun HomeFilterLinkResponse.asModel(): SDLinkModel = SDLinkModel(
    type = type.orEmpty(),
    link = link.orEmpty(),
)

private fun HomeFilterSurfaceStyleResponse.asModel(): SDSurfaceStyleModel = SDSurfaceStyleModel(
    backgroundColor = backgroundColor,
    border = border?.asModel(),
)

private fun HomeFilterBorderResponse.asModel(): SDBorderModel = SDBorderModel(
    color = color,
    width = width,
)

private fun HomeFilterClickLogResponse.asModel(): SDClickLogModel = SDClickLogModel(
    screenName = screenName.orEmpty(),
    objectType = objectType.orEmpty(),
    objectId = objectId.orEmpty(),
    extraParameters = extraParameters.orEmpty().mapValues { it.value.asClickLogValue() },
)

private fun HomeFilterViewLogResponse.asModelOrNull(): SDViewLogModel? {
    val screen = screenName?.takeIf { it.isNotBlank() } ?: return null
    return SDViewLogModel(screenName = screen)
}

private fun JsonElement.asClickLogValue(): SDClickLogValue {
    if (isJsonNull) return SDClickLogValue.Null
    if (!isJsonPrimitive) return SDClickLogValue.StringValue(toString())
    val primitive = asJsonPrimitive
    return when {
        primitive.isBoolean -> SDClickLogValue.BoolValue(primitive.asBoolean)
        primitive.isNumber -> {
            val number = primitive.asNumber
            val asDouble = number.toDouble()
            if (asDouble == asDouble.toLong().toDouble() && !primitive.asString.contains('.')) {
                val asLong = number.toLong()
                if (asLong in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
                    SDClickLogValue.IntValue(asLong.toInt())
                } else {
                    SDClickLogValue.LongValue(asLong)
                }
            } else {
                SDClickLogValue.DoubleValue(asDouble)
            }
        }
        else -> SDClickLogValue.StringValue(primitive.asString)
    }
}
