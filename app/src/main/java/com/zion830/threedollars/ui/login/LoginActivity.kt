package com.zion830.threedollars.ui.login

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.zion830.threedollars.*
import com.zion830.threedollars.Constants.GOOGLE_SIGN_IN
import com.zion830.threedollars.databinding.ActivityLoginBinding
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.ui.login.name.InputNameActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.LegacyBaseActivity
import com.threedollar.common.base.ResultWrapper
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class LoginActivity : LegacyBaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override val viewModel: LoginViewModel by viewModels()
    private val inputNameLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            startActivity(MainActivity.getIntent(this))
            finish()
        }
    }

    override fun initView() {
        collectFlows()
        binding.btnLoginKakao.onSingleClick {
            EventTracker.logEvent(Constants.KAKAO_BTN_CLICKED)
            LegacySharedPrefUtils.saveLoginType(LoginType.KAKAO)
            tryLoginBySocialType()
        }
        binding.btnLoginGoogle.onSingleClick {
            EventTracker.logEvent(Constants.GOOGLE_BTN_CLICKED)
            LegacySharedPrefUtils.saveLoginType(LoginType.GOOGLE)
            tryLoginBySocialType()
        }
    }

    private fun tryLoginBySocialType() {
        if (LegacySharedPrefUtils.getLoginType() == LoginType.KAKAO.socialName) {
            tryKakaoLogin()
        } else {
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
                        account?.account!!,
                        "oauth2:https://www.googleapis.com/auth/plus.me"
                    )
                LegacySharedPrefUtils.saveLoginType(LoginType.GOOGLE)
                LegacySharedPrefUtils.saveGoogleToken(token)
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

    private fun tryLoginBySocialType(token: OAuthToken) {
        UserApiClient.instance.me { user, _ ->
            user?.let {
                Log.d(localClassName, it.groupUserToken.toString())
                LegacySharedPrefUtils.saveGoogleToken(token.accessToken)
                viewModel.tryLogin(LoginType.KAKAO, token.accessToken)
            }
        }
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.loginResult.collect {
                        when (it) {
                            is ResultWrapper.Success -> {
                                LegacySharedPrefUtils.saveUserId(it.value?.userId ?: 0)
                                LegacySharedPrefUtils.saveAccessToken(it.value?.token)
                                FirebaseMessaging.getInstance().token.addOnCompleteListener { firebaseToken ->
                                    if (firebaseToken.isSuccessful) {
                                        viewModel.putPushInformationToken(PushInformationTokenRequest(pushToken = firebaseToken.result))
                                    }
                                }
                                startActivity(MainActivity.getIntent(this@LoginActivity))
                                finish()
                            }

                            is ResultWrapper.GenericError -> {
                                when (it.code) {
                                    400 -> showToast(R.string.connection_failed)
                                    404 -> inputNameLauncher.launch(Intent(this@LoginActivity, InputNameActivity::class.java))
                                    503 -> showToast(R.string.server_500)
                                    500, 502 -> showToast(R.string.connection_failed)
                                    else -> showToast(R.string.connection_failed)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.isNameUpdated.collect {
                        if (it) {
                            startActivity(MainActivity.getIntent(this@LoginActivity))
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun tryKakaoLogin() {
        val loginResCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(localClassName, "로그인 실패", error)
                showToast(R.string.error_no_kakao_login)
            } else if (token != null) {
                LegacySharedPrefUtils.saveLoginType(LoginType.KAKAO)
                LegacySharedPrefUtils.saveKakaoToken(token.accessToken, token.refreshToken)
                Log.d(localClassName, token.toString())
                tryLoginBySocialType(token)
            }
        }

        if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
            LoginClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
        } else {
            LoginClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
        }
    }
}