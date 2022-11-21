package com.zion830.threedollars.ui.login

import android.os.Handler
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.firebase.messaging.FirebaseMessaging
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentLoginNameBinding
import com.zion830.threedollars.datasource.model.v2.request.MarketingConsentRequest
import com.zion830.threedollars.datasource.model.v2.request.PushInformationRequest
import com.zion830.threedollars.ui.MarketingDialog
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class InputNameFragment :
    BaseFragment<FragmentLoginNameBinding, LoginViewModel>(R.layout.fragment_login_name) {

    override val viewModel: LoginViewModel by activityViewModels()

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
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.btnFinish.onSingleClick {
            showMarketingDialog()
        }

        viewModel.isAlreadyUsed.observe(this) {
            binding.tvAlreadyExist.isVisible = it > 0
            if (it != -1) {
                binding.tvAlreadyExist.text = getString(it)
            }
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.trySignUp(
                            informationRequest = PushInformationRequest(pushToken = it.result),
                            isMarketing = isMarketing
                        )
                    }
                }
            }
        })
        dialog.show(parentFragmentManager, dialog.tag)
    }

    companion object {

        fun getInstance() = InputNameFragment()
    }
}