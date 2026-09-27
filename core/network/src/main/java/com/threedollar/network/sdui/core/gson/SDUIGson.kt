package com.threedollar.network.sdui.core.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.threedollar.common.sdui.model.component.SDCardModel
import com.threedollar.common.sdui.model.section.SDSectionModel
import com.threedollar.network.sdui.core.deserializer.SDCardDeserializer
import com.threedollar.network.sdui.core.deserializer.SDSectionDeserializer

object SDUIGson {
    fun provideGson(): Gson = GsonBuilder()
        .registerSDUI()
        .create()

    fun GsonBuilder.registerSDUI(): GsonBuilder = this
        .registerTypeAdapter(SDSectionModel::class.java, SDSectionDeserializer())
        .registerTypeAdapter(SDCardModel::class.java, SDCardDeserializer())
}
