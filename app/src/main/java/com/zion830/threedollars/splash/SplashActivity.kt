package com.zion830.threedollars.splash

import android.animation.Animator
import android.content.Intent
import android.util.Log
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
                if (SharedPrefUtils.getKakaoToken().isNullOrEmpty()) {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                } else {
                    tryServiceLogin(SharedPrefUtils.getKakaoToken()!!)
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
            Log.d("login", it.toString())
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
                            .setTitle("서버 점검 중!")
                            .setMessage("이용에 불편을 드려 죄송합니다. 나중에 다시 만나요..ㅎㅎ!")
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