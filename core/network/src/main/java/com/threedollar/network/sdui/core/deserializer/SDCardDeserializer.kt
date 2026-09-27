package com.threedollar.network.sdui.core.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.threedollar.common.sdui.model.component.ImagePreviewCardModel
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.component.SDCardType
import com.threedollar.common.sdui.model.component.SDUnknownCardModel
import java.lang.reflect.Type

class SDCardDeserializer : JsonDeserializer<SDCardModel> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SDCardModel {
        val jsonObject = json.takeIf { it.isJsonObject }?.asJsonObject
            ?: throw JsonParseException("Card must be an object")
        val rawType = jsonObject["type"]?.takeIf { it.isJsonPrimitive }?.asString
        val cardId = jsonObject["cardId"]?.takeIf { it.isJsonPrimitive }?.asString.orEmpty()

        return when (SDCardType.entries.firstOrNull { it.name == rawType }) {
            SDCardType.IMAGE_PREVIEW_CARD -> runCatching<SDCardModel> {
                context.deserialize(jsonObject, ImagePreviewCardModel::class.java)
            }.getOrElse { SDUnknownCardModel(cardId) }
            SDCardType.UNKNOWN, null -> SDUnknownCardModel(cardId)
        }
    }
}
