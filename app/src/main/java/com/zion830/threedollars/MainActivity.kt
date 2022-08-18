package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.home.HomeFragment
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.vm.StreetStoreByMenuViewModel
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.requestPermissionFirst
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.showSnack
import zion830.com.common.listener.OnBackPressedListener


class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>(R.layout.activity_home),
    ActivityCompat.OnRequestPermissionsResultCallback {

    override val viewModel: UserInfoViewModel by viewModels()

    private val popupViewModel: PopupViewModel by viewModels()

    private val streetStoreByMenuViewModel: StreetStoreByMenuViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment

    override fun initView() {
        requestPermissionFirst()
        popupViewModel.getPopups(position = "SPLASH")
        if (SharedPrefUtils.getCategories().isEmpty()) {
            streetStoreByMenuViewModel.loadCategories()
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.itemIconTintList = null
        binding.navView.setupWithNavController(navController)

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = R.color.color_main_red)
        }
        popupViewModel.popups.observe(this) { popups ->
            if (popups.isNotEmpty() && popups[0].linkUrl != SharedPrefUtils.getPopupUrl()) {
                binding.navHostFragment.findNavController().navigate(R.id.navigation_popup)
            }
        }
        initNavController(navController)
        initNavView()
    }

    private fun initNavView() {
        binding.navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_street -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_street)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_truck -> {
                    if (binding.navHostFragment.findNavController().currentDestination?.id == R.id.navigation_home) {
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                        val homeFragment: HomeFragment? =
                            navHostFragment?.childFragmentManager?.fragments?.get(0) as? HomeFragment
                        startActivity(
                            NewStoreActivity.getInstance(
                                this,
                                homeFragment?.getMapCenterLatLng()
                            )
                        )
                    } else {
                        startActivity(NewStoreActivity.getInstance(this, null))
                    }
                }
                R.id.navigation_mypage -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_mypage)
                    binding.navView.itemBackgroundResource = android.R.color.black
                }
            }
            true
        }
    }

    private fun initNavController(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView.itemBackgroundResource = if (destination.id == R.id.navigation_mypage) {
                android.R.color.black
            } else {
                android.R.color.white
            }
            binding.divider.setBackgroundColor(
                if (destination.id == R.id.navigation_mypage) {
                    ContextCompat.getColor(this, R.color.gray90)
                } else {
                    Color.TRANSPARENT
                }
            )
            binding.navView.isVisible = destination.id != R.id.navigation_popup
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5000) {
            if (resultCode != Activity.RESULT_OK) {
                showToast("앱 업데이트가 필요합니다!")
                finish()
            }
        } else {
            navHostFragment.childFragmentManager.fragments.forEach { fragment ->
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            navHostFragment.childFragmentManager.fragments.forEach { fragment ->
                fragment.onActivityResult(
                    Constants.GET_LOCATION_PERMISSION,
                    Activity.RESULT_OK,
                    null
                )
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
        if (binding.navHostFragment.findNavController().currentDestination?.id == R.id.navigation_popup) {
            binding.navHostFragment.findNavController().navigateUp()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
