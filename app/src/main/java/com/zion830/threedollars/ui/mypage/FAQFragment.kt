package com.zion830.threedollars.ui.mypage

import android.app.AlertDialog
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentFaqBinding
import com.zion830.threedollars.repository.model.response.FaqTag
import com.zion830.threedollars.ui.mypage.adapter.FaqRecyclerAdapter
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment


class FAQFragment : BaseFragment<FragmentFaqBinding, FAQViewModel>(R.layout.fragment_faq) {

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
                it.getAllTag(),
                { tag -> buildSpannedString { bold { append(tag.name) } } },
                { tag -> tag.id == -1 })
            viewModel.loadFaqs(it)
        }

        binding.hashViewTags.addOnTagSelectListener { item, selected ->
            viewModel.loadFaqs(if ((item as FaqTag).id == -1) viewModel.faqTags.value as ArrayList<FaqTag> else arrayListOf(item))
            binding.hashViewTags.setData(
                viewModel.faqTags.value?.getAllTag() ?: listOf(),
                { tag -> buildSpannedString { bold { append(tag.name) } } },
                { tag -> tag.id == item.id }
            )
        }
        binding.rvFaq.adapter = adapter
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
            SharedPrefUtils.clearUserInfo()
            requireActivity().finish()
        }
    }
}