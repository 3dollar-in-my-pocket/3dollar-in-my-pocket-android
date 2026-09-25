package com.zion830.threedollars.ui.storeDetail.user.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.domain.home.data.store.UserStoreModel
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreCertificationBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreCertificationViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.isLocationServiceEnabled
import com.zion830.threedollars.utils.isLocationAvailable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreCertificationActivity :
    BaseActivity<ActivityStoreCertificationBinding, StoreCertificationViewModel>({ ActivityStoreCertificationBinding.inflate(it) }) {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        setDarkSystemBars()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val args = IntentCompat.getSerializableExtra(intent, EXTRA_ARGS, StoreCertificationArgs::class.java)
        val storeId = intent.getIntExtra(EXTRA_STORE_ID, 0)
        when {
            args != null -> requestCurrentLocation { location -> showCertificationFragment(args.toUserStoreModel(), location) }
            storeId > 0 -> loadStoreAndOpen(storeId)
            else -> finish()
        }
    }

    private fun loadStoreAndOpen(storeId: Int) {
        requestCurrentLocation { location ->
            viewModel.loadCertificationStore(storeId, location)
            lifecycleScope.launch {
                val store = viewModel.certificationStore.first()
                if (store == null) finish() else showCertificationFragment(store, location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(onResult: (LatLng?) -> Unit) {
        if (!isLocationAvailable() || !isLocationServiceEnabled()) {
            onResult(null)
            return
        }

        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location -> onResult(location?.toLatLng()) }
                .addOnFailureListener { onResult(null) }
        } catch (e: SecurityException) {
            onResult(null)
        }
    }

    private fun showCertificationFragment(
        userStoreModel: UserStoreModel,
        currentLocation: LatLng?,
    ) {
        val storeLocation = LatLng(userStoreModel.location.latitude, userStoreModel.location.longitude)
        val distance = NaverMapUtils.calculateDistance(currentLocation, storeLocation)
        val fragment = if (distance > StoreCertificationAvailableFragment.MIN_DISTANCE) {
            StoreCertificationFragment.getInstance(
                userStoreModel = userStoreModel,
                finishActivityOnClose = true,
            )
        } else {
            StoreCertificationAvailableFragment.getInstance(
                userStoreModel = userStoreModel,
                finishActivityOnClose = true,
            )
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment::class.java.name)
            .commit()
    }

    private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)

    companion object {
        private const val EXTRA_ARGS = "extra_store_certification_args"
        private const val EXTRA_STORE_ID = "extra_store_certification_store_id"

        fun getIntent(context: Context, args: StoreCertificationArgs): Intent {
            return Intent(context, StoreCertificationActivity::class.java).apply {
                putExtra(EXTRA_ARGS, args)
            }
        }

        /** 가게 정보가 없는 진입점(딥링크·마이페이지 등)용. 가게명·위치는 화면이 직접 조회한다. */
        fun getIntent(context: Context, storeId: Int): Intent {
            return Intent(context, StoreCertificationActivity::class.java).apply {
                putExtra(EXTRA_STORE_ID, storeId)
            }
        }
    }
}
