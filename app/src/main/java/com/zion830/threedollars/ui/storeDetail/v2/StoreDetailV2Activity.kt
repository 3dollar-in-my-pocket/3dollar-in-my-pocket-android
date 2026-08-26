package com.zion830.threedollars.ui.storeDetail.v2

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.activity.SystemBarStyle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseComposeActivity
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.serverdriven.ext.toServerDrivenPlainText
import com.threedollar.common.serverdriven.model.SDClickLogValue
import com.threedollar.common.serverdriven.model.SDCustomActionModel
import com.threedollar.common.serverdriven.model.StoreDetailScreenModel
import com.threedollar.common.serverdriven.model.StoreDetailSectionModel
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.dialog.DeleteStoreDialog
import com.zion830.threedollars.ui.dialog.DirectionBottomDialog
import com.zion830.threedollars.ui.dialog.StorePhotoDialog
import com.zion830.threedollars.ui.dialog.AddReviewDialog
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossReviewWriteActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.MoreImageActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationCategoryArgs
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.ShareFormat
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import com.zion830.threedollars.utils.shareWithKakao
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import base.compose.AppTheme
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class StoreDetailV2Activity : BaseComposeActivity<StoreDetailV2ViewModel>() {

    override val viewModel: StoreDetailV2ViewModel by viewModels()
    private val legacyStoreDetailViewModel: StoreDetailViewModel by viewModels()

    private val storeId: Long by lazy { intent.getLongExtra(EXTRA_STORE_ID, 0L) }
    private val storeType: String? by lazy { intent.getStringExtra(EXTRA_STORE_TYPE) }
    private var initialActionHandled = false
    private lateinit var fragmentContainer: FragmentContainerView

    private val childLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) viewModel.onChildResult(updated = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (storeId <= 0L) {
            finish()
            return
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.WHITE, android.graphics.Color.BLACK),
        )
        val root = FrameLayout(this)
        val composeView = ComposeView(this).apply { id = View.generateViewId() }
        fragmentContainer = FragmentContainerView(this).apply {
            id = View.generateViewId()
            isVisible = false
        }
        ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }
        root.addView(composeView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        root.addView(fragmentContainer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        setContentView(root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finishWithResult()
                }
            }
        })
        composeView.setContent {
            AppTheme {
                StoreDetailV2Screen(
                    viewModel = viewModel,
                    onBack = ::finishWithResult,
                )
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            fragmentContainer.isVisible = supportFragmentManager.backStackEntryCount > 0
            if (!fragmentContainer.isVisible) viewModel.onChildResult(updated = true)
        }
        supportFragmentManager.setFragmentResultListener(EditStoreFragment.STORE_EDITED_RESULT_KEY, this) { _, _ ->
            viewModel.onChildResult(updated = true)
        }
        observeStateAndEvents()
        loadWithOptionalLocation()
    }

    private fun loadWithOptionalLocation() {
        if (!isLocationAvailable()) {
            viewModel.load(storeId, null, null)
            return
        }
        runCatching {
            LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener { location -> viewModel.load(storeId, location?.latitude, location?.longitude) }
                .addOnFailureListener { viewModel.load(storeId, null, null) }
        }.onFailure { viewModel.load(storeId, null, null) }
    }

    private fun observeStateAndEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.events.collect(::handleEvent) }
                launch {
                    legacyStoreDetailViewModel.reviewSuccessEvent.collect { success ->
                        if (success) viewModel.onChildResult(updated = true)
                    }
                }
                launch {
                    legacyStoreDetailViewModel.photoDeleted.collect { success ->
                        if (success) {
                            viewModel.onChildResult(updated = true)
                        } else {
                            showToast(getString(CommonR.string.delete_photo_failed))
                        }
                    }
                }
                launch {
                    legacyStoreDetailViewModel.serverError.collect { message -> message?.let(::showToast) }
                }
                launch {
                    viewModel.uiState.collect { state ->
                        if (state is StoreDetailV2UiState.Content) runInitialActionIfNeeded(state.screen)
                    }
                }
            }
        }
    }

    private fun runInitialActionIfNeeded(screen: StoreDetailScreenModel) {
        if (initialActionHandled) return
        val startCertification = intent.getBooleanExtra(EXTRA_START_CERTIFICATION, false)
        val openReviewWrite = intent.getBooleanExtra(EXTRA_OPEN_REVIEW_WRITE, false)
        if (!startCertification && !openReviewWrite) return
        initialActionHandled = true
        if (startCertification) startCertification(screen) else openReviewWrite(screen = screen)
    }

    private fun handleEvent(event: StoreDetailV2Event) {
        when (event) {
            is StoreDetailV2Event.ShowMessage -> event.message?.let(::showToast)
            is StoreDetailV2Event.CloseContainer -> {
                event.message?.let(::showToast)
                finishWithResult()
            }
            is StoreDetailV2Event.Platform -> handlePlatformAction(event.action)
            is StoreDetailV2Event.ShowReviewReportDialog -> showReviewReportDialog(event)
        }
    }

    private fun handlePlatformAction(action: StoreDetailV2PlatformAction) {
        when (action) {
            is StoreDetailV2PlatformAction.OpenLink -> openLink(action.link.type, action.link.link)
            is StoreDetailV2PlatformAction.Share -> share(action.customAction)
            is StoreDetailV2PlatformAction.Navigation -> showDirections(action.customAction)
            is StoreDetailV2PlatformAction.ReviewWrite -> openReviewWrite(customAction = action.customAction)
            is StoreDetailV2PlatformAction.EditStore -> openEditStore(action.customAction)
            is StoreDetailV2PlatformAction.ReportStore -> showDeleteReasonDialog()
            is StoreDetailV2PlatformAction.AddImage -> childLauncher.launch(MoreImageActivity.getIntent(this, storeId.toInt()))
            is StoreDetailV2PlatformAction.EnlargeImage -> enlargeImage(action.customAction)
            is StoreDetailV2PlatformAction.ReportReview -> viewModel.requestReviewReport(action.customAction)
            is StoreDetailV2PlatformAction.CopyAddress -> copyAddress(action.customAction)
            is StoreDetailV2PlatformAction.EnlargeMap -> enlargeMap(action.customAction)
        }
    }

    private fun openLink(type: String, link: String) {
        if (type == "APP_SCHEME" && link.startsWith("/visit")) {
            currentScreen()?.let(::startCertification)
            return
        }
        val intent = if (type == "APP_SCHEME") {
            Intent(this, DynamicLinkActivity::class.java).putExtra("link", link)
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse(link))
        }
        startActivity(intent)
    }

    private fun openReviewWrite(
        customAction: SDCustomActionModel? = null,
        screen: StoreDetailScreenModel? = currentScreen(),
    ) {
        val resolvedStoreType = customAction?.extraParams?.stringValue("STORE_TYPE")
            ?: storeType
            ?: screen?.sections?.filterIsInstance<StoreDetailSectionModel.Preview>()?.firstOrNull()?.additionalInfos?.storeType
        if (resolvedStoreType == BOSS_STORE) {
            childLauncher.launch(BossReviewWriteActivity.getIntent(this, storeId.toString()))
        } else {
            AddReviewDialog.getInstance(storeId = storeId.toInt())
                .show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
    }

    private fun startCertification(screen: StoreDetailScreenModel) {
        val preview = screen.sections.filterIsInstance<StoreDetailSectionModel.Preview>().firstOrNull()
        val map = screen.sections.filterIsInstance<StoreDetailSectionModel.Map>().firstOrNull()
        val location = map?.location
        if (location == null) {
            showToast(getString(CommonR.string.exist_location_error))
            finishWithResult()
            return
        }
        childLauncher.launch(
            StoreCertificationActivity.getIntent(
                this,
                StoreCertificationArgs(
                    storeId = storeId.toInt(),
                    storeName = preview?.header?.title?.text.orEmpty(),
                    latitude = location.latitude,
                    longitude = location.longitude,
                    categories = preview?.metadata?.primary.orEmpty().mapNotNull { chip ->
                        chip.text.text.takeIf(String::isNotBlank)?.let { name ->
                            StoreCertificationCategoryArgs(name = name, imageUrl = chip.image?.url.orEmpty())
                        }
                    },
                )
            )
        )
    }

    private fun openEditStore(customAction: SDCustomActionModel) {
        val targetId = customAction.extraParams.longValue("STORE_ID") ?: storeId
        fragmentContainer.isVisible = true
        supportFragmentManager.addNewFragment(
            fragmentContainer.id,
            EditStoreFragment.newInstance(targetId.toInt()),
            EditStoreFragment::class.java.name,
            false,
        )
    }

    private fun showDeleteReasonDialog() {
        val resultKey = "store_detail_v2_delete_reason"
        supportFragmentManager.setFragmentResultListener(resultKey, this) { _, result ->
            result.getString(DeleteStoreDialog.RESULT_DELETE_REASON_TYPE)?.let(viewModel::reportMissingStore)
        }
        DeleteStoreDialog.getInstance(resultKey).show(supportFragmentManager, DeleteStoreDialog::class.java.name)
    }

    private fun enlargeImage(customAction: SDCustomActionModel) {
        val imageIndex = customAction.extraParams.longValue("IMAGE_INDEX")?.toInt() ?: 0
        StorePhotoDialog.getInstance(imageIndex, storeId.toInt())
            .show(supportFragmentManager, StorePhotoDialog::class.java.name)
    }

    private fun copyAddress(customAction: SDCustomActionModel) {
        val address = customAction.extraParams.stringValue("ADDRESS") ?: return
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).text = address
        showToast(getString(CommonR.string.address_copied))
    }

    private fun enlargeMap(customAction: SDCustomActionModel) {
        val map = currentScreen()?.sections?.filterIsInstance<StoreDetailSectionModel.Map>()?.firstOrNull()
        val params = customAction.extraParams
        startActivity(
            FullScreenMapActivity.getIntent(
                this,
                params.doubleValue("LATITUDE") ?: map?.location?.latitude,
                params.doubleValue("LONGITUDE") ?: map?.location?.longitude,
                params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStoreName(),
            )
        )
    }

    private fun showDirections(customAction: SDCustomActionModel) {
        val map = currentScreen()?.sections?.filterIsInstance<StoreDetailSectionModel.Map>()?.firstOrNull()
        val params = customAction.extraParams
        DirectionBottomDialog.getInstance(
            params.doubleValue("LATITUDE") ?: map?.location?.latitude,
            params.doubleValue("LONGITUDE") ?: map?.location?.longitude,
            params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStoreName(),
        ).show(supportFragmentManager, DirectionBottomDialog::class.java.name)
    }

    private fun share(customAction: SDCustomActionModel) {
        val map = currentScreen()?.sections?.filterIsInstance<StoreDetailSectionModel.Map>()?.firstOrNull()
        val params = customAction.extraParams
        val latitude = params.doubleValue("LATITUDE") ?: map?.location?.latitude ?: return
        val longitude = params.doubleValue("LONGITUDE") ?: map?.location?.longitude ?: return
        val name = params.stringValue("STORE_NAME")?.toServerDrivenPlainText() ?: currentStoreName()
        val type = params.stringValue("STORE_TYPE") ?: storeType ?: USER_STORE
        shareWithKakao(
            shareFormat = ShareFormat(getString(CommonR.string.kakao_map_format), name, LatLng(latitude, longitude)),
            title = if (type == BOSS_STORE) getString(CommonR.string.share_kakao_food_truck_title, name) else getString(CommonR.string.share_kakao_road_food_title, name),
            description = if (type == BOSS_STORE) getString(CommonR.string.share_kakao_food_truck, name) else getString(CommonR.string.share_kakao_road_food, name),
            imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
            storeId = storeId.toString(),
            type = if (type == BOSS_STORE) getString(CommonR.string.scheme_host_kakao_link_food_truck_type) else getString(CommonR.string.scheme_host_kakao_link_road_food_type),
        )
    }

    private fun showReviewReportDialog(event: StoreDetailV2Event.ShowReviewReportDialog) {
        var selectedIndex = 0
        val input = EditText(this).apply { hint = getString(CommonR.string.review_report_reason_detail_hint) }
        AlertDialog.Builder(this)
            .setTitle(getString(CommonR.string.review_report_dialog_title))
            .setSingleChoiceItems(event.reasons.map { it.description }.toTypedArray(), selectedIndex) { _, which -> selectedIndex = which }
            .setView(input)
            .setPositiveButton(CommonR.string.report_confirm) { _, _ ->
                val reason = event.reasons[selectedIndex]
                viewModel.submitReviewReport(event.customAction, reason.type, input.text?.toString()?.takeIf { reason.hasReasonDetail && it.isNotBlank() })
            }
            .setNegativeButton(CommonR.string.cancel, null)
            .show()
    }

    private fun currentScreen(): StoreDetailScreenModel? =
        (viewModel.uiState.value as? StoreDetailV2UiState.Content)?.screen

    private fun currentStoreName(): String = currentScreen()
        ?.sections
        ?.filterIsInstance<StoreDetailSectionModel.Preview>()
        ?.firstOrNull()
        ?.header
        ?.title
        .displayText()

    private fun finishWithResult() {
        val favorite = currentScreen()
            ?.sections
            ?.filterIsInstance<StoreDetailSectionModel.Preview>()
            ?.firstOrNull()
            ?.additionalInfos
            ?.isSubscriber
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(StoreDetailActivity.EXTRA_IS_UPDATED, viewModel.hasUpdates.value)
                favorite?.let { putExtra(StoreDetailActivity.EXTRA_IS_FAVORITE, it) }
            },
        )
        finish()
    }

    override fun finish() {
        navigateToMainActivityOnCloseIfNeeded()
        super.finish()
    }

    companion object {
        private const val EXTRA_STORE_ID = "store_detail_v2_store_id"
        private const val EXTRA_STORE_TYPE = "store_detail_v2_store_type"
        private const val EXTRA_START_CERTIFICATION = "store_detail_v2_start_certification"
        private const val EXTRA_OPEN_REVIEW_WRITE = "store_detail_v2_open_review_write"

        fun getIntent(
            context: Context,
            storeId: Long? = null,
            storeType: String? = null,
            startCertification: Boolean = false,
            openReviewWrite: Boolean = false,
            deepLinkStoreId: String? = null,
        ): Intent = Intent(context, StoreDetailV2Activity::class.java).apply {
            (storeId ?: deepLinkStoreId?.toLongOrNull())?.let { putExtra(EXTRA_STORE_ID, it) }
            storeType?.let { putExtra(EXTRA_STORE_TYPE, it) }
            putExtra(EXTRA_START_CERTIFICATION, startCertification)
            putExtra(EXTRA_OPEN_REVIEW_WRITE, openReviewWrite)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StoreDetailV2Screen(
    viewModel: StoreDetailV2ViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(DesignSystemR.drawable.ic_arrow_left),
                            contentDescription = "뒤로",
                        )
                    }
                },
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        when (val value = state) {
            is StoreDetailV2UiState.Loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            is StoreDetailV2UiState.Content -> StoreDetailV2Content(
                screen = value.screen,
                onAction = viewModel::onAction,
                onFavoriteToggle = viewModel::toggleFavorite,
                onImpression = viewModel::sendImpression,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            )
            is StoreDetailV2UiState.Error -> Box(Modifier.fillMaxSize().padding(innerPadding))
        }
    }
}

private fun Map<String, SDClickLogValue>.stringValue(key: String): String? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value
    is SDClickLogValue.IntValue -> value.value.toString()
    is SDClickLogValue.LongValue -> value.value.toString()
    is SDClickLogValue.DoubleValue -> value.value.toString()
    is SDClickLogValue.BoolValue -> value.value.toString()
    SDClickLogValue.Null, null -> null
}

private fun Map<String, SDClickLogValue>.longValue(key: String): Long? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value.toLongOrNull()
    is SDClickLogValue.IntValue -> value.value.toLong()
    is SDClickLogValue.LongValue -> value.value
    is SDClickLogValue.DoubleValue -> value.value.toLong()
    is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
}

private fun Map<String, SDClickLogValue>.doubleValue(key: String): Double? = when (val value = this[key]) {
    is SDClickLogValue.StringValue -> value.value.toDoubleOrNull()
    is SDClickLogValue.IntValue -> value.value.toDouble()
    is SDClickLogValue.LongValue -> value.value.toDouble()
    is SDClickLogValue.DoubleValue -> value.value
    is SDClickLogValue.BoolValue, SDClickLogValue.Null, null -> null
}
