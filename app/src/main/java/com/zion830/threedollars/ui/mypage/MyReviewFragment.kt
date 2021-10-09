package com.zion830.threedollars.ui.mypage

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener

class MyReviewFragment : BaseFragment<FragmentMyReviewBinding, UserInfoViewModel>(R.layout.fragment_my_review) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private lateinit var adapter: MyReviewRecyclerAdapter

    override fun initView() {
        adapter = MyReviewRecyclerAdapter(object : OnItemClickListener<ReviewDetail> {
            override fun onClick(item: ReviewDetail) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        })
        binding.rvReview.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myAllReview.observe(this) {
            adapter.submitList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.SHOW_STORE_DETAIL -> {
                // viewModel.updatePreviewData()
            }
        }
    }
}