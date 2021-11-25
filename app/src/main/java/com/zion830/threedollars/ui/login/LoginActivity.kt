package com.zion830.threedollars.ui.login

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.zion830.threedollars.Constants.GOOGLE_SIGN_IN
import com.zion830.threedollars.Constants.TIME_MILLIS_DAY
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginBinding
import com.zion830.threedollars.repository.model.LoginType
import com.zion830.threedollars.ui.popup.PopupActivity
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.ResultWrapper
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment
import java.util.*


class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        observeUiData()
        binding.btnLoginKakao.onSingleClick {
            SharedPrefUtils.saveLoginType(LoginType.KAKAO)
            tryKakaoLogin()
        }
        binding.btnLoginGoogle.onSingleClick {
            SharedPrefUtils.saveLoginType(LoginType.GOOGLE)
            tryGoogleLogin()
        }
    }

    private fun tryGoogleLogin() {
        lifecycleScope.launch(Dispatchers.IO) {
            val account = GoogleSignIn.getLastSignedInAccount(GlobalApplication.getContext())
            if (account != null && account.idToken != null) {
                loginWithGoogle(account)
            } else {
                withContext(Dispatchers.Main) {
                    val intent = GlobalApplication.googleClient.signInIntent
                    startActivityForResult(intent, GOOGLE_SIGN_IN)
                }
            }
        }
    }

    private fun loginWithGoogle(account: GoogleSignInAccount?) {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val token =
                    GoogleAuthUtil.getToken(
                        GlobalApplication.getContext(),
                        account?.account,
                        "oauth2:https://www.googleapis.com/auth/plus.me"
                    )
                SharedPrefUtils.saveGoogleToken(token)
                SharedPrefUtils.saveLoginType(LoginType.GOOGLE)
                viewModel.tryLogin(LoginType.GOOGLE, token)
            }
        } catch (e: Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            val account: GoogleSignInAccount? = completedTask?.getResult(ApiException::class.java)
            if (account != null && account.idToken != null) {
                loginWithGoogle(account)
            } else {
                Log.e("LoginActivity", "account is null")
                showToast(R.string.login_failed)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            showToast(R.string.login_failed)
        }
    }

    private fun tryLogin(token: OAuthToken) {
        UserApiClient.instance.me { user, _ ->
            user?.let {
                Log.d(localClassName, it.groupUserToken.toString())
                SharedPrefUtils.saveGoogleToken(token.accessToken)
                viewModel.tryLogin(LoginType.KAKAO, token.accessToken)
            }
        }
    }

    private fun observeUiData() {
        viewModel.loginResult.observe(this) {
            when (it) {
                is ResultWrapper.Success -> {
                    SharedPrefUtils.saveUserId(it.value?.userId ?: 0)
                    SharedPrefUtils.saveAccessToken(it.value?.token)

                    if (Calendar.getInstance().timeInMillis - SharedPrefUtils.getPopupTime() > TIME_MILLIS_DAY) {
                        startActivity(PopupActivity.getIntent(this))
                    } else {
                        startActivity(MainActivity.getIntent(this))
                    }
                    finish()
                }
                is ResultWrapper.GenericError -> {
                    addInputNameFragment()
                }
                else -> {
                    showToast(R.string.connection_failed)
                }
            }
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                if (Calendar.getInstance().timeInMillis - SharedPrefUtils.getPopupTime() > TIME_MILLIS_DAY) {
                    startActivity(PopupActivity.getIntent(this))
                } else {
                    startActivity(MainActivity.getIntent(this))
                }
                finish()
            }
        }
    }

    private fun addInputNameFragment() {
        supportFragmentManager.addNewFragment(
            R.id.layout_container,
            InputNameFragment.getInstance(),
            InputNameFragment::class.java.name
        )
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