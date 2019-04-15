package com.jayner.githubrepos.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

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

    fun getNumberOfForks(): String {
        return forksCount.toString()
    }

    fun getNumberOfStars(): String {
        return stargazersCount.toString()
    }

    fun getCreatedDateTime(): ZonedDateTime {
        return ZonedDateTime.parse(createdDate)
    }

    fun getUpdatedDateTime(): ZonedDateTime {
        return ZonedDateTime.parse(updatedDate)
    }

    fun toJson(): String {
        return gson.toJson(this)
    }

    fun fromJson(json: String): Repo {
        return gson.fromJson(json, Repo::class.java)
    }
}