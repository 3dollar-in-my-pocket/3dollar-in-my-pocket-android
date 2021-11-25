package com.zion830.threedollars.repository.model

enum class LoginType(val socialName: String) {
    KAKAO("KAKAO"), GOOGLE("GOOGLE"), NONE("");

    companion object {

        fun of(key: String?): LoginType = values().find { it.socialName == key } ?: NONE
    }
}