package com.threedollar.network.sdui.core.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.threedollar.network.sdui.core.deserializer.SDCardDeserializer
import com.threedollar.network.sdui.core.deserializer.SDSectionDeserializer
import com.threedollar.network.sdui.model.component.SDCardModel
import com.threedollar.network.sdui.model.section.SDSectionModel

object SDUIGson {
    fun provideGson(): Gson = GsonBuilder()
        .registerSDUI()
        .create()

    fun GsonBuilder.registerSDUI(): GsonBuilder = this
        .registerTypeAdapter(SDSectionModel::class.java, SDSectionDeserializer())
        .registerTypeAdapter(SDCardModel::class.java, SDCardDeserializer())
}
