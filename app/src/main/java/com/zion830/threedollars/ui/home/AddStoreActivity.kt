package com.zion830.threedollars.ui.home

import androidx.activity.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityAddStoreBinding
import zion830.com.common.base.BaseActivity

class AddStoreActivity : BaseActivity<ActivityAddStoreBinding, AddStoreViewModel>(R.layout.activity_add_store) {

    override val viewModel: AddStoreViewModel by viewModels()

    override fun initView() {

    }
}