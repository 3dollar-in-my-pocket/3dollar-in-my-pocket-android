package com.threedollar.network.sdui.core.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.threedollar.network.sdui.model.component.ImagePreviewCard
import com.threedollar.network.sdui.model.component.SDCard
import com.threedollar.network.sdui.model.component.SDCardType
import java.lang.reflect.Type

class SDCardDeserializer : JsonDeserializer<SDCard> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SDCard {
        val jsonObject = json.asJsonObject

        val type = jsonObject["type"]
            ?.asString
            ?.let { SDCardType.valueOf(it) }
            ?: throw JsonParseException("Card type is missing")

        return when (type) {
            SDCardType.IMAGE_PREVIEW_CARD -> {
                context.deserialize(jsonObject, ImagePreviewCard::class.java)
            }
        }
    }
}
