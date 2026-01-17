package com.threedollar.domain.login.usecase

import com.threedollar.domain.login.model.SignUpModel

interface SignUpUseCase {
    suspend operator fun invoke(param: SignUpModel) : Result<Unit>
}
