package com.zion830.threedollars.login

import android.content.Intent
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

class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        if (!hasUserName()) {
            addInputNameFragment() // 카카오 로그인만 한 상태면 닉네임 필요함
        }

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (tokenInfo != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.btnLoginKakao.onSingleClick {
            tryKakaoLogin()
        }
    }

    private fun hasUserName() = SharedPrefUtils.getKakaoId().isNotNullOrBlank() && SharedPrefUtils.getAccessToken().isNullOrBlank()

    private fun addInputNameFragment() {
        supportFragmentManager.addNewFragment(R.id.layout_container, InputNameFragment.getInstance(), InputNameFragment::class.java.name)
    }

    private fun tryKakaoLogin() {
        val loginResCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(localClassName, "로그인 실패", error)
            } else if (token != null) {
                Log.i(localClassName, "로그인 성공 ${token.accessToken}")
                SharedPrefUtils.saveKakaoId(token.accessToken)
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