package com.zion830.threedollars.ui.storeDetail.sdui.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import base.compose.AppTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.edit.ui.EditStoreFragment
import com.zion830.threedollars.ui.storeDetail.sdui.model.StoreDetailSduiUiIntent
import com.zion830.threedollars.ui.storeDetail.sdui.viewModel.StoreDetailSduiViewModel
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 지도 밖 진입점(찜·마이페이지·방문/제보 목록·커뮤니티·추천 가게·딥링크)에서 여는 전체 화면 가게 상세.
 * 홈 시트의 full 상태와 같은 섹션 렌더링을 쓴다. 제보 가게·사장님 가게 구분 없이 이 화면 하나로 연다.
 */
@AndroidEntryPoint
class StoreDetailSduiActivity : AppCompatActivity() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel: StoreDetailSduiViewModel by viewModels()

    private val storeId: String by lazy(LazyThreadSafetyMode.NONE) { intent.getStringExtra(EXTRA_STORE_ID).orEmpty() }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.dispatch(StoreDetailSduiUiIntent.OnStoreChanged)
    }

    private val imagePickerLauncher: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        navigator.onImagesPicked(uris)
    }

    private val navigator by lazy(LazyThreadSafetyMode.NONE) {
        StoreDetailSduiNavigator(
            activity = this,
            fragmentContainerId = R.id.storeDetailSduiFragmentContainer,
            dispatch = viewModel::dispatch,
            launchForResult = resultLauncher::launch,
            launchImagePicker = {
                imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClose = ::finish,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.BLACK),
        )
        setContentView(R.layout.activity_store_detail_sdui)
        applyFragmentContainerInsets()
        supportFragmentManager.setFragmentResultListener(EditStoreFragment.STORE_EDITED_RESULT_KEY, this) { _, _ ->
            viewModel.dispatch(StoreDetailSduiUiIntent.OnStoreChanged)
        }

        findViewById<ComposeView>(R.id.storeDetailSduiComposeView).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    StoreDetailSduiRoute(
                        viewModel = viewModel,
                        navigator = navigator,
                        onBack = ::finish,
                        onClose = ::finish,
                        modifier = Modifier.statusBarsPadding(),
                    )
                }
            }
        }

        loadWithLocation(fragment = if (savedInstanceState == null) intent.getStringExtra(EXTRA_FRAGMENT) else null)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val fragment = intent.getStringExtra(EXTRA_FRAGMENT) ?: return
        if (intent.getStringExtra(EXTRA_STORE_ID) == storeId) {
            viewModel.dispatch(StoreDetailSduiUiIntent.ScrollToFragment(fragment))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.dispatch(StoreDetailSduiUiIntent.OnDisplayed)
    }

    override fun finish() {
        navigateToMainActivityOnCloseIfNeeded()
        super.finish()
    }

    @SuppressLint("MissingPermission")
    private fun loadWithLocation(fragment: String?) {
        if (!hasLocationPermission()) {
            viewModel.dispatch(StoreDetailSduiUiIntent.Load(storeId, latitude = null, longitude = null, fragment = fragment))
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                viewModel.dispatch(StoreDetailSduiUiIntent.Load(storeId, location?.latitude, location?.longitude, fragment))
            }
            .addOnFailureListener {
                viewModel.dispatch(StoreDetailSduiUiIntent.Load(storeId, latitude = null, longitude = null, fragment = fragment))
            }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun applyFragmentContainerInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.storeDetailSduiFragmentContainer)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }
    }

    companion object {
        private const val EXTRA_STORE_ID = "extra_store_id"
        private const val EXTRA_FRAGMENT = "extra_fragment"

        /**
         * @param fragment 열자마자 스크롤할 섹션(`review`, `INFO` 등). 없으면 맨 위에서 시작한다.
         */
        fun getIntent(context: Context, storeId: String, fragment: String? = null): Intent =
            Intent(context, StoreDetailSduiActivity::class.java).apply {
                putExtra(EXTRA_STORE_ID, storeId)
                fragment?.let { putExtra(EXTRA_FRAGMENT, it) }
            }
    }
}
