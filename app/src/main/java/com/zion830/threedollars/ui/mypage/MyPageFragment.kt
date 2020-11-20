package com.zion830.threedollars.ui.mypage

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.PagerSnapHelper
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMypageBinding
import com.zion830.threedollars.repository.model.response.Store
import com.zion830.threedollars.ui.mypage.adapter.MyReviewPreviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.adapter.MyStorePreviewRecyclerAdapter
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener


class MyPageFragment : BaseFragment<FragmentMypageBinding, UserInfoViewModel>(R.layout.fragment_mypage) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val storeAdapter = MyStorePreviewRecyclerAdapter(object : OnItemClickListener<Store> {
        override fun onClick(item: Store) {
            addShowAllStoreFragment()
        }
    })
    private val reviewAdapter = MyReviewPreviewRecyclerAdapter()

    override fun initView() {
        viewModel.updateUserInfo()

        binding.rvStore.adapter = storeAdapter
        binding.rvReview.adapter = reviewAdapter
        PagerSnapHelper().attachToRecyclerView(binding.rvStore)

        binding.layoutNickname.onSingleClick {
            addEditNameFragment()
        }
        binding.tvShowAllStore.onSingleClick {
            addShowAllStoreFragment()
        }
        binding.tvShowAllReview.onSingleClick {
            addShowAllReviewFragment()
        }
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myStore.observe(this) {
            it.store?.let { items ->
                storeAdapter.submitList(items.toMutableList())
                binding.ivNoStore.visibility = if (items.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }
        viewModel.myReview.observe(this) {
            it.review?.let { items ->
                reviewAdapter.submitList(items.toMutableList())
            }
        }
    }

    private fun addEditNameFragment() {
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.layout_container,
            EditNameFragment(),
            EditNameFragment::class.java.name
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
}