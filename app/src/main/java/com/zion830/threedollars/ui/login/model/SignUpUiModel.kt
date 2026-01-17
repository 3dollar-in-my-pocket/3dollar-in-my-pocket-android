package com.zion830.threedollars.ui.login.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.threedollar.domain.login.model.SignUpToken
import com.zion830.threedollars.core.ui.component.compose.utils.take

@Immutable
sealed interface SignUpNameState {
    val canSignUp: Boolean
        get() = when (this) {
            is Idle -> {
                false
            }

            is Success -> {
                input.text.length >= MANUAL_INPUT_NAME_MIN_LENGTH  && !isDuplicated
            }
        }

    data object Idle : SignUpNameState

    data class Success(
        val signUpNameMode: SignUpNameMode = SignUpNameMode.Random,
        val input: TextFieldValue = TextFieldValue(),
        val isDuplicated: Boolean = false,
    ) : SignUpNameState

    fun toSuccess(
        input: TextFieldValue,
        signUpNameMode: SignUpNameMode
    ): SignUpNameState = Success(
        input = input,
        signUpNameMode = signUpNameMode
    )

    fun onTextChanged(
        new: TextFieldValue
    ): SignUpNameState {
        if (!new.text.matches(INPUT_REGEX)) {
            return this
        }

        return Success(
            input = new.take(MANUAL_INPUT_NAME_MAX_LENGTH),
            signUpNameMode = SignUpNameMode.Manual
        )
    }

    fun markAsDuplicated(): SignUpNameState = (this as? SignUpNameState.Success)?.copy(
        isDuplicated = true
    ) ?: this

    companion object {
        private const val MANUAL_INPUT_NAME_MAX_LENGTH = 10
        private const val MANUAL_INPUT_NAME_MIN_LENGTH = 2
        private val INPUT_REGEX = Regex("^[ㄱ-ㅎ가-힣a-zA-Z\\d_-]*$")
    }
}

@Immutable
sealed interface SignUpUiState {
    data object Idle : SignUpUiState

    data class Success(
        val token: SignUpToken?,
        val name: SignUpNameState,
    ) : SignUpUiState
}

enum class SignUpNameMode {
    Random,
    Manual
}

@Immutable
sealed interface SignUpUiIntent {
    data object OnInit : SignUpUiIntent

    data class OnNameChanged(
        val input: TextFieldValue
    ) : SignUpUiIntent

    data object OnSignUpClick : SignUpUiIntent

    data class OnNameModeChange(
        val mode: SignUpNameMode
    ) : SignUpUiIntent

    data object OnRandomNameClick : SignUpUiIntent
}

@Immutable
sealed interface SignUpUiEffect {
    data object OnInvalidState : SignUpUiEffect
    data object OnSignUpSuccess : SignUpUiEffect
    data class OnApiError(val msg: String) : SignUpUiEffect
    data object OnUnknownError : SignUpUiEffect
    data object OnAvailableName : SignUpUiEffect
}
