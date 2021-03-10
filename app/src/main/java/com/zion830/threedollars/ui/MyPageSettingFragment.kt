package com.zion830.threedollars.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMypageSettingBinding
import com.zion830.threedollars.splash.SplashActivity
import com.zion830.threedollars.ui.mypage.AskFragment
import com.zion830.threedollars.ui.mypage.EditNameFragment
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment


class MyPageSettingFragment : BaseFragment<FragmentMypageSettingBinding, UserInfoViewModel>(R.layout.fragment_mypage_setting) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun initView() {
        binding.btnEditName.setOnClickListener {
            addEditNameFragment()
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.layoutTerms.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_service_url)))
            startActivity(browserIntent)
        }
        binding.layoutAsk.setOnClickListener {
            addAskFragment()
        }
        binding.buttonLogout.setOnClickListener {
            tryLogout()
        }
        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_account_confirm)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.ok) { _, _ -> tryDeleteAccount() }
            .create()
            .show()
    }

    private fun tryDeleteAccount() {
        viewModel.deleteUser {
            showToast(R.string.delete_account_success)
            SharedPrefUtils.clearUserInfo()
            requireActivity().finish()
        }
    }

    private fun tryLogout() {
        SharedPrefUtils.clearUserInfo()
        showToast(R.string.logout_message)
        requireActivity().finish()
        startActivity(Intent(requireContext(), SplashActivity::class.java))
    }

    private fun addEditNameFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.setting_container,
            EditNameFragment(),
            EditNameFragment::class.java.name
        )
    }

    private fun addAskFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.setting_container,
            AskFragment(),
            AskFragment::class.java.name
        )
    }
}