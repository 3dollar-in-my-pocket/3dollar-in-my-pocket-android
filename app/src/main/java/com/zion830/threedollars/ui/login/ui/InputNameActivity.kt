package com.zion830.threedollars.ui.login.ui

import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginNameBinding
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.login.viewModel.InputNameViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zion830.com.common.base.LegacyBaseActivity
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class InputNameActivity :
    LegacyBaseActivity<ActivityLoginNameBinding, InputNameViewModel>(R.layout.activity_login_name) {

    override val viewModel: InputNameViewModel by viewModels()

    override fun initView() {
        binding.etName.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            val handler = Handler()
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    binding.scrollView.smoothScrollTo(0, binding.btnFinish.bottom)
                    handler.postDelayed(this, 10)
                }
            }
            handler.postDelayed(runnable, 10)
        }
        initButton()
        initFlow()
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                showMarketingDialog()
            }
        }
        viewModel.isAlreadyUsed.observe(this) {
            binding.tvAlreadyExist.isVisible = it > 0
            if (it != -1) {
                binding.tvAlreadyExist.text = getString(it)
            }
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.isMarketing.collect {
                        if (it) {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFinish.onSingleClick {
            viewModel.trySignUp()
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.postPushInformation(pushToken = it.result, isMarketing = isMarketing)
                    }
                }
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    companion object {
        fun getInstance() = InputNameActivity()
    }
}