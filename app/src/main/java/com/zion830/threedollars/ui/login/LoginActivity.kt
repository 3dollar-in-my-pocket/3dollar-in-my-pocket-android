package com.zion830.threedollars.ui.login

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginBinding
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.ResultWrapper
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        observeUiData()
        binding.btnLoginKakao.onSingleClick {
            tryKakaoLogin()
        }
        binding.btnLoginGoogle.onSingleClick {
            tryGoogleLogin()
        }
    }

    private fun tryGoogleLogin() {

    }

    private fun tryLogin(token: OAuthToken) {
        UserApiClient.instance.me { user, _ ->
            user?.let {
                Log.d(localClassName, it.groupUserToken.toString())
                viewModel.tryLogin(token.accessToken)
            }
        }
    }

    private fun observeUiData() {
        viewModel.loginResult.observe(this) {
            when {
                it is ResultWrapper.Success -> {
                    SharedPrefUtils.saveUserName(viewModel.userName.value)
                    SharedPrefUtils.saveUserId(it.value?.userId ?: 0)
                    SharedPrefUtils.saveAccessToken(it.value?.token)

                    startActivity(MainActivity.getIntent(this))
                    finish()
                }
                it is ResultWrapper.GenericError -> {
                    addInputNameFragment()
                }
            }
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                startActivity(MainActivity.getIntent(this))
                finish()
            }
        }
    }

    private fun addInputNameFragment() {
        supportFragmentManager.addNewFragment(R.id.layout_container, InputNameFragment.getInstance(), InputNameFragment::class.java.name)
    }

    private fun tryKakaoLogin() {
        val loginResCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(localClassName, "로그인 실패", error)
                showToast(R.string.error_no_kakao_login)
            } else if (token != null) {
                SharedPrefUtils.saveKakaoToken(token.accessToken, token.refreshToken)
                Log.d(localClassName, token.toString())
                tryLogin(token)
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            LoginClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
        }
    }
}