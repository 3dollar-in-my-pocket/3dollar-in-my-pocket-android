package com.threedollar.network.sdui.core.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.threedollar.network.sdui.model.component.ImagePreviewCardModel
import com.threedollar.network.sdui.model.component.SDCardModel
import com.threedollar.network.sdui.model.component.SDCardType
import java.lang.reflect.Type

class SDCardDeserializer : JsonDeserializer<SDCardModel> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SDCardModel {
        val jsonObject = json.asJsonObject

        val type = jsonObject["type"]
            ?.asString
            ?.let { SDCardType.valueOf(it) }
            ?: throw JsonParseException("Card type is missing")

        return when (type) {
            SDCardType.IMAGE_PREVIEW_CARD -> {
                context.deserialize(jsonObject, ImagePreviewCardModel::class.java)
            }
        }
    }
}
