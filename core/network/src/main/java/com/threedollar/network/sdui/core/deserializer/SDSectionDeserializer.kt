package com.threedollar.network.sdui.core.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.threedollar.network.sdui.model.section.SDRelatedStoresSectionModel
import com.threedollar.network.sdui.model.section.SDSectionModel
import com.threedollar.network.sdui.model.section.SDSectionType
import java.lang.reflect.Type

class SDSectionDeserializer : JsonDeserializer<SDSectionModel> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): SDSectionModel {
        val jsonObject = json.asJsonObject

        val type = jsonObject["type"]
            ?.asString
            ?.let { SDSectionType.valueOf(it) }
            ?: throw JsonParseException("SDSection type is missing")

        return when (type) {
            SDSectionType.RELATED_STORES ->
                context.deserialize(jsonObject, SDRelatedStoresSectionModel::class.java)
        }
    }
}
