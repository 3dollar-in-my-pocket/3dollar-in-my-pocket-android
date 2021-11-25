package com.zion830.threedollars.ui.mypage

import android.content.Intent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSnapHelper
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.UserInfoViewModel
import com.zion830.threedollars.databinding.FragmentMypageBinding
import com.zion830.threedollars.repository.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.MyPageSettingFragment
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.mypage.adapter.MyReviewPreviewRecyclerAdapter
import com.zion830.threedollars.ui.mypage.adapter.MyStorePreviewRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener


class MyPageFragment :
    BaseFragment<FragmentMypageBinding, UserInfoViewModel>(R.layout.fragment_mypage) {

    override val viewModel: UserInfoViewModel by activityViewModels()

    private val storeDetailViewModel: StoreDetailViewModel by viewModels()


    private lateinit var storeAdapter: MyStorePreviewRecyclerAdapter

    private lateinit var reviewAdapter: MyReviewPreviewRecyclerAdapter

    override fun initView() {
        storeAdapter = MyStorePreviewRecyclerAdapter(
            object : OnItemClickListener<StoreInfo> {
                override fun onClick(item: StoreInfo) {
                    storeDetailViewModel.requestStoreInfo(
                        storeId = item.storeId,
                        item.latitude,
                        item.longitude
                    )
                }
            }, object : OnItemClickListener<StoreInfo> {
                override fun onClick(item: StoreInfo) {
                    addShowAllStoreFragment()
                }
            })
        reviewAdapter = MyReviewPreviewRecyclerAdapter(
            object : OnItemClickListener<ReviewDetail> {
                override fun onClick(item: ReviewDetail) {
                    val intent = StoreDetailActivity.getIntent(requireContext(), item.storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
                }
            }
        )

        binding.rvStore.adapter = storeAdapter
        binding.rvReview.adapter = reviewAdapter
        LinearSnapHelper().attachToRecyclerView(binding.rvStore)

        binding.layoutNickname.onSingleClick {
            addSettingPageFragment()
        }
        binding.tvShowAllStore.onSingleClick {
            addShowAllStoreFragment()
        }
        binding.tvShowAllReview.onSingleClick {
            addShowAllReviewFragment()
        }
        viewModel.updateUserInfo()
        observeUiData()
    }

    private fun observeUiData() {
        viewModel.myStore.observe(this) {
            it?.let { items ->
                storeAdapter.submitList(items)
                binding.ivNoStore.visibility =
                    if (items.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }
        viewModel.myReview.observe(this) {
            it.let { items ->
                reviewAdapter.submitList(items?.contents)
            }
        }
        storeDetailViewModel.isExistStoreInfo.observe(viewLifecycleOwner) { isExistStore ->
            val storeId = isExistStore.first
            val isExist = isExistStore.second
            if (isExist) {
                val intent = StoreDetailActivity.getIntent(requireContext(), storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_DETAIL)
            } else {
                showToast(R.string.exist_store_error)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.ADD_STORE, Constants.SHOW_STORE_DETAIL -> {
                viewModel.updateUserInfo()
            }
        }
    }
}