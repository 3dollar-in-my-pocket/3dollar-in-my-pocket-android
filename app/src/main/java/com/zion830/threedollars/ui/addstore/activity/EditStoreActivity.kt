package com.zion830.threedollars.ui.addstore.activity

import androidx.activity.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityNewStoreBinding
import com.zion830.threedollars.ui.addstore.AddStoreViewModel
import com.zion830.threedollars.ui.addstore.NewAddressFragment
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnBackPressedListener

class EditStoreActivity : BaseActivity<ActivityNewStoreBinding, AddStoreViewModel>(R.layout.activity_new_store), OnBackPressedListener {

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