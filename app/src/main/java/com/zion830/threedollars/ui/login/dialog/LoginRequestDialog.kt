package com.zion830.threedollars.ui.login.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogBottomLoginRequestBinding
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.ui.login.LoginViewModel
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.ResultWrapper
import zion830.com.common.base.onSingleClick


@AndroidEntryPoint
class LoginRequestDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogBottomLoginRequestBinding
    private val viewModel: LoginViewModel by viewModels()
    private var callBack:(Boolean) ->Unit = {}
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogBottomLoginRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        observeUiData()
        binding.btnLoginKakao.onSingleClick {
            EventTracker.logEvent(Constants.KAKAO_BTN_CLICKED)
            SharedPrefUtils.saveLoginType(LoginType.KAKAO)
            tryLoginBySocialType()
        }
        binding.btnLoginGoogle.onSingleClick {
            EventTracker.logEvent(Constants.GOOGLE_BTN_CLICKED)
            SharedPrefUtils.saveLoginType(LoginType.GOOGLE)
            tryLoginBySocialType()
        }
        binding.closeImage.onSingleClick {
            dismiss()
        }
    }

    private fun tryLoginBySocialType() {
        if (SharedPrefUtils.getLoginType() == LoginType.KAKAO.socialName) {
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
                    startActivityForResult(intent, Constants.GOOGLE_SIGN_IN)
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
                SharedPrefUtils.saveLoginType(LoginType.GOOGLE)
                SharedPrefUtils.saveGoogleToken(token)
                viewModel.tryLogin(LoginType.GOOGLE, token)
            }
        } catch (e: Exception) {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
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
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { firebaseToken ->
                        if (firebaseToken.isSuccessful) {
                            viewModel.putPushInformationToken(PushInformationTokenRequest(pushToken = firebaseToken.result))
                        }
                    }
                    callBack.invoke(true)
                    dismiss()
                }
                is ResultWrapper.GenericError -> {
                    when (it.code) {
                        400 -> showToast(R.string.connection_failed)
                        404 -> callBack.invoke(false)
                        503 -> showToast(R.string.server_500)
                        500, 502 -> showToast(R.string.connection_failed)
                        else -> showToast(R.string.connection_failed)
                    }
                }
            }
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                callBack.invoke(true)
                dismiss()
            }
        }
    }

    private fun tryKakaoLogin() {
        val loginResCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                showToast(R.string.error_no_kakao_login)
            } else if (token != null) {
                SharedPrefUtils.saveLoginType(LoginType.KAKAO)
                SharedPrefUtils.saveKakaoToken(token.accessToken, token.refreshToken)
                tryLoginBySocialType(token)
            }
        }
        context?.apply {
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
            }
        }
    }

    fun setLoginCallBack(callBack:(Boolean) -> Unit):LoginRequestDialog{
        this.callBack = callBack
        return this
    }

}