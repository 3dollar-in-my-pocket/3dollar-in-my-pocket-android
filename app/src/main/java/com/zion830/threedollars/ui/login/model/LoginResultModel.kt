package com.zion830.threedollars.ui.login.model

import com.zion830.threedollars.datasource.model.LoginType

sealed interface LoginResultModel {
    data class Success(
        val userId: Int,
        val token: String
    ) : LoginResultModel

    data object Maintanance : LoginResultModel

    data object Error : LoginResultModel

    data class RequireSignUp(
        val loginType: LoginType,
        val token: String
    ) : LoginResultModel
}
