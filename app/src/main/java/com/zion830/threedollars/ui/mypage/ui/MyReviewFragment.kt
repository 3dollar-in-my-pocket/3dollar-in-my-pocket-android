package com.zion830.threedollars.ui.mypage.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.zion830.threedollars.ui.my.page.MyPageViewModel
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnBackPressedListener
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMyReviewBinding
import com.zion830.threedollars.ui.mypage.adapter.ReviewPagerFragmentStateAdapter
import com.zion830.threedollars.ui.mypage.viewModel.MyReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        }
        binding.reviewViewPager.adapter = fragmentAdapter
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            viewModel.updateUserInfo()
        }
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.MY_REVIEW, this::class.java.simpleName)
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyReviewBinding =
        FragmentMyReviewBinding.inflate(inflater, container, false)

    override fun onBackPressed() {
        activity?.supportFragmentManager?.popBackStack()
        viewModel.updateUserInfo()
    }
}