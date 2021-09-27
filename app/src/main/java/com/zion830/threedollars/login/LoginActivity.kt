package com.zion830.threedollars.login

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
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        startMainActivityIfUserExist()
        observeUiData()

        binding.btnLoginKakao.onSingleClick {
            tryKakaoLogin()
        }
    }

    private fun startMainActivityIfUserExist() {
        if (SharedPrefUtils.getKakaoId() != null && SharedPrefUtils.getUserId() >= 0) {
            startActivity(MainActivity.getIntent(this))
            finish()
        }
    }

    private fun tryLogin() {
        UserApiClient.instance.me { user, _ ->
            user?.let {
                showToast(R.string.error_no_kakao_login)
                // SharedPrefUtils.saveKakaoId(it.id.toString())
                // viewModel.tryLogin()
            }
        }
    }

    private fun observeUiData() {
        viewModel.loginResult.observe(this) {
            if (it != null) {
                SharedPrefUtils.saveUserName(viewModel.userName.value)
                SharedPrefUtils.saveUserId(it.userId)
                SharedPrefUtils.saveAccessToken(it.token)

                if (!it.state) {
                    addInputNameFragment()
                } else {
                    startActivity(MainActivity.getIntent(this))
                    finish()
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
                Log.d(localClassName, token.toString())
                tryLogin()
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            LoginClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
        }
    }
}