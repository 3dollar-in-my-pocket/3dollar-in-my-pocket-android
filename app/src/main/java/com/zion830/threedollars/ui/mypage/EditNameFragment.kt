package com.zion830.threedollars.ui.mypage

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnBackPressedListener

@AndroidEntryPoint
class EditNameFragment : BaseFragment<FragmentEditNameBinding, UserInfoViewModel>(R.layout.fragment_edit_name), OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun initView() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_PAGE_BTN_CLICKED)

        binding.backImageView.setOnClickListener {
            viewModel.clearName()
            activity?.supportFragmentManager?.popBackStack()
        }
        viewModel.isNameUpdated.observe(this) {
            if (it) {
                viewModel.initNameUpdateInfo()
                viewModel.clearName()
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        viewModel.isAlreadyUsed.observe(this) {
            EventTracker.logEvent(Constants.NICKNAME_ALREADY_EXISTED)
            binding.alreadyExistTextView.isVisible = it > 0
            if (it != -1) {
                binding.alreadyExistTextView.text = getString(it)
            }
        }
    }

    override fun onBackPressed() {
        viewModel.clearName()
    }

}