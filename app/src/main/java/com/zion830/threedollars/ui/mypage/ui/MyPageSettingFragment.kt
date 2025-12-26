package com.zion830.threedollars.ui.mypage.ui

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.my.presentation.page.team.MyPageTeamActivity
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.utils.Constants
import com.threedollar.network.request.PatchPushInformationRequest
import com.zion830.threedollars.BuildConfig
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.GlobalApplication.Companion.eventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMypageSettingBinding
import com.zion830.threedollars.datasource.model.LoginType
import com.zion830.threedollars.ui.splash.ui.SplashActivity
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.getMarketingDate
import com.zion830.threedollars.utils.showToast
import com.zion830.threedollars.utils.subscribeToTopicFirebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class MyPageSettingFragment :
    BaseFragment<FragmentMypageSettingBinding, UserInfoViewModel>(), OnBackPressedListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMypageSettingBinding =
        FragmentMypageSettingBinding.inflate(inflater, container, false)

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.SETTING, this::class.simpleName.toString())
    }

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun initView() {
        EventTracker.logEvent(Constants.SETTING_BTN_CLICKED)

        initButton()

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

        initObserve()

        viewModel.updateUserInfo()
    }

    private fun initObserve() {
        viewModel.logoutResult.observe(viewLifecycleOwner) {
            if (it) {
                LegacySharedPrefUtils.clearUserInfo()
                startActivity(Intent(requireContext(), SplashActivity::class.java))
                requireActivity().finish()
            } else {
                showToast(CommonR.string.connection_failed)
            }
        }

        viewModel.userInfo.observe(viewLifecycleOwner) {
            binding.pushSwitchButton.isSelected = it.settings.enableActivitiesPush == true
            binding.pushMarketingSwitchButton.isSelected = it.settings.marketingConsent == "APPROVE"
            checkBoxListener()
            binding.tvName.text = it.name
            binding.twLoginType.setCompoundDrawablesRelativeWithIntrinsicBounds(
                resources.getDrawable(if (it.socialType == LoginType.KAKAO.socialName) DesignSystemR.drawable.ic_logo_kakao else DesignSystemR.drawable.ic_logo_google),
                null,
                null,
                null
            )
            binding.twLoginType.text =
                if (it.socialType == LoginType.KAKAO.socialName) getString(CommonR.string.kakao_user) else getString(CommonR.string.google_user)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        lifecycleScope.launch {
            viewModel.isNameUpdated.collect {
                viewModel.updateUserInfo()
            }
        }
    }

    private fun initButton() {
        binding.tvName.setOnClickListener {
            addEditNameFragment()
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.layoutTerms.setOnClickListener {
            EventTracker.logEvent(Constants.TERMS_OF_USE_BTN_CLICKED)
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(CommonR.string.terms_of_service_url)))
            startActivity(browserIntent)
        }
        binding.layoutTeamIntroduce.setOnClickListener {
            EventTracker.logEvent(Constants.TEAM_INTRODUCE_BTN_CLICKED)
            val browserIntent = Intent(requireContext(), MyPageTeamActivity::class.java)
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
        binding.constraintAd.setOnClickListener {
            val webpage: Uri = Uri.parse("https://massive-iguana-121.notion.site/3-ff344e306d0c4417973daee8792cfe4d") // 여기에 원하는 URL 입력
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
        binding.constraintBoss.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=app.threedollars.manager"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=app.threedollars.manager"))
                startActivity(intent)
            }
        }
    }

    private fun checkBoxListener() {
        binding.layoutPush.setOnClickListener {
            binding.pushSwitchButton.isSelected = !binding.pushSwitchButton.isSelected
            viewModel.patchPushInformation(
                PatchPushInformationRequest(
                    binding.pushSwitchButton.isSelected,
                    if (binding.pushMarketingSwitchButton.isSelected) "APPROVE" else "DENY"
                )
            )
        }
        binding.layoutPushMarketing.setOnClickListener {
            if (!binding.pushMarketingSwitchButton.isSelected) {
                binding.pushMarketingSwitchButton.isSelected = !binding.pushMarketingSwitchButton.isSelected
                subscribeToTopicFirebase(true)
                viewModel.patchPushInformation(PatchPushInformationRequest(binding.pushSwitchButton.isSelected, "APPROVE"))
                eventTracker.setUserProperty("isPushEnable", "true")
                showToast(getString(CommonR.string.marketing_approve_toast, getMarketingDate()))
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(CommonR.string.marketing_deny_title))
                    .setMessage(getString(CommonR.string.marketing_deny_msg))
                    .setCancelable(false)
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(zion830.com.common.R.string.ok) { _, _ ->
                        binding.pushMarketingSwitchButton.isSelected = !binding.pushMarketingSwitchButton.isSelected
                        subscribeToTopicFirebase(false)
                        viewModel.patchPushInformation(PatchPushInformationRequest(binding.pushSwitchButton.isSelected, "DENY"))
                        eventTracker.setUserProperty("isPushEnable", "false")
                        showToast(getString(CommonR.string.marketing_deny_toast, getMarketingDate()))
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(CommonR.string.delete_account_confirm)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                EventTracker.logEvent(Constants.SIGNOUT_CANCEL_BTN_CLICKED)
            }
            .setPositiveButton(zion830.com.common.R.string.ok) { _, _ -> tryDeleteAccount() }
            .create()
            .show()
    }

    private fun tryDeleteAccount() {
        EventTracker.logEvent(Constants.SIGNOUT_WITHDRAW_BTN_CLICKED)
        viewModel.deleteUser {
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
            EditNameFragment::class.java.name,
        )
    }

    private fun addAskFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.setting_container,
            AskFragment(),
            AskFragment::class.java.name,
        )
    }
}
