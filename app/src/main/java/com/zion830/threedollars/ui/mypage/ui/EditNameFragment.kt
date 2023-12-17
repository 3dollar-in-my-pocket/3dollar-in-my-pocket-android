package com.zion830.threedollars.ui.mypage.ui

import android.app.Activity
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class EditNameFragment : BaseFragment<FragmentEditNameBinding, UserInfoViewModel>(), OnBackPressedListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEditNameBinding =
        FragmentEditNameBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "EditNameFragment", screenName = null)
    }

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun initView() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_PAGE_BTN_CLICKED)
        initEditTextView()
        initButton()
        initObserve()
    }

    private fun initObserve() {
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
        viewModel.userInfo.observe(viewLifecycleOwner) {
            binding.tvName.text = it.data.name
        }
        viewModel.isNameEmpty.observe(viewLifecycleOwner) {
            binding.btnFinish.isClickable = !it
            binding.btnFinish.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (it) R.drawable.ic_start_off else R.drawable.ic_start,
                0,
            )
            binding.btnFinish.setTextColor(
                if (it) {
                    resources.getColor(R.color.color_gray2, null)
                } else {
                    resources.getColor(
                        R.color.color_main_red,
                        null,
                    )
                },
            )
        }
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            viewModel.clearName()
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.btnFinish.onSingleClick {
            viewModel.updateName()
        }
        binding.tvAlreadyExist.setOnClickListener {
            viewModel.updateName()
        }
    }

    private fun initEditTextView() {
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
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.userName.value = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onBackPressed() {
        viewModel.clearName()
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
