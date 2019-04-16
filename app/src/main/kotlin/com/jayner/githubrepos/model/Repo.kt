package com.jayner.githubrepos.model

import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

/**
 * Represents the GitHub repository data
 */
data class Repo(val id: Long,
                val name: String,
                val description: String,
                val owner: User,
                @SerializedName("stargazers_count") val stargazersCount: Long,
                @SerializedName("forks_count") val forksCount: Long,
                @SerializedName("contributors_url") val contributorsUrl: String,
                @SerializedName("created_at") val createdDate: ZonedDateTime,
                @SerializedName("updated_at") val updatedDate: ZonedDateTime) {

}