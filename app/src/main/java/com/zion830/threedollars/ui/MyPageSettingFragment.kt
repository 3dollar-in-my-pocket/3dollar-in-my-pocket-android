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
        binding.btnTest.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setPositiveButton(R.string.ok) { _, _ ->
                    SharedPrefUtils.changeServerStatus()
                    SharedPrefUtils.clearUserInfo()
                    initTestBtn()
                    requireActivity().finish()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                }
                .setTitle("진짜 바꾸시겠습니까?")
                .setMessage("확인을 누르면 앱이 종료되고 다시 로그인 해야돼요.")
                .create()
                .show()
        }
        initTestBtn()
    }

    private fun initTestBtn() {
        if (SharedPrefUtils.getServerStatus()) {
            binding.btnTest.text = "[DEBUG] 서버 변경하기 (현재 개발 서버)"
        } else {
            binding.btnTest.text = "[DEBUG] 서버 변경하기 (현재 운영 서버)"
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