package com.threedollar.domain.login.usecase

interface GetRandomNameUseCase {
    suspend operator fun invoke() : Result<String>
}
