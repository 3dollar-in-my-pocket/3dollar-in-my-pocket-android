package com.zion830.threedollars.ui

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.firebase.messaging.FirebaseMessaging
import com.zion830.threedollars.*
import com.zion830.threedollars.GlobalApplication.Companion.eventTracker
import com.zion830.threedollars.databinding.FragmentMypageSettingBinding
import com.zion830.threedollars.datasource.model.v2.request.PushInformationRequest
import com.zion830.threedollars.ui.mypage.AskFragment
import com.zion830.threedollars.ui.mypage.EditNameFragment
import com.zion830.threedollars.ui.splash.SplashActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import com.threedollar.common.ext.addNewFragment

@AndroidEntryPoint
class MyPageSettingFragment :
    LegacyBaseFragment<FragmentMypageSettingBinding, UserInfoViewModel>(R.layout.fragment_mypage_setting) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun initView() {
        EventTracker.logEvent(Constants.SETTING_BTN_CLICKED)

        binding.btnEditName.setOnClickListener {
            addEditNameFragment()
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.layoutTerms.setOnClickListener {
            EventTracker.logEvent(Constants.TERMS_OF_USE_BTN_CLICKED)
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_of_service_url)))
            startActivity(browserIntent)
        }
        binding.layoutPrivacyPolicy.setOnClickListener {
            EventTracker.logEvent(Constants.PRIVACY_POLICY_OF_USE_BTN_CLICKED)
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }
        binding.layoutAsk.setOnClickListener {
            EventTracker.logEvent(Constants.INQUIRY_BTN_CLICKED)
            addAskFragment()
        }
        binding.buttonLogout.setOnClickListener {
            EventTracker.logEvent(Constants.LOGOUT_BTN_CLICKED)
            tryLogout()
        }
        binding.btnDeleteAccount.setOnClickListener {
            EventTracker.logEvent(Constants.SIGNOUT_BTN_CLICKED)
            showDeleteAccountDialog()
        }
        binding.pushSwitchButton.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                eventTracker.setUserProperty("isPushEnable", "true")
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.postPushInformation(
                            informationRequest = PushInformationRequest(pushToken = it.result)
                        )
                    } else {
                        // TODO: 실패했을때 예외처리 필요
                    }

                }
            } else {
                eventTracker.setUserProperty("isPushEnable", "false")
                viewModel.deletePushInformation()
            }
        }

        binding.token.isVisible = BuildConfig.BUILD_TYPE == "debug"
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = "테스트용 토큰입니다 - 길게 클릭하면 복사됩니다!\n${task.result}"
                binding.token.text = token
                binding.token.setOnLongClickListener {
                    val manager =
                        (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                    manager.text = task.result
                    showToast("토큰이 복사되었습니다.")
                    false
                }
            }
        }

        viewModel.logoutResult.observe(viewLifecycleOwner) {
            if (it) {
                LegacySharedPrefUtils.clearUserInfo()
                showToast(R.string.logout_message)
                startActivity(Intent(requireContext(), SplashActivity::class.java))
                requireActivity().finish()
            } else {
                showToast(R.string.connection_failed)
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner) {
            binding.pushSwitchButton.isChecked = it.data.device?.isSetupNotification == true
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_account_confirm)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                EventTracker.logEvent(Constants.SIGNOUT_CANCEL_BTN_CLICKED)
            }
            .setPositiveButton(R.string.ok) { _, _ -> tryDeleteAccount() }
            .create()
            .show()
    }

    private fun tryDeleteAccount() {
        EventTracker.logEvent(Constants.SIGNOUT_WITHDRAW_BTN_CLICKED)
        viewModel.deleteUser {
            showToast(R.string.delete_account_success)
            LegacySharedPrefUtils.clearUserInfo()
            GlobalApplication.googleClient.signOut()
            requireActivity().finish()
        }
    }

    private fun tryLogout() {
        viewModel.logout()
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