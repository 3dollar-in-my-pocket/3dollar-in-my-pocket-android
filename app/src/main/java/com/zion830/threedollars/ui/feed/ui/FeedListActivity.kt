package com.zion830.threedollars.ui.feed.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.AppTheme
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.sdui.model.element.SDLink
import com.threedollar.common.sdui.model.element.SDLinkType
import com.threedollar.domain.community.data.AdvertisementModelV2
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.ui.feed.model.FeedListUiEffect
import com.zion830.threedollars.ui.feed.model.FeedListUiIntent
import com.zion830.threedollars.ui.feed.model.FeedLocation
import com.zion830.threedollars.ui.feed.viewModel.FeedListViewModel
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import dagger.hilt.android.AndroidEntryPoint
import com.threedollar.common.R as CommonR

/**
 * 우리 동네 소식(지역 피드). 커뮤니티 탭 플로팅 버튼에서 전체 화면 모달로 연다 (iOS `FeedListViewController`).
 */
@AndroidEntryPoint
class FeedListActivity : AppCompatActivity() {

    private val viewModel: FeedListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
        )
        setContent {
            AppTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { viewModel.dispatch(FeedListUiIntent.OnInit) }
                FlowWithLifecycleEffect(viewModel.effect) { handleEffect(it) }
                FeedListScreen(state = state, onIntent = viewModel::dispatch)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LogManager.sendPageView(ScreenName.FEED_LIST, this::class.java.simpleName)
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, CommonR.anim.slide_out_down)
    }

    private fun handleEffect(effect: FeedListUiEffect) {
        when (effect) {
            FeedListUiEffect.Close -> finish()
            is FeedListUiEffect.OpenLink -> openLink(effect.link)
            is FeedListUiEffect.OpenAdvertisement -> openAdvertisement(effect.advertisement)
            is FeedListUiEffect.ShowErrorAlert -> showErrorAlert(effect.message)
        }
    }

    private fun openLink(link: SDLink) {
        val url = link.link?.takeIf { it.isNotBlank() } ?: return
        if (link.type == SDLinkType.WEB) {
            startActivitySafely(Intent(Intent.ACTION_VIEW, url.toUri()))
        } else {
            DynamicLinkActivity.launch(this, url)
        }
    }

    private fun openAdvertisement(advertisement: AdvertisementModelV2) {
        val url = advertisement.link.url.takeIf { it.isNotBlank() } ?: return
        if (advertisement.link.type == SDLinkType.APP_SCHEME.name) {
            DynamicLinkActivity.launch(this, url)
        } else {
            startActivitySafely(Intent(Intent.ACTION_VIEW, url.toUri()))
        }
    }

    private fun startActivitySafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            showErrorAlert(null)
        }
    }

    private fun showErrorAlert(message: String?) {
        if (isFinishing) return
        AlertDialog.Builder(this)
            .setMessage(message ?: getString(CommonR.string.store_detail_error_default))
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    companion object {
        private const val EXTRA_MAP_LATITUDE = "extra_map_latitude"
        private const val EXTRA_MAP_LONGITUDE = "extra_map_longitude"
        private const val EXTRA_DEVICE_LATITUDE = "extra_device_latitude"
        private const val EXTRA_DEVICE_LONGITUDE = "extra_device_longitude"

        fun getIntent(context: Context, location: FeedLocation): Intent =
            Intent(context, FeedListActivity::class.java).apply {
                location.mapLatitude?.let { putExtra(EXTRA_MAP_LATITUDE, it) }
                location.mapLongitude?.let { putExtra(EXTRA_MAP_LONGITUDE, it) }
                location.deviceLatitude?.let { putExtra(EXTRA_DEVICE_LATITUDE, it) }
                location.deviceLongitude?.let { putExtra(EXTRA_DEVICE_LONGITUDE, it) }
            }

        fun readLocation(savedStateHandle: SavedStateHandle) = FeedLocation(
            mapLatitude = savedStateHandle.get<Double>(EXTRA_MAP_LATITUDE),
            mapLongitude = savedStateHandle.get<Double>(EXTRA_MAP_LONGITUDE),
            deviceLatitude = savedStateHandle.get<Double>(EXTRA_DEVICE_LATITUDE),
            deviceLongitude = savedStateHandle.get<Double>(EXTRA_DEVICE_LONGITUDE),
        )
    }
}
