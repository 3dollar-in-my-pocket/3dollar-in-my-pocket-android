package com.threedollar.common.serverdriven.model

data class SDScreenModel(
    val sections: List<SDSectionModel> = emptyList(),
)

sealed interface SDSectionModel {
    val type: String

    data class ActionBarSection(
        override val type: String,
        val actionBar: SDActionBarModel,
    ) : SDSectionModel

    data class HeaderSection(
        override val type: String,
        val header: SDHeaderModel,
    ) : SDSectionModel

    data class CardsSection(
        override val type: String,
        val cards: List<SDCardModel> = emptyList(),
        val cursor: SDCursorModel? = null,
    ) : SDSectionModel

    data class Unknown(
        override val type: String,
    ) : SDSectionModel
}

data class SDActionBarModel(
    val button: SDButtonModel,
)

data class SDHeaderModel(
    val title: SDTextModel,
)

sealed interface SDCardModel {
    val type: String
    val cardId: String
    val title: SDTextModel
    val style: SDSurfaceStyleModel?

    data class DescriptionCard(
        override val type: String,
        override val cardId: String,
        override val title: SDTextModel,
        val description: SDTextModel,
        override val style: SDSurfaceStyleModel? = null,
    ) : SDCardModel

    data class HistoryCard(
        override val type: String,
        override val cardId: String,
        override val title: SDTextModel,
        val subTitles: List<SDTextModel> = emptyList(),
        val subTitleChip: SDChipModel? = null,
        val image: SDImageModel? = null,
        val metadata: SDTextModel? = null,
        override val style: SDSurfaceStyleModel? = null,
    ) : SDCardModel

    data class Unknown(
        override val type: String,
        override val cardId: String,
        override val title: SDTextModel,
        override val style: SDSurfaceStyleModel? = null,
    ) : SDCardModel
}

data class SDTextModel(
    val text: String,
    val isHtml: Boolean,
    val fontColor: String? = null,
    val fontWeight: String? = null,
)

data class SDTextSpansModel(
    val spans: List<SDTextModel> = emptyList(),
)

data class SDImageModel(
    val url: String,
    val style: SDImageStyleModel? = null,
)

data class SDImageStyleModel(
    val width: Double? = null,
    val height: Double? = null,
)

data class SDButtonModel(
    val text: SDTextModel,
    val image: SDImageModel? = null,
    val imageAlignment: String? = null,
    val link: SDLinkModel? = null,
    val customAction: SDCustomActionModel? = null,
    val style: SDSurfaceStyleModel? = null,
)

data class SDChipModel(
    val image: SDImageModel? = null,
    val text: SDTextModel,
    val additionalText: SDTextModel? = null,
    val style: SDSurfaceStyleModel? = null,
)

data class SDLinkModel(
    val type: String,
    val link: String,
)

data class SDViewLogModel(
    val screenName: String,
    val eventType: String = "",
    val objectType: String = "",
    val objectId: String = "",
    val extraParameters: Map<String, SDClickLogValue> = emptyMap(),
)

data class SDSurfaceStyleModel(
    val backgroundColor: String? = null,
    val border: SDBorderModel? = null,
)

data class SDBorderModel(
    val color: String? = null,
    val width: Double? = null,
)

data class SDCursorModel(
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
)

data class SDClickLogModel(
    val eventType: String = "",
    val screenName: String,
    val objectType: String,
    val objectId: String,
    val extraParameters: Map<String, SDClickLogValue> = emptyMap(),
)

data class SDCustomActionModel(
    val actionType: String,
    val extraParams: Map<String, SDClickLogValue> = emptyMap(),
)

data class SDImpressionLogModel(
    val eventType: String,
    val screenName: String,
    val objectType: String,
    val objectId: String,
    val extraParameters: Map<String, SDClickLogValue> = emptyMap(),
)

data class SDLocationModel(
    val latitude: Double,
    val longitude: Double,
)

data class HomeListSectionModel(
    val cards: List<HomeListCardModel> = emptyList(),
    val cursor: SDCursorModel? = null,
)

sealed interface HomeListCardModel {
    val type: String
    val cardId: String

    data class BasicCard(
        override val type: String,
        override val cardId: String,
        val header: HomeListCardHeaderModel,
        val metadata: HomeListCardMetadataModel,
        val images: List<SDImageModel> = emptyList(),
        val bodies: List<SDTextModel> = emptyList(),
        val marker: HomeListMarkerModel,
        val link: SDLinkModel? = null,
        val style: SDSurfaceStyleModel? = null,
        val clickLog: SDClickLogModel? = null,
        val impressionLog: SDImpressionLogModel? = null,
    ) : HomeListCardModel

    data class EmptyCard(
        override val type: String,
        override val cardId: String,
        val header: HomeListCardHeaderModel? = null,
        val bodies: List<SDTextModel> = emptyList(),
        val style: SDSurfaceStyleModel? = null,
    ) : HomeListCardModel

    data class AdMobCard(
        override val type: String,
        override val cardId: String,
        val clickLog: SDClickLogModel? = null,
        val impressionLog: SDImpressionLogModel? = null,
    ) : HomeListCardModel
}

data class HomeListCardHeaderModel(
    val title: SDTextModel? = null,
    val badge: SDImageModel? = null,
)

data class HomeListCardMetadataModel(
    val primary: List<SDChipModel> = emptyList(),
    val secondary: List<SDChipModel> = emptyList(),
    val separator: SDImageModel? = null,
)

data class HomeListMarkerModel(
    val focused: SDChipModel,
    val unfocused: SDChipModel,
    val location: SDLocationModel,
    val link: SDLinkModel? = null,
    val clickLog: SDClickLogModel? = null,
)

data class StoreScreenModel(
    val sections: List<StoreSectionModel> = emptyList(),
    val viewLog: SDViewLogModel? = null,
)

sealed interface StoreSectionModel {
    val type: String

    data class Preview(
        override val type: String,
        val header: HomeListCardHeaderModel,
        val metadata: HomeListCardMetadataModel,
        val additionalInfos: StoreSectionAdditionalInfosModel = StoreSectionAdditionalInfosModel(),
        val topActionBars: List<StoreActionBarModel> = emptyList(),
        val actionBars: List<StoreActionBarModel> = emptyList(),
        val images: List<SDImageModel> = emptyList(),
        val bodies: List<SDTextModel> = emptyList(),
        val style: SDSurfaceStyleModel? = null,
    ) : StoreSectionModel
}

data class StoreSectionAdditionalInfosModel(
    val type: String = "EMPTY",
    val isSubscriber: Boolean = false,
)

data class StoreActionBarModel(
    val type: String,
    val button: SDButtonModel,
    val clickLog: SDClickLogModel? = null,
)

sealed interface SDClickLogValue {
    val anyValue: Any?

    data class StringValue(val value: String) : SDClickLogValue {
        override val anyValue: Any get() = value
    }

    data class IntValue(val value: Int) : SDClickLogValue {
        override val anyValue: Any get() = value
    }

    data class DoubleValue(val value: Double) : SDClickLogValue {
        override val anyValue: Any get() = value
    }

    data class BoolValue(val value: Boolean) : SDClickLogValue {
        override val anyValue: Any get() = value
    }

    data object Null : SDClickLogValue {
        override val anyValue: Any? get() = null
    }
}
