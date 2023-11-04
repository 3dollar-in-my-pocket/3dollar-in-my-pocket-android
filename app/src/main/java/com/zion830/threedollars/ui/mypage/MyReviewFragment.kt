package com.zion830.threedollars.ui.mypage

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.ui.EditReviewDialog
import com.zion830.threedollars.ui.mypage.vm.MyPageViewModel
import com.zion830.threedollars.ui.mypage.vm.MyReviewViewModel
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.LegacyBaseFragment

@AndroidEntryPoint
class MyReviewFragment :
    LegacyBaseFragment<FragmentMyReviewBinding, UserInfoViewModel>(R.layout.fragment_my_review) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private var adapter: MyReviewRecyclerAdapter? = null

    override fun onResume() {
        super.onResume()
        adapter?.refresh()
    }

    override fun onPause() {
        super.onPause()
        myPageViewModel.requestUserActivity()
        myPageViewModel.requestVisitHistory()
    }

    override fun initView() {
        adapter = MyReviewRecyclerAdapter(object : OnItemClickListener<ReviewDetail> {
            override fun onClick(item: ReviewDetail) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.store.storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            }
        }, {
            EditReviewDialog(it) { review ->
                myReviewViewModel.editReview(it.reviewId, review)
            }.show(requireActivity().supportFragmentManager, EditReviewDialog::class.java.simpleName)
        }, {
            myReviewViewModel.deleteReview(it.reviewId)
        })
        binding.rvReview.adapter = adapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
        observeUiData()
    }

    private fun observeUiData() {
        myReviewViewModel.updateReview.observe(viewLifecycleOwner) {
            adapter?.refresh()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myReviewViewModel.myReviewPager.collectLatest {
                        adapter?.submitData(it)
                    }
                }
                launch {
                    adapter?.loadStateFlow?.collectLatest { loadState ->
                        if (loadState.refresh is LoadState.NotLoading) {
                            binding.ivEmpty.isVisible = adapter?.itemCount == 0
                            binding.layoutNoData.root.isVisible = adapter?.itemCount == 0
                        }
                    }
                }
            }
        }
    }
}