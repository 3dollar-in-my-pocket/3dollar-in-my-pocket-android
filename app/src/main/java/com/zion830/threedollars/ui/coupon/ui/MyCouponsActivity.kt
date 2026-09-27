package com.zion830.threedollars.ui.coupon.ui

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import base.compose.AppTheme
import com.threedollar.common.listener.ActivityStarter
import com.zion830.threedollars.core.ui.component.compose.components.FlowWithLifecycleEffect
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiEffect
import com.zion830.threedollars.ui.coupon.model.MyCouponsUiIntent
import com.zion830.threedollars.ui.coupon.viewModel.MyCouponsViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.threedollar.common.R as CommonR

/**
 * 내 쿠폰함. 사용 가능/지난 쿠폰 두 탭과 쿠폰 사용 바텀시트 (iOS `CouponTabViewController`).
 * 마이페이지 쿠폰 섹션과 `myCoupons` 딥링크로 들어온다.
 */
@AndroidEntryPoint
class MyCouponsActivity : AppCompatActivity() {

    @Inject
    lateinit var activityStarter: ActivityStarter

    private val viewModel: MyCouponsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            AppTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                LaunchedEffect(Unit) { viewModel.dispatch(MyCouponsUiIntent.OnInit) }
                FlowWithLifecycleEffect(viewModel.effect) { handleEffect(it) }
                MyCouponsScreen(state = state, onIntent = viewModel::dispatch)
            }
        }
    }

    private fun handleEffect(effect: MyCouponsUiEffect) {
        when (effect) {
            MyCouponsUiEffect.Close -> finish()
            is MyCouponsUiEffect.OpenStore -> activityStarter.startBossDetailActivity(this, effect.storeId)
            is MyCouponsUiEffect.ShowToast -> showToast(effect.messageRes)
            is MyCouponsUiEffect.ShowErrorAlert -> showErrorAlert(effect.message)
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
        fun getIntent(context: Context): Intent = Intent(context, MyCouponsActivity::class.java)
    }
}
