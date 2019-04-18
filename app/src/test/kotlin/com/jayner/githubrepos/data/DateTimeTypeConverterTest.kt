package com.jayner.githubrepos.data

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import java.lang.reflect.Type

class DateTimeTypeConverterTest {

    val dateTimeTypeConverter = DateTimeTypeConverter()

    val dateTimeString = "2019-04-15T03:34:34Z"
    val dateTime = ZonedDateTime.parse(dateTimeString)
    val dateTimeJson = JsonPrimitive(dateTimeString)
    val zonedDateTimeType = object : TypeToken<ZonedDateTime>(){}.type

    @Test
    fun testSerialize() {
        val context = object: JsonSerializationContext {
            override fun serialize(src: Any?): JsonElement {
                return JsonNull.INSTANCE // not used
            }

            override fun serialize(src: Any?, typeOfSrc: Type?): JsonElement {
                return JsonNull.INSTANCE // not used
            }
        }

        val json = dateTimeTypeConverter.serialize(dateTime, zonedDateTimeType, context)
        Assert.assertEquals(dateTimeJson, json)
    }

    @Test
    fun testDeserialize() {
        val context = object: JsonDeserializationContext {
            override fun <T : Any?> deserialize(json: JsonElement?, typeOfT: Type?): T {
                return "" as T // not used
            }
        }

        val json = dateTimeTypeConverter.deserialize(dateTimeJson, zonedDateTimeType, context)
        Assert.assertEquals(dateTime, json)
    }
}