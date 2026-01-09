package com.threedollar.domain.login.usecase.impl

import com.threedollar.domain.login.repository.SignUpRepository
import com.threedollar.domain.login.usecase.GetRandomNameUseCase
import javax.inject.Inject

internal class GetRandomNameUseCaseImpl @Inject constructor(
    private val signUpRepository: SignUpRepository
): GetRandomNameUseCase {
    override suspend fun invoke(): Result<String> = signUpRepository.getRandomName().map {
        it.name
    }
}
