package com.zion830.threedollars.ui.login.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.base.ResultWrapper
import com.threedollar.common.utils.Constants.CLICK_SIGN_UP
import com.threedollar.common.utils.Constants.GOOGLE_SIGN_IN
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.ActivityLoginNameBinding
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.login.viewModel.InputNameViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class SignUpActivity :
    BaseActivity<ActivityLoginNameBinding, InputNameViewModel>({ ActivityLoginNameBinding.inflate(it) }) {

    override val viewModel: InputNameViewModel by viewModels()

    override fun initView() {
        setDarkSystemBars()
        initEditTextView()
        initButton()
        initFlow()
        viewModel.isAlreadyUsed.observe(this) {
            binding.tvAlreadyExist.isVisible = it > 0
            if (it != -1) {
                binding.tvAlreadyExist.text = getString(it)
            }
        }

        viewModel.isNameEmpty.observe(this) {
            binding.btnFinish.apply {
                isClickable = !it
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (it) DesignSystemR.drawable.ic_start_off else DesignSystemR.drawable.ic_start, 0)
                setTextColor(resources.getColor(if (it) DesignSystemR.color.color_gray2 else DesignSystemR.color.color_main_red, null))
            }
        }
        viewModel.isAvailable.observe(this) {
            binding.btnFinish.text = if (it) getString(CommonR.string.login_name3) else getString(CommonR.string.login_name_fail)
            binding.tvAlreadyExist.visibility = if (it) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SignUpActivity", screenName = "sign_up")
    }

    private fun initEditTextView() {
        binding.etName.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            val handler = Handler()
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    binding.scrollView.smoothScrollTo(0, binding.btnFinish.bottom)
                    handler.postDelayed(this, 10)
                }
            }
            handler.postDelayed(runnable, 10)
        }
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.userName.value = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.isMarketing.collect {
                        if (it) {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                launch {
                    viewModel.signUpResult.collect {
                        when (it) {
                            is ResultWrapper.Success -> {
                                LegacySharedPrefUtils.saveUserId(it.value?.userId ?: 0)
                                LegacySharedPrefUtils.saveAccessToken(it.value?.token)
                                showMarketingDialog()
                            }
                            is ResultWrapper.GenericError -> {
                                when (it.code) {
                                    409 -> {
                                        binding.tvAlreadyExist.isVisible = true
                                        binding.tvAlreadyExist.text = getString(CommonR.string.login_name_already_exist)
                                    }
                                    400 -> {
                                        binding.tvAlreadyExist.isVisible = true
                                        binding.tvAlreadyExist.text = getString(CommonR.string.invalidate_name)
                                    }
                                    else -> showToast(CommonR.string.connection_failed)
                                }
                            }
                            ResultWrapper.NetworkError -> showToast(CommonR.string.connection_failed)
                        }
                    }
                }
            }
        }
    }

    private fun initButton() {
        binding.btnBack.onSingleClick {
            finish()
        }

        binding.btnFinish.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "sign_up")
                putString("nickname", binding.etName.text.toString())
            }
            EventTracker.logEvent(CLICK_SIGN_UP, bundle)
            if (LegacySharedPrefUtils.getLoginType() == LoginType.KAKAO.socialName) {
                tryKakaoLogin()
            } else {
                tryGoogleLogin()
            }
        }
    }

    private fun tryKakaoLogin() {
        val loginResCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(localClassName, "로그인 실패", error)
                showToast(CommonR.string.error_no_kakao_login)
            } else if (token != null) {
                Log.d(localClassName, token.toString())
                tryLoginBySocialType(token)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = loginResCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = loginResCallback)
        }
    }
    private fun tryLoginBySocialType(token: OAuthToken) {
        UserApiClient.instance.me { user, _ ->
            user?.let {
                Log.d(localClassName, it.groupUserToken.toString())
                viewModel.trySignUp(token.accessToken)
            }
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
                        "oauth2:https://www.googleapis.com/auth/plus.me",
                    )
                viewModel.trySignUp(token)
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
                showToast(CommonR.string.login_failed)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            showToast(CommonR.string.login_failed)
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.putPushInformation(pushToken = it.result, isMarketing = isMarketing)
                    }
                }
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    companion object {
        fun getInstance() = SignUpActivity()
    }
}