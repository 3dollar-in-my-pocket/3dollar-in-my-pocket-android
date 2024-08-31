package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.google.android.material.tabs.TabLayoutMediator
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.BOSS_STORE
import com.threedollar.common.utils.Constants.USER_STORE
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.ui.dialog.EditReviewDialog
import com.zion830.threedollars.ui.mypage.adapter.MyReviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.adapter.ReviewPagerFragmentStateAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyPageViewModel
import com.zion830.threedollars.ui.mypage.viewModel.MyReviewViewModel
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReviewFragment : BaseFragment<FragmentMyReviewBinding, UserInfoViewModel>() , OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private var fragmentAdapter: ReviewPagerFragmentStateAdapter? = null



    override fun onPause() {
        super.onPause()
        myPageViewModel.requestUserActivity()
    }


    override fun initView() {
        fragmentAdapter = ReviewPagerFragmentStateAdapter(requireActivity()).apply {
            addFragment(ReviewFragmentItem())
            addFragment(FeedBackFragmentItem())
        }
        binding.reviewViewPager.adapter = fragmentAdapter
        TabLayoutMediator(binding.sortTabLayout, binding.reviewViewPager) { tab, position ->
            tab.setText(if (position == 0) R.string.review_user_tab_title else R.string.review_boss_tab_title)
        }.attach()
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MyReviewFragment", screenName = null)
    }
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyReviewBinding =
        FragmentMyReviewBinding.inflate(inflater, container, false)

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        viewModel.updateUserInfo()
    }
}