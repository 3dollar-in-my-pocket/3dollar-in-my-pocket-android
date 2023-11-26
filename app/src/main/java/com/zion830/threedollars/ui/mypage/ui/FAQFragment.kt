package com.zion830.threedollars.ui.mypage.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.threedollar.common.base.BaseFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentFaqBinding
import com.zion830.threedollars.datasource.model.v2.response.FAQCategory
import com.zion830.threedollars.ui.mypage.adapter.FaqRecyclerAdapter
import com.zion830.threedollars.ui.mypage.viewModel.FAQViewModel
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment

@AndroidEntryPoint
class FAQFragment : BaseFragment<FragmentFaqBinding, FAQViewModel>() {

    override val viewModel: FAQViewModel by viewModels()

    private val userViewModel: UserInfoViewModel by activityViewModels()

    private lateinit var adapter: FaqRecyclerAdapter

    override fun initView() {
        adapter = FaqRecyclerAdapter {
            showDeleteAccountDialog()
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        viewModel.faqTags.observe(viewLifecycleOwner) {
            binding.hashViewTags.setData(
                it,
                { tag -> buildSpannedString { bold { append(tag.description) } } },
                { tag -> tag == it.first() })

            viewModel.loadFaqs(it.first().category)
            binding.tvSelectedCategory.text = it.first().description
        }

        binding.hashViewTags.addOnTagSelectListener { item, _ ->
            val selectedCategory = (item as? FAQCategory)
            viewModel.loadFaqs(selectedCategory?.category ?: "")
            binding.hashViewTags.setData(
                viewModel.faqTags.value ?: emptyList(),
                { tag -> buildSpannedString { bold { append(tag.description) } } },
                { tag -> tag.category == selectedCategory?.category }
            )
            binding.tvSelectedCategory.text = selectedCategory?.description
        }
        binding.rvFaqs.adapter = adapter

        viewModel.faqsByTag.observe(viewLifecycleOwner) {
            adapter.submitList(it.data)
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent("FAQFragment")
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
        userViewModel.deleteUser {
            showToast(R.string.delete_account_success)
            LegacySharedPrefUtils.clearUserInfo()
            requireActivity().finish()
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFaqBinding =
        FragmentFaqBinding.inflate(inflater, container, false)
}