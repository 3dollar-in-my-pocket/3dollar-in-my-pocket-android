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
    val link: SDLinkModel? = null,
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
    val screenName: String,
    val objectType: String,
    val objectId: String,
    val extraParameters: Map<String, SDClickLogValue> = emptyMap(),
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
