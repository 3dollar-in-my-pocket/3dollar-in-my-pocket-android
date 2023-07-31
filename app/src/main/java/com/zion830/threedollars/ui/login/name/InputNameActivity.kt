package com.zion830.threedollars.ui.login.name

import android.os.Handler
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.messaging.FirebaseMessaging
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginNameBinding
import com.zion830.threedollars.ui.MarketingDialog
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class InputNameActivity :
    BaseActivity<ActivityLoginNameBinding, InputNameViewModel>(R.layout.activity_login_name) {

    override val viewModel: InputNameViewModel by viewModels()

    override fun initView() {
        binding.nameEditTextView.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            val handler = Handler()
            val runnable: Runnable = object : Runnable {
                override fun run() {
                    handler.postDelayed(this, 10)
                }
            }
            handler.postDelayed(runnable, 10)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.finishButton.onSingleClick {
            showMarketingDialog()
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                setResult(RESULT_OK)
                finish()
            }
        }
        viewModel.isAlreadyUsed.observe(this) {
            binding.alreadyExistTextView.isVisible = it > 0
            binding.waringImageView.isVisible = it > 0
            binding.nameEditTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
            if (it != -1) {
                binding.alreadyExistTextView.text = getString(it)
            }
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.trySignUp()
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