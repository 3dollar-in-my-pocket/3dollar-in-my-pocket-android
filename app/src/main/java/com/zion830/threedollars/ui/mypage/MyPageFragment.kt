package com.zion830.threedollars.ui.mypage

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearSnapHelper
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentNewMyPageBinding
import com.zion830.threedollars.ui.MyPageSettingFragment
import com.zion830.threedollars.ui.mypage.adapter.RecentVisitHistoryRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment


class MyPageFragment : BaseFragment<FragmentNewMyPageBinding, MyPageViewModel>(R.layout.fragment_new_my_page) {

    override val viewModel: MyPageViewModel by activityViewModels()

    private val userInfoViewModel: UserInfoViewModel by activityViewModels()

    private lateinit var visitHistoryAdapter: RecentVisitHistoryRecyclerAdapter

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        viewModel.requestUserActivity()
        viewModel.requestVisitHistory()
        userInfoViewModel.updateUserInfo()
    }

    override fun initView() {
        visitHistoryAdapter = RecentVisitHistoryRecyclerAdapter {
            val intent = StoreDetailActivity.getIntent(requireContext(), it)
            startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
        }

        binding.rvRecentVisitHistory.adapter = visitHistoryAdapter
        LinearSnapHelper().attachToRecyclerView(binding.rvRecentVisitHistory)

        binding.ibSetting.onSingleClick {
            addSettingPageFragment()
        }
        binding.layoutStore.onSingleClick {
            addShowAllStoreFragment()
        }
        binding.layoutReview.onSingleClick {
            addShowAllReviewFragment()
        }
        binding.tvMessage.setOnClickListener {
            addShowAllVisitHistoryFragment()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myVisitHistory.observe(viewLifecycleOwner) {
            visitHistoryAdapter.submitList(it)
        }
        userInfoViewModel.userInfo.observe(viewLifecycleOwner) {
            binding.tvName.text = it.data.name
            binding.tvUserMedal.text = it.data.medal?.name ?: "장착한 칭호가 없어요!"
            binding.ivProfile.loadUrlImg(it.data.medal?.iconUrl)
        }
    }

    private fun addSettingPageFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyPageSettingFragment(),
            MyPageSettingFragment::class.java.name
        )
    }

    private fun addShowAllStoreFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyStoreFragment(),
            MyStoreFragment::class.java.name
        )
    }

    private fun addShowAllReviewFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyReviewFragment(),
            MyReviewFragment::class.java.name
        )
    }

    private fun addShowAllVisitHistoryFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            MyVisitHistoryFragment(),
            MyVisitHistoryFragment::class.java.name
        )
    }
}