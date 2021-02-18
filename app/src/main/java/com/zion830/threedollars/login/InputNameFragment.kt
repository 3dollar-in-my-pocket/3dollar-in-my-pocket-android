package com.zion830.threedollars.login

import android.os.Handler
import android.view.View
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentLoginNameBinding
import zion830.com.common.base.BaseFragment

class InputNameFragment : BaseFragment<FragmentLoginNameBinding, LoginViewModel>(R.layout.fragment_login_name) {

    override val viewModel: LoginViewModel by activityViewModels()

    override fun initView() {
        binding.etName.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            val handler: Handler = Handler()
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
        binding.btnFinish.setOnClickListener {
            viewModel.updateName()
        }
    }

    companion object {

        fun getInstance() = InputNameFragment()
    }
}