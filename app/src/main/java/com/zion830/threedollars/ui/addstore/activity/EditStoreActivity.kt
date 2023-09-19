package com.zion830.threedollars.ui.addstore.activity

import androidx.activity.viewModels
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityNewStoreBinding
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.addstore.NewAddressFragment
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseActivity

@AndroidEntryPoint
class EditStoreActivity : LegacyBaseActivity<ActivityNewStoreBinding, AddStoreViewModel>(R.layout.activity_new_store), OnBackPressedListener {

    override val viewModel: AddStoreViewModel by viewModels()

    override fun initView() {
        supportFragmentManager.addNewFragment(
            R.id.container,
            NewAddressFragment(),
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
}