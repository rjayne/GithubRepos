package com.jayner.githubrepos.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jayner.githubrepos.data.DateTimeTypeConverter
import org.threeten.bp.ZonedDateTime
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type

object TestUtils {

    fun <T> loadJson(path: String, type: Type): T {
        try {
            val json = getFileAsString(path)

            return Gson().fromJson<T>(json, type)
        } catch (e: IOException) {
            throw IllegalArgumentException("Could not deserialize: $path into type: $type")
        }

    }

    fun <T> loadJson(path: String, clazz: Class<T>): T {
        try {
            val json = getFileAsString(path)
            val gson = GsonBuilder()
                .registerTypeAdapter(ZonedDateTime::class.java, DateTimeTypeConverter())
                .create()
            return gson.fromJson<T>(json, clazz)
        } catch (e: IOException) {
            throw IllegalArgumentException("Could not deserialize: $path into class: $clazz")
        }

    }

    private fun getFileAsString(path: String): String {
        try {
            val sb = StringBuilder()
            val reader = BufferedReader(
                InputStreamReader(
                    this.javaClass.getClassLoader().getResourceAsStream(path)
                )
            )

            var strLine = reader.readLine()
            while (strLine != null) {
                sb.append(strLine).append("\n")
                strLine = reader.readLine()
            }

            return sb.toString()
        } catch (e: IOException) {
            throw IllegalArgumentException("Could not read from resource at: $path")
        }
    }

}