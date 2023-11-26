package com.zion830.threedollars.ui.mypage.ui

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment

@AndroidEntryPoint
class EditNameFragment : LegacyBaseFragment<FragmentEditNameBinding, UserInfoViewModel>(R.layout.fragment_edit_name), OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun initView() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_PAGE_BTN_CLICKED)

        binding.etName.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    binding.scrollView.smoothScrollTo(0, binding.btnFinish.bottom)
                    handler.postDelayed(this, 10)
                }
            }
            handler.postDelayed(runnable, 10)
        }
        binding.btnBack.setOnClickListener {
            viewModel.clearName()
            activity?.supportFragmentManager?.popBackStack()
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                hideKeyboard(binding.scrollView)
                viewModel.initNameUpdateInfo()
                viewModel.clearName()
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        viewModel.isAlreadyUsed.observe(this) {
            EventTracker.logEvent(Constants.NICKNAME_ALREADY_EXISTED)
            binding.tvAlreadyExist.isVisible = it > 0
            if (it != -1) {
                binding.tvAlreadyExist.text = getString(it)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.clearName()
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}