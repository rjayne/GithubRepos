package com.jayner.githubrepos.model

import com.google.gson.annotations.SerializedName

data class Contributor(val id: Long, val login: String, @SerializedName("avatar_url") val avatarUrl: String) {

}