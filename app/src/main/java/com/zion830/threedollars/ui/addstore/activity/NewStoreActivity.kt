package com.zion830.threedollars.ui.addstore.activity

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityNewStoreBinding
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.addstore.NewAddressFragment
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnBackPressedListener

class NewStoreActivity : BaseActivity<ActivityNewStoreBinding, AddStoreViewModel>(R.layout.activity_new_store), OnBackPressedListener {

    override val viewModel: AddStoreViewModel by viewModels()

    override fun initView() {
        val latitude = intent.getDoubleExtra(KEY_LATITUDE, -1.0)
        val longitude = intent.getDoubleExtra(KEY_LONGITUDE, -1.0)

        supportFragmentManager.addNewFragment(
            R.id.container,
            NewAddressFragment.getInstance(if (latitude > 0) LatLng(latitude, longitude) else null),
            NewAddressFragment::class.java.name,
            false
        )
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments.filter { it.isVisible }
        for (fragment in fragmentList) {
            if (fragment is NewAddressFragment) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        const val KEY_LATITUDE = "KEY_LATITUDE"
        const val KEY_LONGITUDE = "KEY_LONGITUDE"

        fun getInstance(context: Context, latLng: LatLng?) = Intent(context, NewStoreActivity::class.java).apply {
            putExtra(KEY_LATITUDE, latLng?.latitude)
            putExtra(KEY_LONGITUDE, latLng?.longitude)
        }
    }
}