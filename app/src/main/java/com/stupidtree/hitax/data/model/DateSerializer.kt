package com.stupidtree.hitax.data.model

import com.google.gson.JsonPrimitive

import com.google.gson.JsonSerializationContext

import com.google.gson.JsonElement

import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.sql.Timestamp


class DateSerializer : JsonSerializer<Timestamp> {
    override fun serialize(
        src: Timestamp,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.time)
    }
}