package com.zion830.threedollars.splash

import android.animation.Animator
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.login.LoginActivity
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.ResultWrapper

class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override fun initView() {
        binding.lottieView.playAnimation()
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                // do nothing
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (SharedPrefUtils.getKakaoAccessToken().isNullOrEmpty()) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    tryServiceLogin(SharedPrefUtils.getKakaoAccessToken()!!)
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
                // do nothing
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // do nothing
            }
        })
    }

    private fun tryServiceLogin(token: String) {
        viewModel.tryLogin(token)

        viewModel.loginResult.observe(this) {
            when (it) {
                is ResultWrapper.Success -> {
                    SharedPrefUtils.saveAccessToken(it.value?.token)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is ResultWrapper.GenericError -> {
                    if (it.code == 400) {
                        viewModel.refreshToken()
                    }
                    if (it.code in 401..499) {
                        startActivity(Intent(this, LoginActivity::class.java))
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
                }
                is ResultWrapper.NetworkError -> {
                    showToast(R.string.login_failed)
                }
            }
        }
    }
}