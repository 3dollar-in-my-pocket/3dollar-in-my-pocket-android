package com.zion830.threedollars.ui.login.viewModel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.analytics.sendClick
import com.threedollar.domain.login.model.UnavailableNameException
import com.threedollar.domain.login.model.SignUpModel
import com.threedollar.domain.login.model.SignUpName
import com.threedollar.domain.login.model.SignUpToken
import com.threedollar.domain.login.usecase.GetRandomNameUseCase
import com.threedollar.domain.login.usecase.SignUpUseCase
import com.threedollar.common.base.UdfViewModel
import com.threedollar.common.ext.isRunning
import com.threedollar.common.ext.runWithDelayedAction
import com.threedollar.network.result.ApiError
import com.threedollar.network.result.ApiException
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.login.SignUpConstant
import com.zion830.threedollars.ui.login.model.SignUpNameMode
import com.zion830.threedollars.ui.login.model.SignUpNameState
import com.zion830.threedollars.ui.login.model.SignUpUiEffect
import com.zion830.threedollars.ui.login.model.SignUpUiIntent
import com.zion830.threedollars.ui.login.model.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val getRandomNameUseCase: GetRandomNameUseCase,
    private val signUpUseCase: SignUpUseCase,
    savedStateHandle: SavedStateHandle
) : UdfViewModel<SignUpUiIntent, SignUpUiState, SignUpUiEffect>() {
    private val nameState = MutableStateFlow<SignUpNameState>(SignUpNameState.Idle)

    private val tokenState: Flow<SignUpToken?> = combine(
        savedStateHandle.getStateFlow(SignUpConstant.Argument.LOGIN_TYPE, initialValue = LoginType.NONE),
        savedStateHandle.getStateFlow(SignUpConstant.Argument.TOKEN, "")
    ) { socialType, token ->
        if (socialType == LoginType.NONE) {
            _effect.send(SignUpUiEffect.OnInvalidState)
            return@combine null
        }

        if (token.isEmpty()) {
            _effect.send(SignUpUiEffect.OnInvalidState)
            return@combine null
        }

        return@combine SignUpToken(socialType.socialName, token)
    }

    override val state: StateFlow<SignUpUiState> = combine(nameState, tokenState) { name, token ->
        if (token == null) {
            return@combine SignUpUiState.Idle
        }

        return@combine SignUpUiState.Success(
            token = token,
            name = name
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SignUpUiState.Idle
    )

    private val currentState: SignUpUiState
        get() = state.value

    private val _effect = Channel<SignUpUiEffect>(
        capacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val effect: Flow<SignUpUiEffect> = _effect.receiveAsFlow()

    private var job: Job? = null

    override fun onException(exception: Throwable, tag: Any?) {
        super.onException(exception, tag)
        when (exception) {
            is ApiException -> {
                _effect.trySend(SignUpUiEffect.OnApiError(exception.message.orEmpty()))
            }

           is UnavailableNameException -> {
                _effect.trySend(SignUpUiEffect.OnAvailableName)
            }

            else -> {
                _effect.trySend(SignUpUiEffect.OnUnknownError)
            }
        }
    }

    override fun dispatch(intent: SignUpUiIntent) {
        if (job.isRunning) {
            return
        }

        when (intent) {
            is SignUpUiIntent.OnInit -> {
                onInit()
            }

            is SignUpUiIntent.OnNameChanged -> {
                onNameChanged(intent)
            }

            is SignUpUiIntent.OnSignUpClick -> {
                onSignUpClick()
            }

            is SignUpUiIntent.OnNameModeChange -> {
                onNameModeChange(intent)
            }

            is SignUpUiIntent.OnRandomNameClick -> {
                onRandomNameClick()
            }
        }
    }

    private fun onInit() {
        if (currentState !is SignUpUiState.Idle) {
            return
        }

        fetchRandomName()
    }

    private fun fetchRandomName() {
        launch {
            runWithDelayedAction(
                delayed = {
                    setLoading(true)
                }
            ) {
                getRandomNameUseCase.invoke().onSuccess { ret ->
                    nameState.update {
                        it.toSuccess(
                            input = TextFieldValue(ret, TextRange(ret.length)),
                            signUpNameMode = SignUpNameMode.Random
                        )
                    }
                }.onFailure {
                    onException(it)
                }
            }
        }.also {
            job = it
        }.invokeOnCompletion {
            setLoading(false)
        }
    }

    private fun onRandomNameClick() {
        LogManager.sendClick(
            screen = ScreenName.SIGN_UP,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.RANDOM
        )

        fetchRandomName()
    }

    private fun onNameChanged(intent: SignUpUiIntent.OnNameChanged) {
        nameState.update {
            it.onTextChanged(intent.input)
        }
    }

    private fun onNameModeChange(intent: SignUpUiIntent.OnNameModeChange) {
        val currentUiState = currentState as? SignUpUiState.Success ?: return
        val currentNameState = currentUiState.name as? SignUpNameState.Success ?: return

        if (currentNameState.signUpNameMode == intent.mode) {
            return
        }

        when (intent.mode) {
            SignUpNameMode.Random -> {
                fetchRandomName()
            }

            SignUpNameMode.Manual -> {
                nameState.update {
                    it.toSuccess(
                        input = TextFieldValue(),
                        signUpNameMode = SignUpNameMode.Manual
                    )
                }
            }
        }
    }

    private fun onSignUpClick() {
        LogManager.sendClick(
            screen = ScreenName.SIGN_UP,
            objectType = LogObjectType.BUTTON,
            objectId = LogObjectId.SIGN_UP
        )

        val currentUiState = currentState as? SignUpUiState.Success ?: return
        val currentNameState = currentUiState.name as? SignUpNameState.Success ?: return
        val signUpToken = currentUiState.token ?: return

        launch {
            runWithDelayedAction(
                delayed = {
                    setLoading(true)
                }
            ) {
                signUpUseCase.invoke(
                    param = SignUpModel(
                        name = when (currentNameState.signUpNameMode) {
                            SignUpNameMode.Random -> {
                                SignUpName.Random(value = currentNameState.input.text)
                            }

                            SignUpNameMode.Manual -> {
                                SignUpName.Manual(value = currentNameState.input.text)
                            }
                        },
                        token = signUpToken,
                    )
                ).onSuccess {
                    _effect.send(SignUpUiEffect.OnSignUpSuccess)
                }.onFailure {
                    onSignUpFailure(it)
                }
            }
        }.also {
            job = it
        }.invokeOnCompletion {
            setLoading(false)
        }
    }

    private fun onSignUpFailure(throwable: Throwable) {
        if (throwable !is ApiException) {
            return onException(throwable)
        }

        when (throwable.error) {
            ApiError.ALREADY_EXISTS_NICKNAME -> {
                nameState.update { it.markAsDuplicated() }
            }

            else -> {
                onException(throwable)
            }
        }
    }
}
