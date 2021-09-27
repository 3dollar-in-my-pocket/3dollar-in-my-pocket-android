package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.utils.requestPermissionFirst
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.showSnack
import zion830.com.common.listener.OnBackPressedListener


class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>(R.layout.activity_home), ActivityCompat.OnRequestPermissionsResultCallback {

    override val viewModel: UserInfoViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment

    override fun initView() {
        requestPermissionFirst()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.itemIconTintList = null
        binding.navView.setupWithNavController(navController)

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = R.color.color_main_red)
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            binding.navView.itemBackgroundResource = if (destination.id == R.id.navigation_mypage) {
                android.R.color.black
            } else {
                android.R.color.white
            }
        }

        binding.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_category -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_category)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_review -> {
                    startActivity(Intent(this, NewStoreActivity::class.java))
                }
                R.id.navigation_mypage -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_mypage)
                    binding.navView.itemBackgroundResource = android.R.color.black
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navHostFragment.childFragmentManager.fragments.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            navHostFragment.childFragmentManager.fragments.forEach { fragment ->
                fragment.onActivityResult(Constants.GET_LOCATION_PERMISSION, Activity.RESULT_OK, null)
            }
        }
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                (fragment as OnBackPressedListener).onBackPressed()
            }
        }

        super.onBackPressed()
    }

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
