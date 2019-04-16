package com.jayner.githubrepos.model

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

data class Repo(val id: Long,
                val name: String,
                val description: String,
                val owner: User,
                @SerializedName("stargazers_count") val stargazersCount: Long,
                @SerializedName("forks_count") val forksCount: Long,
                @SerializedName("contributors_url") val contributorsUrl: String,
                @SerializedName("created_at") val createdDate: String,
                @SerializedName("updated_at") val updatedDate: String) {


    val gson = Gson()
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    fun getNumberOfForks(): String {
        return forksCount.toString()
    }

    fun getNumberOfStars(): String {
        return stargazersCount.toString()
    }

    fun getCreatedDateTime(): ZonedDateTime {
        Log.d("Repo", "getCreatedDateTime - createdDate: $createdDate")
        return ZonedDateTime.parse(createdDate)
    }

    fun getCreatedDateForDisplay(): String {
        val st = getCreatedDateTime().format(DATE_FORMATTER)
        Log.d("Repo", "getCreatedDateForDisplay - $st")

        return st
    }

    fun getUpdatedDateTime(): ZonedDateTime? {
        Log.d("Repo", "getUpdatedDateTime - updatedDate: $updatedDate")
        return ZonedDateTime.parse(updatedDate)
    }

    fun getUpdatedDateForDisplay(): String? {
        val st = getUpdatedDateTime()?.format(DATE_FORMATTER)
        Log.d("Repo", "getUpdatedDateForDisplay - $st")

        return st
    }

    fun toJson(): String {
        return gson.toJson(this)
    }

    fun fromJson(json: String): Repo {
        return gson.fromJson(json, Repo::class.java)
    }

}