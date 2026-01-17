package com.zion830.threedollars.ui.login.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.Gray100
import com.zion830.threedollars.core.ui.component.compose.LottieFishLoading
import com.zion830.threedollars.ui.login.model.SignUpNameMode
import com.zion830.threedollars.ui.login.model.SignUpNameState
import com.zion830.threedollars.ui.login.model.SignUpUiEffect
import com.zion830.threedollars.ui.login.model.SignUpUiIntent
import com.zion830.threedollars.ui.login.model.SignUpUiState
import com.zion830.threedollars.ui.login.viewModel.SignUpViewModel
import base.compose.ColorWhite
import base.compose.Gray40
import base.compose.Gray60
import base.compose.Gray80
import base.compose.Gray90
import base.compose.LocalAppShape
import base.compose.Pink
import base.compose.Red
import com.zion830.threedollars.core.ui.component.compose.components.ConfirmAlertDialog
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.core.ui.component.compose.components.HorizontalSpacer
import com.zion830.threedollars.core.ui.component.compose.components.VerticalSpacer
import com.zion830.threedollars.core.ui.component.compose.components.clearImeOnTap
import com.zion830.threedollars.core.ui.component.compose.components.noRippleClickable
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

private sealed interface Alert {
    data object UnknownError : Alert
    data object UnavailableName : Alert
    data class ApiError(val message: String) : Alert
}

@SuppressWarnings("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    onInvalidState: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    var alert by remember {
        mutableStateOf<Alert?>(null)
    }

    FlowWithLifecycleEffect(
        flow = viewModel.effect
    ) {
        when (it) {
            is SignUpUiEffect.OnSignUpSuccess -> {
                onSuccess.invoke()
            }

            is SignUpUiEffect.OnUnknownError -> {
                alert = Alert.UnknownError
            }

            is SignUpUiEffect.OnApiError -> {
                alert = Alert.ApiError(it.msg)
            }

            is SignUpUiEffect.OnInvalidState -> {
                onInvalidState.invoke()
            }

            is SignUpUiEffect.OnAvailableName -> {
                alert = Alert.UnavailableName
            }
        }
    }

    LaunchedEffect(state) {
        when (state) {
            is SignUpUiState.Idle -> {
                viewModel.dispatch(SignUpUiIntent.OnInit)
            }

            else -> {
                // do nothing
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Gray100,
        topBar = {
            TopBar(onBack = onBack)
        },
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = state) {
                is SignUpUiState.Idle -> {
                    // do nothing
                }

                is SignUpUiState.Success -> {
                    Content(
                        state = state,
                        onIntent = viewModel::dispatch
                    )
                }
            }

            if (loading) {
                LottieFishLoading(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }

    alert?.let {
        ConfirmAlertDialog(
            title = when (it) {
                is Alert.UnknownError -> {
                    stringResource(CommonR.string.error_unknown_title)
                }

                else -> {
                    ""
                }
            },
            text = when (it) {
                is Alert.UnknownError -> {
                    stringResource(CommonR.string.error_unknown_message)
                }

                is Alert.ApiError -> {
                    it.message
                }

                is Alert.UnavailableName -> {
                    stringResource(CommonR.string.signup_unavailable_name)
                }
            }
        ) {
            alert = null
        }
    }
}

@Composable
private fun BoxScope.Content(
    state: SignUpUiState.Success,
    onIntent: (SignUpUiIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(top = 56.dp)
            .systemBarsPadding()
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .clearImeOnTap(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerticalSpacer(110)
        Logo()

        when (state.name) {
            is SignUpNameState.Idle -> {
                // do nothing
            }

            is SignUpNameState.Success -> {
                VerticalSpacer(24)
                InputHeader()
                VerticalSpacer(2)
                Input(
                    state = state.name,
                    onChanged = {
                        onIntent.invoke(SignUpUiIntent.OnNameChanged(it))
                    },
                    onModeToManual = {
                        onIntent.invoke(SignUpUiIntent.OnNameModeChange(SignUpNameMode.Manual))
                    }
                )
                VerticalSpacer(2)
                InputFooter(
                    onRequestRandomName = {
                        focusManager.clearFocus(force = true)
                        onIntent.invoke(SignUpUiIntent.OnRandomNameClick)
                    }
                )
                if (state.name.isDuplicated) {
                    VerticalSpacer(12)
                    DuplicatedNameIndicator()
                }
            }
        }
    }
    BottomButton(
        enabled = state.name.canSignUp,
        onClick = {
            focusManager.clearFocus(force = true)
            onIntent.invoke(SignUpUiIntent.OnSignUpClick)
        }
    )
    BottomOverlay(
        enabled = state.name.canSignUp,
    )
}

@Composable
private fun TopBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(
            onClick = onBack,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = ColorWhite
            ),
            shape = CircleShape,
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
                modifier = Modifier.size(24.dp),
                contentDescription = "뒤로가기"
            )
        }
    }
}

@Composable
private fun BoxScope.BottomButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(
                color = if (enabled) {
                    Pink
                } else {
                    Gray80
                }
            )
            .align(Alignment.BottomCenter),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(CommonR.string.signup_btn),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) {
                ColorWhite
            } else {
                Gray60
            }
        )
    }
}

@Composable
private fun BoxScope.BottomOverlay(
    enabled: Boolean
) {
    val density = LocalDensity.current

    val overlayHeight = with(density) {
        WindowInsets.navigationBars
            .getBottom(this)
            .toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(overlayHeight)
            .background(
                color = if (enabled) {
                    Pink
                } else {
                    Gray80
                }
            )
            .align(Alignment.BottomCenter)
    )
}

@Composable
private fun Logo() {
    Image(
        painter = painterResource(DesignSystemR.drawable.ic_fish_simple),
        modifier = Modifier
            .width(130.dp)
            .height(78.dp),
        contentDescription = "로고"
    )
}

@Composable
private fun InputHeader() {
    Text(
        text = stringResource(CommonR.string.signup_input_name_header),
        fontSize = 30.sp,
        lineHeight = 42.sp,
        fontWeight = FontWeight.W700,
        color = ColorWhite,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Input(
    state: SignUpNameState.Success,
    textStyle: TextStyle = LocalTextStyle.current.copy(
        color = if (state.isDuplicated) {
            Red
        } else {
            Pink
        },
        fontSize = 30.sp,
        lineHeight = 42.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    onChanged: (TextFieldValue) -> Unit,
    onModeToManual: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isImeVisible = WindowInsets.isImeVisible

    /**
     * Compose의 TextField는 wrap-content-width 미지원
     * 직접 처리해주어야 함
     */
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textWidthDp by remember(state.input.text, textStyle) {
        derivedStateOf {
            with(density) {
                textMeasurer
                    .measure(
                        text = AnnotatedString(state.input.text),
                        style = textStyle
                    )
                    .size
                    .width
                    .toDp()
            }
        }
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(state.signUpNameMode) {
        when (state.signUpNameMode) {
            SignUpNameMode.Manual -> {
                focusRequester.requestFocus()
            }

            else -> {
                // do nothing
            }
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterHorizontally)
        ) {
            BasicTextField(
                value = state.input,
                onValueChange = { onChanged.invoke(it) },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            onModeToManual.invoke()
                        }

                        isFocused = it.isFocused
                    }
                    .width(textWidthDp + 4.dp)
                    .wrapContentHeight(),
                enabled = state.signUpNameMode == SignUpNameMode.Manual,
                textStyle = textStyle,
                cursorBrush = SolidColor(value = Pink),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    showKeyboardOnFocus = true,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                ),
                keyboardActions = KeyboardActions(
                    onAny = {
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
                maxLines = 1
            )

            if (state.isDuplicated && !isFocused) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_warning_red_16),
                    tint = Red,
                    contentDescription = "닉네임 중복 아이콘",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (!isFocused) {
            Box(
                modifier = Modifier
                    .noRippleClickable {
                        when (state.signUpNameMode) {
                            SignUpNameMode.Random -> {
                                onModeToManual.invoke()
                            }

                            SignUpNameMode.Manual -> {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                    .fillMaxSize()
            )
        }

        if (state.input.text.isEmpty()) {
            Text(
                text = stringResource(CommonR.string.name_empty),
                fontSize = 30.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Gray60
            )
        }
    }
}

@Composable
private fun InputFooter(
    onRequestRandomName: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(CommonR.string.signup_input_name_footer),
            fontSize = 30.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.W700,
            color = ColorWhite
        )
        HorizontalSpacer(8)
        RandomNameButton(
            onClick = onRequestRandomName
        )
    }
}

@Composable
private fun RandomNameButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(
                shape = LocalAppShape.current.small
            )
            .background(
                color = Gray90,
                shape = LocalAppShape.current.small
            )
            .border(
                width = 1.dp,
                color = Gray80,
                shape = LocalAppShape.current.small
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_refresh_20),
            contentDescription = "랜덤 닉네임",
            modifier = Modifier.size(20.dp),
            tint = Gray40
        )
    }
}

@Composable
private fun DuplicatedNameIndicator() {
    Box(
        modifier = Modifier
            .background(
                color = Red.copy(alpha = 0.1f),
                shape = LocalAppShape.current.chipShape
            )
            .wrapContentSize()
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(CommonR.string.signup_input_name_duplicated),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.W500,
            color = Red
        )
    }
}
