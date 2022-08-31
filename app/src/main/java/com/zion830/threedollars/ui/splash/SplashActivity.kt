package com.zion830.threedollars.ui.splash

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.repository.model.LoginType
import com.zion830.threedollars.ui.VersionUpdateDialog
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.login.LoginActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.VersionChecker
import com.zion830.threedollars.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.ResultWrapper

class SplashActivity :
    BaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override fun initView() {
        binding.lottieView.playAnimation()
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                // do nothing
            }

            override fun onAnimationEnd(animation: Animator) {
                VersionChecker.checkForceUpdateAvailable(this@SplashActivity,
                    { minimum, current ->
                        VersionUpdateDialog.getInstance(minimum, current)
                            .show(supportFragmentManager, VersionUpdateDialog::class.java.name)
                    }, {
                        if (SharedPrefUtils.getLoginType().isNullOrBlank()) {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            tryServiceLogin()
                        }
                    })
            }

            override fun onAnimationCancel(animation: Animator) {
                // do nothing
            }

            override fun onAnimationRepeat(animation: Animator) {
                // do nothing
            }
        })
    }

    private fun tryServiceLogin() {
        when (SharedPrefUtils.getLoginType()) {
            LoginType.KAKAO.socialName -> {
                viewModel.refreshKakaoToken()
            }
            LoginType.GOOGLE.socialName -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val account =
                        GoogleSignIn.getLastSignedInAccount(GlobalApplication.getContext())
                    if (account != null && account.idToken != null) {
                        viewModel.refreshGoogleToken(account)
                    } else {
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }

        viewModel.loginResult.observe(this) {
            when (it) {
                is ResultWrapper.Success -> {
                    SharedPrefUtils.saveAccessToken(it.value?.token)

                    when (intent.getStringExtra(STORE_TYPE)) {
                        getString(R.string.scheme_host_kakao_link_food_truck_type) -> {
                            startActivity(
                                FoodTruckStoreDetailActivity.getIntent(
                                    this,
                                    deepLinkStoreId = intent.getStringExtra(STORE_ID)
                                )
                            )
                        }
                        getString(R.string.scheme_host_kakao_link_road_food_type) -> {
                            startActivity(
                                StoreDetailActivity.getIntent(
                                    this,
                                    deepLinkStoreId = intent.getStringExtra(STORE_ID)
                                )
                            )
                        }
                        else -> {
                            startActivity(Intent(this, MainActivity::class.java))
                        }

                    }
                    finish()
                }
                is ResultWrapper.GenericError -> {
                    if (it.code == 400) {
                        showToast(R.string.login_failed)
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    if (it.code in 401..499) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    if (it.code == 503) {
                        AlertDialog.Builder(this)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                finish()
                            }
                            .setTitle(getString(R.string.server_500))
                            .setMessage(getString(R.string.server_500_msg))
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                    if (it.code in 500..599) {
                        AlertDialog.Builder(this)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                finish()
                            }
                            .setTitle(it.msg ?: getString(R.string.server_500))
                            .setMessage(it.msg ?: getString(R.string.server_500_msg))
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                }
                is ResultWrapper.NetworkError -> {
                    showToast(R.string.login_failed)
                }
            }
        }
    }

    companion object {
        private const val STORE_ID = "storeId"
        private const val STORE_TYPE = "STORE_TYPE"

        fun getIntent(
            context: Context,
            deepLink: Uri? = null,
            storeType: String? = null
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