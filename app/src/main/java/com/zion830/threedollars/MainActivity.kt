package com.zion830.threedollars

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zion830.threedollars.databinding.ActivityHomeBinding
import com.zion830.threedollars.ui.addstore.AddStoreActivity
import com.zion830.threedollars.utils.requestPermissionFirst
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.showSnack

class MainActivity : BaseActivity<ActivityHomeBinding, UserInfoViewModel>(R.layout.activity_home) {

    override val viewModel: UserInfoViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment

    override fun initView() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.itemIconTintList = null
        binding.navView.setupWithNavController(navController)

        binding.ibEdit.setOnClickListener {
            startActivityForResult(Intent(this, AddStoreActivity::class.java), ADD_STORE)
        }

        requestPermissionFirst()

        viewModel.msgTextId.observe(this) {
            binding.container.showSnack(it, color = R.color.color_main_red)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        navHostFragment.childFragmentManager.fragments.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val ADD_STORE: Int = 10

        fun getIntent(context: Context): Intent = Intent(context, MainActivity::class.java)
    }
}
