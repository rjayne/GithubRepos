package com.jayner.githubrepos.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the GitHub Repository Contributor data
 */
data class Contributor(val id: Long, val login: String, @SerializedName("avatar_url") val avatarUrl: String, val contributions: Long) {

}