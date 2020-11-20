package com.zion830.threedollars.login

import android.util.Log
import androidx.activity.viewModels
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginBinding
import com.zion830.threedollars.utils.SharedPrefUtils
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment
import zion830.com.common.ext.isNotNullOrBlank
import zion830.com.common.ext.showSnack

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        UserApiClient.instance.me { user, _ ->
            if (user != null && SharedPrefUtils.getUserName().isNotNullOrBlank()) {
                SharedPrefUtils.saveKakaoId(user.id.toString())
                startActivity(MainActivity.getIntent(this))
                finish()
            }
        }

        binding.btnLoginKakao.onSingleClick {
            tryKakaoLogin()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.isFinishing.observe(this) {
            if (it) {
                addInputNameFragment()
            } else {
                binding.layoutContainer.showSnack(R.string.login_failed)
            }
        }
        viewModel.loginResult.observe(this) {
            if (it != null) {
                SharedPrefUtils.saveUserName(viewModel.userName.value)
                SharedPrefUtils.saveUserId(it.userId)
                SharedPrefUtils.saveAccessToken(it.token)

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
            } else if (token != null) {
                addInputNameFragment()
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            LoginClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
        }
    }
}