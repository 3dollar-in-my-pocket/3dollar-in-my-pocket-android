package com.zion830.threedollars.datasource.model

enum class LoginType(val socialName: String) {
    KAKAO("KAKAO"), GOOGLE("GOOGLE"), APPLE("APPLE"), NONE("");

    companion object {

        fun of(key: String?): LoginType = values().find { it.socialName == key } ?: NONE
    }
}