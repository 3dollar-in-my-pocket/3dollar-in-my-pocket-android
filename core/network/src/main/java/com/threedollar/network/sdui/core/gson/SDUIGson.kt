package com.threedollar.network.sdui.core.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.threedollar.network.sdui.core.deserializer.SDCardDeserializer
import com.threedollar.network.sdui.core.deserializer.SDSectionDeserializer
import com.threedollar.network.sdui.model.component.SDCard
import com.threedollar.network.sdui.model.section.SDSection

object SDUIGson {
    fun provideGson(): Gson = GsonBuilder()
        .registerSDUI()
        .create()

    fun GsonBuilder.registerSDUI(): GsonBuilder = this
        .registerTypeAdapter(SDSection::class.java, SDSectionDeserializer())
        .registerTypeAdapter(SDCard::class.java, SDCardDeserializer())
}
