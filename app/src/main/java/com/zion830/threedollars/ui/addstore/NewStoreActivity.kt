package com.zion830.threedollars.ui.addstore

import androidx.activity.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityNewStoreBinding
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnBackPressedListener

class NewStoreActivity : BaseActivity<ActivityNewStoreBinding, AddStoreViewModel>(R.layout.activity_new_store), OnBackPressedListener {

    override val viewModel: AddStoreViewModel by viewModels()

    override fun initView() {
        supportFragmentManager.addNewFragment(
            R.id.container,
            EditAddressFragment(),
            EditAddressFragment::class.java.name,
            false
        )
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments.filter { it.isVisible }
        for (fragment in fragmentList) {
            if (fragment is EditAddressFragment) {
                finish()
            } else {
                super.onBackPressed()
            }
        }
    }
}