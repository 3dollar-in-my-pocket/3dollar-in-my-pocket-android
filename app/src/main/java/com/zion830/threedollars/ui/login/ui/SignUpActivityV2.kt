package com.zion830.threedollars.ui.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import base.compose.AppTheme
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.login.SignUpConstant
import com.zion830.threedollars.ui.login.ui.composable.SignUpScreen
import com.zion830.threedollars.ui.login.viewModel.SignUpViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class SignUpActivityV2 : ComponentActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            AppTheme {
                SignUpScreen(
                    viewModel = viewModel,
                    onBack = this::finish,
                    onSuccess = ::onSuccess,
                    onInvalidState = ::onInvalidState
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogManager.sendPageView(
            screen = ScreenName.SIGN_UP,
            className = this::class.java.simpleName
        )
    }

    private fun onSuccess() {
        setResult(RESULT_OK)
        finish()
    }

    private fun onInvalidState() {
        showToast(CommonR.string.signup_invalid_state)
        setResult(RESULT_CANCELED)
        finish()
    }

    companion object {
        fun getIntent(
            context: Context,
            loginType: LoginType,
            token: String
        ): Intent = Intent(
            context,
            SignUpActivityV2::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(SignUpConstant.Argument.LOGIN_TYPE, loginType)
            putExtra(SignUpConstant.Argument.TOKEN, token)
        }
    }
}
