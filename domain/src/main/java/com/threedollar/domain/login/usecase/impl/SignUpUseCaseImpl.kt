package com.threedollar.domain.login.usecase.impl

import com.threedollar.domain.login.model.SignUpModel
import com.threedollar.domain.login.model.SignUpName
import com.threedollar.domain.login.model.UnavailableNameException
import com.threedollar.domain.login.repository.SignUpRepository
import com.threedollar.domain.login.usecase.SignUpUseCase
import javax.inject.Inject

internal class SignUpUseCaseImpl @Inject constructor(
    private val signUpRepository: SignUpRepository
) : SignUpUseCase {

    override suspend fun invoke(param: SignUpModel): Result<Unit> {
        val isAvailableName = when (param.name) {
            is SignUpName.Manual -> {
                param.name.value.matches(MANUAL_INPUT_VERIFY_REGEX)
            }

            else -> {
                true
            }
        }
        if (!isAvailableName){
            return Result.failure(UnavailableNameException())
        }

        val nameCheckException = when (param.name) {
            is SignUpName.Manual -> {
                signUpRepository
                    .checkName(name = param.name.value)
                    .exceptionOrNull()
            }

            is SignUpName.Random -> {
                null
            }
        }

        if (nameCheckException != null) {
            return Result.failure(nameCheckException)
        }

        return signUpRepository.signUp(model = param)
    }

    companion object {
        private val MANUAL_INPUT_VERIFY_REGEX = Regex("^[ㄱ-ㅎ가-힣a-zA-Z\\d][ㄱ-ㅎ가-힣\\sa-zA-Z\\d_-]{0,8}[ㄱ-ㅎ가-힣a-zA-Z\\d]$")
    }
}
