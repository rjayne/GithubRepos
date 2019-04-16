package com.jayner.githubrepos.data

import com.google.gson.*
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type


class DateTimeTypeConverter: JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    override fun serialize(src: ZonedDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ZonedDateTime {
        return ZonedDateTime.parse(json.getAsString())
    }

}