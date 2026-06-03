package com.zion830.threedollars.ui.storeDetail.user.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreCertificationBinding
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreCertificationViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreCertificationActivity :
    BaseActivity<ActivityStoreCertificationBinding, StoreCertificationViewModel>({ ActivityStoreCertificationBinding.inflate(it) }) {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        setDarkSystemBars()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val args = IntentCompat.getSerializableExtra(intent, EXTRA_ARGS, StoreCertificationArgs::class.java)
        if (args == null) {
            finish()
            return
        }
        openCertification(args)
    }

    @SuppressLint("MissingPermission")
    private fun openCertification(args: StoreCertificationArgs) {
        if (!isLocationAvailable() || !isGpsAvailable()) {
            showCertificationFragment(args = args, currentLocation = null)
            return
        }

        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    showCertificationFragment(args = args, currentLocation = location?.toLatLng())
                }
                .addOnFailureListener {
                    showCertificationFragment(args = args, currentLocation = null)
                }
        } catch (e: SecurityException) {
            showCertificationFragment(args = args, currentLocation = null)
        }
    }

    private fun showCertificationFragment(
        args: StoreCertificationArgs,
        currentLocation: LatLng?,
    ) {
        val userStoreModel = args.toUserStoreModel()
        val storeLocation = LatLng(args.latitude, args.longitude)
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

        fun getIntent(context: Context, args: StoreCertificationArgs): Intent {
            return Intent(context, StoreCertificationActivity::class.java).apply {
                putExtra(EXTRA_ARGS, args)
            }
        }
    }
}
