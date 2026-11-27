package com.zion830.threedollars

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zion830.threedollars.ui.home.data.HomeDeepLinkRequest
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.threedollar.common.R as CommonR
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.LogObjectType
import com.threedollar.common.analytics.SDClickLogger
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
import com.zion830.threedollars.ui.main.MainTab
import com.zion830.threedollars.ui.main.MainTabDestination
import com.zion830.threedollars.ui.main.MainTabResolver
import com.zion830.threedollars.ui.main.MainTabStore
import com.zion830.threedollars.ui.main.WebTabFragment
import com.zion830.threedollars.ui.main.WebTabUrl
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

    @Inject
    lateinit var mainTabStore: MainTabStore

    override val viewModel: UserInfoViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val popupViewModel: PopupViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()


    private lateinit var navHostFragment: NavHostFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    /** 딥링크로 탭을 옮기는 동안에는 사용자 탭이 아니므로 클릭 로그를 보내지 않는다. */
    private var isTabChangingByDeepLink = false

    /** 스플래시가 받아 둔 서버 하단 탭(TH-1650). 응답이 없으면 기본 4탭. */
    private var mainTabs: List<MainTab> = MainTab.defaults

    /** 화면 복원이 아닌 첫 생성인지. 첫 생성일 때만 기본 탭을 고른다. */
    private var isFirstCreate = false

    /** 탭 응답을 받기 전에 들어온 탭 선택 딥링크의 탭 id. 응답을 받아 메뉴를 다시 만든 뒤 고른다. */
    private var pendingTabId: String? = null

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
        mainTabs = MainTabResolver.resolve(mainTabStore.tabs, WebTabUrl.baseUrl(BuildConfig.DEBUG))
        buildTabMenu()
        if (mainTabStore.tabs == null) loadTabsWithoutSplash(navController)

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = DesignSystemR.color.color_main_red)
        }
        initFlow()
        initNavController(navController)
        initNavView()
        onBackPressedDispatcher.addCallback(this, mainBackPressedCallback)
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

    /**
     * 스플래시를 거치지 않고 들어온 경우(외부 링크로 콜드 스타트, 프로세스 재시작 복원) 탭 응답을 직접 받는다.
     * 받으면 메뉴를 다시 만들고, 지금 화면의 탭과 기다리던 탭 딥링크를 반영한다. 실패하면 기본 탭을 유지한다.
     */
    private fun loadTabsWithoutSplash(navController: NavController) {
        lifecycleScope.launch {
            if (!mainTabStore.load()) return@launch
            mainTabs = MainTabResolver.resolve(mainTabStore.tabs, WebTabUrl.baseUrl(BuildConfig.DEBUG))
            buildTabMenu()
            navController.currentBackStackEntry?.let { entry ->
                tabShownAt(entry.destination.id, entry.arguments)?.let(::checkTab)
            }
            pendingTabId?.let { tabId ->
                pendingTabId = null
                selectTabById(tabId)
            }
        }
    }

    /** 화면 이동 없이 선택 표시만 바꾼다. */
    private fun checkTab(tab: MainTab) {
        binding.navView.menu.findItem(tab.menuItemId)?.isChecked = true
    }

    /** 응답 순서대로 탭 메뉴를 만든다. 메뉴 id 는 탭 id 로 정해 화면 복원 때도 같은 탭을 가리킨다. */
    private fun buildTabMenu() {
        val menu = binding.navView.menu
        menu.clear()
        mainTabs.forEachIndexed { index, tab ->
            val label = tab.label.ifBlank { tab.defaultLabelRes?.let(::getString).orEmpty() }
            val item = menu.add(Menu.NONE, tab.menuItemId, index, label)
            tab.defaultIconRes?.let(item::setIcon)
            loadTabIcon(tab)
        }
    }

    /** 서버 아이콘(단색 PNG)을 선택 시 [MainTab.selectedTintColor], 비선택 시 gray40 으로 칠한다. 실패하면 기본 아이콘을 둔다. */
    private fun loadTabIcon(tab: MainTab) {
        val url = tab.iconUrl ?: return
        val sizePx = (TAB_ICON_SIZE_DP * resources.displayMetrics.density).toInt()
        Glide.with(this).asBitmap().load(url).into(object : CustomTarget<Bitmap>(sizePx, sizePx) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val bitmap = resource.copy(resource.config ?: Bitmap.Config.ARGB_8888, false)
                binding.navView.menu.findItem(tab.menuItemId)?.icon = tabIconDrawable(bitmap, tab.selectedColor())
            }

            override fun onLoadCleared(placeholder: Drawable?) = Unit
        })
    }

    private fun tabIconDrawable(bitmap: Bitmap, selectedColor: Int): Drawable = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_checked), BitmapDrawable(resources, bitmap).apply { setTint(selectedColor) })
        addState(
            intArrayOf(),
            BitmapDrawable(resources, bitmap).apply { setTint(ContextCompat.getColor(this@MainActivity, DesignSystemR.color.gray40)) },
        )
    }

    private fun MainTab.selectedColor(): Int =
        selectedTintColor?.let { runCatching { Color.parseColor(it) }.getOrNull() }
            ?: ContextCompat.getColor(this@MainActivity, DesignSystemR.color.red)

    private val MainTab.defaultLabelRes: Int?
        get() = when (destination) {
            MainTabDestination.Home -> CommonR.string.title_home
            MainTabDestination.StoreCreate -> CommonR.string.title_add_store
            MainTabDestination.Community -> CommonR.string.title_community
            MainTabDestination.My -> CommonR.string.title_mypage
            is MainTabDestination.Web, is MainTabDestination.Deeplink -> null
        }

    private val MainTab.defaultIconRes: Int?
        get() = when (destination) {
            MainTabDestination.Home -> DesignSystemR.drawable.selector_home
            MainTabDestination.StoreCreate -> DesignSystemR.drawable.selector_write
            MainTabDestination.Community -> DesignSystemR.drawable.selector_vote
            MainTabDestination.My -> DesignSystemR.drawable.selector_mypage
            is MainTabDestination.Web, is MainTabDestination.Deeplink -> null
        }

    private fun tabOf(menuItemId: Int): MainTab? = mainTabs.firstOrNull { it.menuItemId == menuItemId }

    private fun initNavView() {
        binding.navView.setOnItemSelectedListener { item ->
            val tab = tabOf(item.itemId) ?: return@setOnItemSelectedListener false
            sendTabClickLog(tab)
            openTab(tab)
        }
        // 이미 선택된 탭을 다시 눌렀을 때는 setOnItemSelectedListener 가 호출되지 않으므로 따로 받는다.
        binding.navView.setOnItemReselectedListener { item ->
            val tab = tabOf(item.itemId) ?: return@setOnItemReselectedListener
            sendTabClickLog(tab)
            if (isShowingOtherTabScreen(tab)) openTab(tab)
        }
        // 딥링크 탭 이동보다 먼저 실행되도록 딥링크 처리 전에 예약한다.
        if (isFirstCreate) binding.navView.post { selectInitialTab() }
        navigateToMedalPageWithDeepLink(intent)
    }

    /**
     * 선택 표시는 [tab]인데 화면은 다른 탭 화면인지. 탭 없이 화면만 연 경우(서버가 뺀 탭의 딥링크)나
     * 복원된 웹 탭이 목록에 없는 경우 다시 눌러도 이동하지 않던 문제를 막는다. 탭 하위 화면(리스트뷰 등)은 그대로 둔다.
     */
    private fun isShowingOtherTabScreen(tab: MainTab): Boolean {
        val entry = binding.navHostFragment.findNavController().currentBackStackEntry ?: return false
        if (entry.destination.id !in TAB_DESTINATION_IDS) return false
        return tabShownAt(entry.destination.id, entry.arguments)?.id != tab.id
    }

    /** 탭 화면을 연다. 딥링크 탭은 탭을 선택하지 않고 링크 화면만 띄우므로 false 를 돌려준다. */
    private fun openTab(tab: MainTab): Boolean {
        val navController = binding.navHostFragment.findNavController()
        when (val destination = tab.destination) {
            MainTabDestination.Home -> navController.navigate(R.id.navigation_home)
            MainTabDestination.StoreCreate -> navController.navigate(R.id.navigation_write)
            MainTabDestination.Community -> navController.navigate(R.id.navigation_vote)
            MainTabDestination.My -> navController.navigate(R.id.navigation_mypage)
            is MainTabDestination.Web ->
                navController.navigate(R.id.navigation_web_tab, WebTabFragment.arguments(tab.id, destination.url))

            is MainTabDestination.Deeplink -> {
                DynamicLinkActivity.launch(this, destination.link)
                return false
            }
        }
        showBottomNavigation(tab.destination != MainTabDestination.StoreCreate)
        return true
    }

    /** 기존 GA 탭 클릭 로그(main_tab_bar)는 유지하고, 서버가 내려준 clickLog 도 그대로 보낸다(TH-1650). */
    private fun sendTabClickLog(tab: MainTab) {
        if (isTabChangingByDeepLink) return
        tab.legacyLogObjectId?.let { objectId ->
            LogManager.sendClick(
                screen = ScreenName.MAIN_TAB_BAR,
                objectType = LogObjectType.TAB,
                objectId = objectId
            )
        }
        tab.clickLog?.let(SDClickLogger::send)
    }

    /** 탭바 클릭 로그 없이 가게 제보 탭으로 이동한다. 홈 [+ 가게 제보] 버튼처럼 화면이 따로 로그를 남길 때 쓴다. */
    fun selectWriteTab() {
        selectTabWithoutClickLog(MainTabDestination.StoreCreate)
    }

    /** 서버가 그 화면의 탭을 내려주지 않았으면 탭 선택 없이 화면만 연다. */
    private fun selectTabWithoutClickLog(destination: MainTabDestination) {
        val tab = mainTabs.firstOrNull { it.destination == destination }
        if (tab != null) {
            selectTabWithoutClickLog(tab)
            return
        }
        MainTab.defaults.firstOrNull { it.destination == destination }?.let(::openTab)
    }

    private fun selectTabWithoutClickLog(tab: MainTab) {
        if (!tab.isSelectable) {
            openTab(tab)
            return
        }
        isTabChangingByDeepLink = true
        try {
            binding.navView.selectedItemId = tab.menuItemId
        } finally {
            isTabChangingByDeepLink = false
        }
    }

    /** 탭 선택 딥링크(`tab?id=`). 없는 id 면 아무 일도 하지 않는다. 탭 응답을 아직 못 받았으면 받은 뒤에 고른다. */
    private fun selectTabById(tabId: String) {
        val tab = mainTabs.firstOrNull { it.id.equals(tabId, ignoreCase = true) }
        when {
            tab != null -> selectTabWithoutClickLog(tab)
            mainTabStore.tabs == null -> pendingTabId = tabId
        }
    }

    /** 첫 탭을 기본 선택한다. 홈은 nav graph 시작 화면이라 따로 열지 않는다. */
    private fun selectInitialTab() {
        val initialTab = MainTabResolver.initialTab(mainTabs)
        if (initialTab.destination != MainTabDestination.Home) selectTabWithoutClickLog(initialTab)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isFirstCreate = savedInstanceState == null
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) resumePendingLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigateToMedalPageWithDeepLink(intent)
        resumePendingLink(intent)
    }

    /** TH-1070: 로그인 때문에 멈춘 푸시·외부 링크를 메인 위에서 다시 연다. 한 번만 열도록 extra 를 지운다. */
    private fun resumePendingLink(intent: Intent) {
        val link = intent.getStringExtra(EXTRA_RESUME_LINK)
        if (link.isNullOrBlank()) return
        intent.removeExtra(EXTRA_RESUME_LINK)
        startActivity(DynamicLinkActivity.getExternalIntent(this, link))
    }

    private fun initNavController(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            tabShownAt(destination.id, arguments)?.let(::checkTab)
            binding.navView.itemBackgroundResource = if (destination.id == R.id.navigation_mypage) {
                DesignSystemR.color.gray100
            } else {
                android.R.color.white
            }
            binding.divider.setBackgroundColor(
                if (destination.id == R.id.navigation_mypage) {
                    ContextCompat.getColor(this, DesignSystemR.color.gray80)
                } else {
                    ContextCompat.getColor(this, DesignSystemR.color.gray20)
                }
            )
            binding.navView.isVisible = destination.id != R.id.navigation_popup
        }
    }

    /** 지금 화면이 어느 탭인지. 탭 화면이 아니면(제보 상세·팝업 등) null 이라 선택 상태를 그대로 둔다. */
    private fun tabShownAt(destinationId: Int, arguments: Bundle?): MainTab? = when (destinationId) {
        R.id.navigation_home -> mainTabs.firstOrNull { it.destination == MainTabDestination.Home }
        R.id.navigation_write -> mainTabs.firstOrNull { it.destination == MainTabDestination.StoreCreate }
        R.id.navigation_vote -> mainTabs.firstOrNull { it.destination == MainTabDestination.Community }
        R.id.navigation_mypage -> mainTabs.firstOrNull { it.destination == MainTabDestination.My }
        R.id.navigation_web_tab -> WebTabFragment.tabIdOf(arguments)?.let { id -> mainTabs.firstOrNull { it.id == id } }
        else -> null
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

    /**
     * TH-919: targetSdk 36(Android 16)·predictive back 에서는 [onBackPressed] override 가 호출되지 않는다.
     * 기존 뒤로가기 규칙을 [OnBackPressedCallback] 으로 옮겨 predictive back 사용 여부와 관계없이 같게 동작시킨다.
     */
    private val mainBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handleMainBack()
        }
    }

    private fun handleMainBack() {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                (fragment as OnBackPressedListener).onBackPressed()
                return
            }
        }
        val navController = binding.navHostFragment.findNavController()
        when (navController.currentDestination?.id) {
            R.id.navigation_popup -> navController.navigateUp()
            R.id.navigation_write -> dispatchBackToNextHandler()
            R.id.navigation_home -> dispatchBackToNextHandler()
            else -> navController.navigate(R.id.navigation_home)
        }
    }

    /** 기존 `super.onBackPressed()` 와 같다: 이 콜백을 잠시 끄고 다음 콜백(홈 시트·제보 화면 등) 또는 기본 동작에 넘긴다. */
    private fun dispatchBackToNextHandler() {
        mainBackPressedCallback.isEnabled = false
        try {
            onBackPressedDispatcher.onBackPressed()
        } finally {
            mainBackPressedCallback.isEnabled = true
        }
    }

    private fun navigateToMedalPageWithDeepLink(intent: Intent) {
        if (intent.getStringExtra(DynamicLinkActivity.MEDAL).isNotNullOrEmpty()) {
            binding.navView.post {
                myPageViewModel.isMoveMedalPage = true
                selectTabWithoutClickLog(MainTabDestination.My)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.COMMUNITY).isNotNullOrEmpty()) {
            binding.navView.post {
                selectTabWithoutClickLog(MainTabDestination.Community)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.HOME).isNotNullOrEmpty()) {
            binding.navView.post {
                selectTabWithoutClickLog(MainTabDestination.Home)
            }
            val tabId = intent.getStringExtra(DynamicLinkActivity.HOME_TAB_ID)
            intent.removeExtra(DynamicLinkActivity.HOME_TAB_ID)
            HomeDeepLinkRequest.from(
                preset = intent.getStringExtra(DynamicLinkActivity.HOME_PRESET),
                tabId = tabId,
            )?.let(homeViewModel::applyHomeDeepLink)
        } else if (intent.hasExtra(DynamicLinkActivity.STORE_PREVIEW)) {
            val isStoreLanding = intent.hasExtra(DynamicLinkActivity.STORE_PREVIEW_EXPANDED)
            binding.navView.post {
                if (isStoreLanding) closeScreensAboveHome()
                selectTabWithoutClickLog(MainTabDestination.Home)
            }
        } else if (intent.getStringExtra(DynamicLinkActivity.TAB).isNotNullOrEmpty()) {
            val tabId = intent.getStringExtra(DynamicLinkActivity.TAB).orEmpty()
            intent.removeExtra(DynamicLinkActivity.TAB)
            binding.navView.post { selectTabById(tabId) }
        } else if (intent.getStringExtra(DynamicLinkActivity.BROWSER).isNotNullOrEmpty()) {
            val url = intent.getStringExtra(DynamicLinkActivity.BROWSER).orEmpty()
            startActivity(WebActivity.getIntent(this, url))
        }
    }

    /** 외부 가게 링크로 홈에 랜딩할 때 탭 위에 쌓인 화면(MY 하위 화면 등)과 닫을 수 있는 다이얼로그를 닫는다. */
    private fun closeScreensAboveHome() {
        if (supportFragmentManager.isStateSaved) return
        supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        dismissCancelableDialogs(supportFragmentManager)
    }

    private fun dismissCancelableDialogs(fragmentManager: FragmentManager) {
        fragmentManager.fragments.forEach { fragment ->
            if (fragment is DialogFragment && fragment.isCancelable) {
                fragment.dismissAllowingStateLoss()
            } else {
                dismissCancelableDialogs(fragment.childFragmentManager)
            }
        }
    }

    fun showBottomNavigation(state: Boolean) {
        if (isBindingInitialized()) {
            binding.navView.isVisible = state
        }
    }


    companion object {

        private const val EXTRA_RESUME_LINK = "extra_resume_link"
        private const val TAB_ICON_SIZE_DP = 24
        private val TAB_DESTINATION_IDS = setOf(
            R.id.navigation_home,
            R.id.navigation_write,
            R.id.navigation_vote,
            R.id.navigation_mypage,
            R.id.navigation_web_tab,
        )

        /** [resumeLink]: 로그인 전에 열려던 딥링크. 메인을 띄운 뒤 그 위에서 이어서 연다(TH-1070). */
        fun getIntent(context: Context, resumeLink: String? = null): Intent =
            Intent(context, MainActivity::class.java).apply {
                if (!resumeLink.isNullOrBlank()) putExtra(EXTRA_RESUME_LINK, resumeLink)
            }
    }
}
