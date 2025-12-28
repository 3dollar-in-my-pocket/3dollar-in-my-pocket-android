package com.zion830.threedollars.ui.mypage.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class EditNameFragment : BaseFragment<FragmentEditNameBinding, UserInfoViewModel>(), OnBackPressedListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentEditNameBinding =
        FragmentEditNameBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "EditNameFragment", screenName = null)
    }

    override val viewModel: UserInfoViewModel by activityViewModels()
    val myPageViewModel: MyPageViewModel by activityViewModels()

    override fun initView() {
        EventTracker.logEvent(Constants.NICKNAME_CHANGE_PAGE_BTN_CLICKED)
        initEditTextView()
        initButton()
        initObserve()
        viewModel.updateUserInfo()
    }

    private fun initObserve() {
        lifecycleScope.launch {
            viewModel.isNameUpdated.collect {
                activity?.supportFragmentManager?.popBackStack()
                myPageViewModel.isNameUpdated()
            }
        }
        viewModel.isAlreadyUsed.observe(viewLifecycleOwner) {
            EventTracker.logEvent(Constants.NICKNAME_ALREADY_EXISTED)
            binding.groupAlreadyExist.isVisible = it.isNotEmpty()
            if (it.isNotEmpty()) {
                binding.editNickName.setTextColor(ContextCompat.getColor(requireContext(), DesignSystemR.color.red))
                binding.tvAlreadyExist.text = it
            }
        }
        viewModel.userInfo.observe(viewLifecycleOwner) {
            binding.editNickName.hint = it.name
        }
        viewModel.isNameEmpty.observe(viewLifecycleOwner) {
            binding.btnFinish.isSelected = !it
            binding.btnFinish.isClickable = !it
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
        binding.editNickName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateName()
            }
            return@setOnEditorActionListener false
        }
    }

    private fun initEditTextView() {
        binding.editNickName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.userName.value = charSequence.toString()
                binding.editNickName.setTextColor(ContextCompat.getColor(requireContext(), DesignSystemR.color.pink))
                if (binding.groupAlreadyExist.isVisible) {
                    binding.groupAlreadyExist.isVisible = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

    }

    override fun onBackPressed() {
        viewModel.clearName()
        activity?.supportFragmentManager?.popBackStack()
    }
}
