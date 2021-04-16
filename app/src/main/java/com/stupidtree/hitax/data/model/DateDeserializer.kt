package com.stupidtree.hitax.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.sql.Timestamp


class DateDeserializer : JsonDeserializer<Timestamp> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Timestamp {
        return Timestamp(json.asJsonPrimitive.asLong)
    }
}
