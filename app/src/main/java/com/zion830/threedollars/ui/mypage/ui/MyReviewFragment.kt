package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.ui.mypage.adapter.ReviewPagerFragmentStateAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class MyReviewFragment : BaseFragment<FragmentMyReviewBinding, UserInfoViewModel>(), OnBackPressedListener {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val myReviewViewModel: MyReviewViewModel by viewModels()

    private val myPageViewModel: MyPageViewModel by activityViewModels()

    private var fragmentAdapter: ReviewPagerFragmentStateAdapter? = null


    override fun onPause() {
        super.onPause()
        myPageViewModel.getUserInfo()
    }


    override fun initView() {
        fragmentAdapter = ReviewPagerFragmentStateAdapter(requireActivity()).apply {
            addFragment(ReviewFragmentItem())
            addFragment(FeedBackFragmentItem())
        }
        binding.reviewViewPager.adapter = fragmentAdapter
        TabLayoutMediator(binding.sortTabLayout, binding.reviewViewPager) { tab, position ->
            when (fragmentAdapter?.getFragmentOrNull(position)) {
                is ReviewFragmentItem -> {
                    tab.setText(CommonR.string.review_user_tab_title)
                }
                is FeedBackFragmentItem -> {
                    tab.setText(CommonR.string.review_boss_tab_title)
                }
            }
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