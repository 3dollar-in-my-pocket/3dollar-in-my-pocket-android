package com.zion830.threedollars.ui.mypage

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import zion830.com.common.base.BaseFragment


class EditNameFragment : BaseFragment<FragmentEditNameBinding, UserInfoViewModel>(R.layout.fragment_edit_name) {

    override val viewModel: UserInfoViewModel by activityViewModels()

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
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                hideKeyboard(binding.scrollView)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}