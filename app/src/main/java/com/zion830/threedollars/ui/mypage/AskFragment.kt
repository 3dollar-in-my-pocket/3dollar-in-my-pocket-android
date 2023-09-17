package com.zion830.threedollars.ui.mypage

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentAskBinding
import com.zion830.threedollars.utils.getInstalledInfo
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import com.threedollar.common.ext.addNewFragment

@AndroidEntryPoint
class AskFragment : LegacyBaseFragment<FragmentAskBinding, UserInfoViewModel>(R.layout.fragment_ask) {

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
}