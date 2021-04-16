package com.stupidtree.hitax.data.model

import com.google.gson.Gson

import com.google.gson.GsonBuilder
import java.text.DateFormat
import java.util.*


object GsonBuilderUtil {
    fun create(): Gson {
        val gb = GsonBuilder()
        gb.registerTypeAdapter(Date::class.java, DateSerializer())
            .setDateFormat(DateFormat.LONG)
        gb.registerTypeAdapter(Date::class.java, DateDeserializer())
            .setDateFormat(DateFormat.LONG)
        return gb.create()
    }
}