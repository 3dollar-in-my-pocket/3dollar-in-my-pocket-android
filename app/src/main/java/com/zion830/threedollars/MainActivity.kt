package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
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
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectId
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.analytics.sendClick
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.getCurrentDate
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.showSnack
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.utils.AdvertisementsPosition
import com.threedollar.common.utils.GlobalEvent
import com.threedollar.common.utils.SharedPrefUtils
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.popup.PopupViewModel
import com.zion830.threedollars.ui.webview.WebActivity
import com.zion830.threedollars.utils.isLocationServiceEnabled
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>({ ActivityHomeBinding.inflate(it) }) {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: UserInfoViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val popupViewModel: PopupViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()


    private lateinit var navHostFragment: NavHostFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /** 딥링크로 탭을 옮기는 동안에는 사용자 탭이 아니므로 클릭 로그를 보내지 않는다. */
    private var isTabChangingByDeepLink = false

    override fun initView() {
        setDarkSystemBars()
        // 정상적으로 메인에 진입했으므로 다음 세션 만료를 다시 감지할 수 있도록 초기화한다.
        GlobalEvent.resetLogoutEvent()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (isLocationAvailable() && isLocationServiceEnabled()) {
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
            binding.container.showSnack(it, color = DesignSystemR.color.color_main_red)
        }
        initFlow()
        initNavController(navController)
        initNavView()
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        // Fragment에서 page_view 전송중
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
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
            }
        }
    }

    private fun initNavView() {
        binding.navView.setOnItemSelectedListener {
            sendTabClickLog(it.itemId)
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
        // 이미 선택된 탭을 다시 눌렀을 때는 setOnItemSelectedListener 가 호출되지 않으므로 따로 받는다.
        binding.navView.setOnItemReselectedListener { sendTabClickLog(it.itemId) }
        navigateToMedalPageWithDeepLink(intent)
    }

    private fun sendTabClickLog(itemId: Int) {
        if (isTabChangingByDeepLink) return
        val objectId = when (itemId) {
            R.id.navigation_home -> LogObjectId.HOME
            R.id.navigation_write -> LogObjectId.WRITE
            R.id.navigation_vote -> LogObjectId.COMMUNITY
            R.id.navigation_mypage -> LogObjectId.MY_PAGE
            else -> return
        }
        LogManager.sendClick(
            screen = ScreenName.MAIN_TAB_BAR,
            objectType = LogObjectType.TAB,
            objectId = objectId
        )
    }

    private fun selectTabWithoutClickLog(itemId: Int) {
        isTabChangingByDeepLink = true
        try {
            binding.navView.selectedItemId = itemId
        } finally {
            isTabChangingByDeepLink = false
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigateToMedalPageWithDeepLink(intent)
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
                    ContextCompat.getColor(this, DesignSystemR.color.gray90)
                } else {
                    ContextCompat.getColor(this, DesignSystemR.color.gray20)
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
        } else if (binding.navHostFragment.findNavController().currentDestination?.id == R.id.navigation_write) {
            super.onBackPressed()
        }  else if (binding.navHostFragment.findNavController().currentDestination?.id != R.id.navigation_home) {
            binding.navHostFragment.findNavController().navigate(R.id.navigation_home)
        } else {
            super.onBackPressed()
        }
    }

    private fun navigateToMedalPageWithDeepLink(intent: Intent) {
        if (intent.getStringExtra(DynamicLinkActivity.MEDAL).isNotNullOrEmpty()) {
            binding.navView.post {
                myPageViewModel.isMoveMedalPage = true
                selectTabWithoutClickLog(R.id.navigation_mypage)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.COMMUNITY).isNotNullOrEmpty()) {
            binding.navView.post {
                selectTabWithoutClickLog(R.id.navigation_vote)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.HOME).isNotNullOrEmpty()) {
            binding.navView.post {
                selectTabWithoutClickLog(R.id.navigation_home)
            }
            intent.getStringExtra(DynamicLinkActivity.HOME_PRESET)?.let { preset ->
                homeViewModel.applyPreset(preset)
            }
        } else if (intent.hasExtra(DynamicLinkActivity.STORE_PREVIEW)) {
            binding.navView.post {
                selectTabWithoutClickLog(R.id.navigation_home)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.BROWSER).isNotNullOrEmpty()) {
            val url = intent.getStringExtra(DynamicLinkActivity.BROWSER).orEmpty()
            startActivity(WebActivity.getIntent(this, url))
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
