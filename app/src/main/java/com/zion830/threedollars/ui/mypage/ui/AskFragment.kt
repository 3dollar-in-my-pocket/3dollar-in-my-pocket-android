package com.zion830.threedollars.ui.mypage.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentAskBinding
import com.zion830.threedollars.utils.getInstalledInfo
import dagger.hilt.android.AndroidEntryPoint
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class AskFragment : BaseFragment<FragmentAskBinding, UserInfoViewModel>(), OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
    }

    override fun initView() {
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.layoutFaq.setOnClickListener {
            addFQAFragment()
        }
        binding.layoutFeedback.setOnClickListener {
            openEmailApp()
        }
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.QNA, this::class.simpleName.toString())
    }

    private fun openEmailApp() {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, getString(CommonR.string.help_feedback_title))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(CommonR.string.official_email)))
            putExtra(Intent.EXTRA_TEXT, requireContext().getInstalledInfo())
            selector = selectorIntent
        }

        startActivity(intent)
    }

    private fun addFQAFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.ask_container,
            FAQFragment(),
            FAQFragment::class.java.name
        )
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAskBinding =
        FragmentAskBinding.inflate(inflater, container, false)
}