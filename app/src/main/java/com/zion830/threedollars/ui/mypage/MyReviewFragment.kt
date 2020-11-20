package com.zion830.threedollars.ui.mypage

import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import zion830.com.common.base.BaseFragment

class MyReviewFragment : BaseFragment<FragmentMyReviewBinding, UserInfoViewModel>(R.layout.fragment_my_review) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val adapter = MyReviewRecyclerAdapter()

    override fun initView() {
        binding.rvReview.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myAllReview.observe(this) {
            adapter.submitList(it)
        }
    }
}