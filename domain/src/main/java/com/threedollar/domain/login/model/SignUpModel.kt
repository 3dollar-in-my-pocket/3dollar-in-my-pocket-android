package com.threedollar.domain.login.model

data class SignUpModel(
    val name: SignUpName,
    val token: SignUpToken,
)

sealed interface SignUpName {
    val value: String

    data class Manual(
        override val value: String
    ) : SignUpName

    data class Random(
        override val value: String
    ) : SignUpName
}

data class SignUpToken(
    val socialType: String,
    val value: String
)
