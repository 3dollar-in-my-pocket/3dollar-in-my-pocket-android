package com.zion830.threedollars

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.utils.requestPermissionFirst
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.showSnack

class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>(R.layout.activity_home) {

    override val viewModel: UserInfoViewModel by viewModels()

    override fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.itemIconTintList = null
        binding.navView.setupWithNavController(navController)

        requestPermissionFirst()

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = R.color.color_main_red)
        }
    }

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
