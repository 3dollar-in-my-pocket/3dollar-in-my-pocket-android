package com.zion830.threedollars.ui.login.ui

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.messaging.FirebaseMessaging
import com.threedollar.common.base.BaseActivity
import com.zion830.threedollars.Constants.CLICK_SIGN_UP
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginNameBinding
import com.zion830.threedollars.ui.dialog.MarketingDialog
import com.zion830.threedollars.ui.login.viewModel.InputNameViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class SignUpActivity :
    BaseActivity<ActivityLoginNameBinding, InputNameViewModel>({ ActivityLoginNameBinding.inflate(it) }) {

    override val viewModel: InputNameViewModel by viewModels()

    override fun initView() {
        initEditTextView()
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

        viewModel.isNameEmpty.observe(this) {
            binding.btnFinish.apply {
                isClickable = !it
                setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, if (it) R.drawable.ic_start_off else R.drawable.ic_start, 0)
                setTextColor(resources.getColor(if (it) R.color.color_gray2 else R.color.color_main_red, null))
            }
        }
        viewModel.isAvailable.observe(this) {
            binding.btnFinish.text = if (it) getString(R.string.login_name3) else getString(R.string.login_name_fail)
            binding.tvAlreadyExist.visibility = if (it) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SignUpActivity", screenName = "sign_in")
    }

    private fun initEditTextView() {
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
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.userName.value = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
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
            val bundle = Bundle().apply {
                putString("screen", "sign_up")
                putString("nickname", binding.etName.text.toString())
            }
            EventTracker.logEvent(CLICK_SIGN_UP, bundle)
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
        fun getInstance() = SignUpActivity()
    }
}