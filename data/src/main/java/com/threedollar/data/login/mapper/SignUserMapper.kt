package com.threedollar.data.login.mapper

import com.threedollar.domain.login.model.SignUserModel
import com.threedollar.network.data.auth.SignUser

object SignUserMapper {

    fun toDomainModel(response: SignUser): SignUserModel {
        return SignUserModel(
            token = response.token,
            userId = response.userId
        )
    }
}
