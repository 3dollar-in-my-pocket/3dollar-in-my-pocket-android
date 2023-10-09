package com.zion830.threedollars

import android.Manifest
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
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.mypage.vm.MyPageViewModel
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.store_detail.vm.StreetStoreByMenuViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.requestPermissionFirst
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseActivity
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.showSnack


@AndroidEntryPoint
class MainActivity : LegacyBaseActivity<ActivityHomeBinding, UserInfoViewModel>(R.layout.activity_home),
    ActivityCompat.OnRequestPermissionsResultCallback {

    override val viewModel: UserInfoViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val popupViewModel: PopupViewModel by viewModels()

    private val streetStoreByMenuViewModel: StreetStoreByMenuViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment

    override fun initView() {
        requestPermissionFirst()
        popupViewModel.getPopups(position = "SPLASH")
        if (LegacySharedPrefUtils.getCategories().isEmpty()) {
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
            if (popups.isNotEmpty() && popups[0].linkUrl != LegacySharedPrefUtils.getPopupUrl()) {
                binding.navHostFragment.findNavController().navigate(R.id.navigation_popup)
            }
        }
        initNavController(navController)
        initNavView()
    }

    private fun initNavView() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_write -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_write)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_vote -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_vote)
                    binding.navView.itemBackgroundResource = android.R.color.white
                }
                R.id.navigation_mypage -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_mypage)
                    binding.navView.itemBackgroundResource = android.R.color.black
                }
            }
            true
        }
        navigateToMedalPageWithDeepLink()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToMedalPageWithDeepLink()
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
        if (permissions.isEmpty()) return
        val locationIndex = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (grantResults[locationIndex] == PackageManager.PERMISSION_GRANTED) {
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

    private fun navigateToMedalPageWithDeepLink() {
        if (intent.getStringExtra("medal").isNotNullOrEmpty()) {
            binding.navView.post {
                myPageViewModel.isMoveMedalPage = true
                binding.navView.selectedItemId = R.id.navigation_mypage
            }
        }
    }

    companion object {

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
