package com.zion830.threedollars.ui.mypage

import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentEditNameBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.onSingleClick
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

        binding.finishButton.onSingleClick {
            viewModel.updateName()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.eventsFlow.collect {
                        when (it) {
                            UserInfoViewModel.Event.NameAlready -> showNameErrorTextView(R.string.login_name_already_exist)
                            UserInfoViewModel.Event.NameError -> showNameErrorTextView(R.string.invalidate_name)
                            UserInfoViewModel.Event.NameUpdate -> {
                                viewModel.clearName()
                                activity?.supportFragmentManager?.popBackStack()
                            }
                        }
                    }
                }
            }
        }

    }

    private fun showNameErrorTextView(@StringRes stringRes: Int) {
        EventTracker.logEvent(Constants.NICKNAME_ALREADY_EXISTED)
        binding.alreadyExistTextView.isVisible = true
        binding.waringImageView.isVisible = true
        binding.nameEditTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        binding.alreadyExistTextView.text = getString(stringRes)
    }

    override fun onBackPressed() {
        viewModel.clearName()
    }

}