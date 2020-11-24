package com.zion830.threedollars.splash

import android.animation.Animator
import android.content.Intent
import androidx.activity.viewModels
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivitySplashBinding
import com.zion830.threedollars.login.LoginActivity
import com.zion830.threedollars.utils.SharedPrefUtils
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(R.layout.activity_splash) {

    override val viewModel: SplashViewModel by viewModels()

    override fun initView() {
        binding.lottieView.playAnimation()
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                // do nothing
            }

            override fun onAnimationEnd(animation: Animator?) {
                selectStartActivity()
            }

            override fun onAnimationCancel(animation: Animator?) {
                // do nothing
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // do nothing
            }
        })
    }

    fun selectStartActivity() {
        startActivity(
            when {
                SharedPrefUtils.getAccessToken() == null -> Intent(this, LoginActivity::class.java)
                else -> Intent(this, MainActivity::class.java)
            }
        )
        finish()
    }
}