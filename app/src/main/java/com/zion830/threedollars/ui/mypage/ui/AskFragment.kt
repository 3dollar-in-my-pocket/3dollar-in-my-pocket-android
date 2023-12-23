package com.zion830.threedollars.ui.mypage.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentAskBinding
import com.zion830.threedollars.utils.getInstalledInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AskFragment : BaseFragment<FragmentAskBinding, UserInfoViewModel>() {

    override val viewModel: UserInfoViewModel by activityViewModels()

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

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "AskFragment", screenName = null)
    }

    private fun openEmailApp() {
        val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_feedback_title))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.official_email)))
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