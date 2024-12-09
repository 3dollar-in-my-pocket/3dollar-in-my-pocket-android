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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.my.presentation.page.MyPageViewModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.getCurrentDate
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.showSnack
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.GlobalEvent
import com.threedollar.common.utils.SharedPrefUtils
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.splash.ui.SplashActivity
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.requestPermissionFirst
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>({ ActivityHomeBinding.inflate(it) }),
    ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: UserInfoViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val popupViewModel: PopupViewModel by viewModels()


    private lateinit var navHostFragment: NavHostFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        requestPermissionFirst()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (isLocationAvailable() && isGpsAvailable()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                if (it != null) {
                    popupViewModel.getPopups(
                        position = AdvertisementsPosition.SPLASH,
                        latLng = LatLng(it.latitude, it.longitude)
                    )
                }
            }
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.itemIconTintList = null
        binding.navView.setupWithNavController(navController)

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = R.color.color_main_red)
        }
        initFlow()
        initNavController(navController)
        initNavView()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MainActivity", screenName = null)
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    popupViewModel.popups.collect { popups ->
                        if (popups.isNotEmpty() && getCurrentDate() != sharedPrefUtils.getTodayNotPopupDate()) {
                            binding.navHostFragment.findNavController().navigate(R.id.navigation_popup)
                        }
                    }
                }
                launch {
                    popupViewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                launch {
                    GlobalEvent.logoutEvent.collect {
                        if(it) {
                            startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                            finish()
                            GlobalEvent.resetLogoutEvent()
                        }
                    }
                }
            }
        }

    }

    private fun initNavView() {
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
                    binding.navView.itemBackgroundResource = android.R.color.white
                    showBottomNavigation(true)
                }

                R.id.navigation_write -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_write)
                    binding.navView.itemBackgroundResource = android.R.color.white
                    showBottomNavigation(false)
                }

                R.id.navigation_vote -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_vote)
                    binding.navView.itemBackgroundResource = android.R.color.white
                    showBottomNavigation(true)
                }

                R.id.navigation_mypage -> {
                    binding.navHostFragment.findNavController().navigate(R.id.navigation_mypage)
                    binding.navView.itemBackgroundResource = android.R.color.black
                    showBottomNavigation(true)
                }
            }
            true
        }
        navigateToMedalPageWithDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            navigateToMedalPageWithDeepLink(it)
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
        grantResults: IntArray,
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
                return
            }
        }
        if (binding.navHostFragment.findNavController().currentDestination?.id == R.id.navigation_popup) {
            binding.navHostFragment.findNavController().navigateUp()
        } else if (binding.navHostFragment.findNavController().currentDestination?.id != R.id.navigation_home) {
            binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToMedalPageWithDeepLink(intent: Intent) {
        if (intent.getStringExtra(DynamicLinkActivity.MEDAL).isNotNullOrEmpty()) {
            binding.navView.post {
                myPageViewModel.isMoveMedalPage = true
                binding.navView.selectedItemId = R.id.navigation_mypage
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.COMMUNITY).isNotNullOrEmpty()) {
            binding.navView.post {
                binding.navView.selectedItemId = R.id.navigation_vote
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.HOME).isNotNullOrEmpty()) {
            binding.navView.post {
                binding.navView.selectedItemId = R.id.navigation_home
            }
        }
    }

    fun showBottomNavigation(state: Boolean) {
        if (isBindingInitialized()) {
            binding.navView.isVisible = state
        }
    }


    companion object {

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
