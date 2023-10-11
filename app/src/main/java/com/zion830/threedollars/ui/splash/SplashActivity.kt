package com.zion830.threedollars.ui.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.messaging.FirebaseMessaging
import com.threedollar.common.base.ResultWrapper
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.datasource.model.v2.request.PushInformationTokenRequest
import com.zion830.threedollars.ui.VersionUpdateDialog
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.login.LoginActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.VersionChecker
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.LegacyBaseActivity

@AndroidEntryPoint
class SplashActivity :
    LegacyBaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override fun initView() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.putPushInformationToken(PushInformationTokenRequest(pushToken = it.result))
            }
        }
        lifecycleScope.launch {
            delay(2000L)
            VersionChecker.checkForceUpdateAvailable(this@SplashActivity,
                { minimum, current ->
                    VersionUpdateDialog.getInstance(minimum, current)
                        .show(supportFragmentManager, VersionUpdateDialog::class.java.name)
                }, {
                    if (LegacySharedPrefUtils.getLoginType().isNullOrBlank()) {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        tryServiceLogin()
                    }
                })
        }

        initFlow()
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_ERROR_REQUEST_CODE) {
            if (resultCode == 200) {
                val account =
                    GoogleSignIn.getLastSignedInAccount(GlobalApplication.getContext())
                if (account != null && account.idToken != null)
                    try {
                        viewModel.refreshGoogleToken(account)
                    } catch (e: UserRecoverableAuthException) {
                        e.intent?.let {
                            startActivityForResult(it, GOOGLE_LOGIN_ERROR_REQUEST_CODE)
                        }
                    }
            }
        }
    }

    private fun tryServiceLogin() {
        when (LegacySharedPrefUtils.getLoginType()) {
            LoginType.KAKAO.socialName -> {
                viewModel.refreshKakaoToken()
            }

            LoginType.GOOGLE.socialName -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val account =
                        GoogleSignIn.getLastSignedInAccount(GlobalApplication.getContext())
                    if (account != null && account.idToken != null) {
                        try {
                            viewModel.refreshGoogleToken(account)
                        } catch (e: UserRecoverableAuthException) {
                            e.intent?.let {
                                startActivityForResult(it, 1001)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }

        collectFlows()

    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.loginResult.collect {
                        when (it) {
                            is ResultWrapper.Success -> {
                                LegacySharedPrefUtils.saveAccessToken(it.value?.token)
                                val deepLink = intent.getStringExtra(STORE_TYPE) ?: intent.getStringExtra(PUSH_LINK) ?: ""
                                when {
                                    deepLink == getString(R.string.scheme_host_kakao_link_food_truck_type) -> {
                                        startActivity(
                                            FoodTruckStoreDetailActivity.getIntent(
                                                this@SplashActivity,
                                                deepLinkStoreId = intent.getStringExtra(STORE_ID)
                                            )
                                        )
                                    }

                                    deepLink == getString(R.string.scheme_host_kakao_link_road_food_type) -> {
                                        startActivity(
                                            StoreDetailActivity.getIntent(
                                                this@SplashActivity,
                                                deepLinkStoreId = intent.getStringExtra(STORE_ID)
                                            )
                                        )
                                    }

                                    deepLink.contains("dollars") -> {
                                        startActivity(Intent(this@SplashActivity, DynamicLinkActivity::class.java).apply {
                                            putExtra("link", deepLink)
                                        })
                                    }

                                    else -> {
                                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                    }

                                }
                                finish()
                            }

                            is ResultWrapper.GenericError -> {
                                if (it.code == 400) {
                                    showToast(R.string.login_failed)
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                }
                                if (it.code in 401..499) {
                                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                                    finish()
                                }
                                if (it.code == 503) {
                                    showAlertDialog()
                                }
                                if (it.code in 500..599) {
                                    showAlertDialog(it.msg)
                                }
                            }

                            is ResultWrapper.NetworkError -> {
                                showToast(R.string.login_failed)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showAlertDialog(msg: String? = null) {
        AlertDialog.Builder(this@SplashActivity)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .setTitle(msg ?: getString(R.string.server_500))
            .setMessage(msg ?: getString(R.string.server_500_msg))
            .setCancelable(false)
            .create()
            .show()
    }

    companion object {
        private const val STORE_ID = "storeId"
        private const val STORE_TYPE = "STORE_TYPE"
        private const val PUSH_LINK = "link"
        private const val GOOGLE_LOGIN_ERROR_REQUEST_CODE = 1001

        fun getIntent(
            context: Context,
            deepLink: Uri? = null,
            storeType: String? = null,
        ) =
            Intent(context, SplashActivity::class.java).apply {
                deepLink?.let {
                    val deepLinkStoreId = it.getQueryParameter(STORE_ID) ?: run {
                        // 딥링크 name 파라미터 없음 오류 로그남기기
                        // DeepLink does'nt have name query parameter
                        ""
                    }
                    putExtra(STORE_ID, deepLinkStoreId)
                }
                storeType?.let {
                    putExtra(STORE_TYPE, it)
                }
            }
    }
}