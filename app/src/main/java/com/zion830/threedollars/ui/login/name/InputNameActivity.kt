package com.zion830.threedollars.ui.login.name

import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityLoginNameBinding
import com.zion830.threedollars.ui.MarketingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class InputNameActivity :
    BaseActivity<ActivityLoginNameBinding, InputNameViewModel>(R.layout.activity_login_name) {

    override val viewModel: InputNameViewModel by viewModels()

    override fun initView() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.finishButton.onSingleClick {
            showMarketingDialog()
        }
        collectFlows()
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.eventsFlow.collect {
                        when (it) {
                            InputNameViewModel.Event.NameUpdate -> {
                                setResult(RESULT_OK)
                                finish()
                            }

                            InputNameViewModel.Event.NameAlready -> {
                                showNameErrorTextView(R.string.login_name_already_exist)
                            }

                            InputNameViewModel.Event.NameError -> {
                                showNameErrorTextView(R.string.invalidate_name)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showNameErrorTextView(@StringRes stringRes: Int) {
        binding.alreadyExistTextView.isVisible = true
        binding.waringImageView.isVisible = true
        binding.nameEditTextView.setTextColor(ContextCompat.getColor(this@InputNameActivity, R.color.red))
        binding.alreadyExistTextView.text = getString(stringRes)
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