package com.jayner.githubrepos.model

import com.google.gson.Gson

data class User(val id: Long, val login: String) {

    val gson = Gson()

    fun toJson(): String {
        return gson.toJson(this)
    }

    fun fromJson(json: String): User {
        return gson.fromJson(json, User::class.java)
    }
}