package com.zion830.threedollars.ui.store_detail

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewSortType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.databinding.ActivityStoreReviewDetailBinding
import com.zion830.threedollars.ui.addstore.activity.NewStoreActivity
import com.zion830.threedollars.ui.addstore.adapter.ReviewRecyclerAdapter
import com.zion830.threedollars.ui.report_store.AddReviewDialog
import com.zion830.threedollars.ui.store_detail.adapter.MoreReviewAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoreReviewDetailActivity :
    BaseActivity<ActivityStoreReviewDetailBinding, StoreDetailViewModel>({ ActivityStoreReviewDetailBinding.inflate(it) }) {
    override val viewModel: StoreDetailViewModel by viewModels()

    private var reviewSortType = ReviewSortType.LATEST
    private val storeId: Int by lazy { intent.getIntExtra(STORE_ID, 0) }

    private val moreReviewAdapter by lazy {
        MoreReviewAdapter(
            object : OnItemClickListener<ReviewContentModel> {
                override fun onClick(item: ReviewContentModel) {
                    if (item.review.isOwner) {
                        AddReviewDialog.getInstance(item).show(supportFragmentManager, AddReviewDialog::class.java.name)
                    } else {
//                    viewModel.deleteReview(item.reviewId)
                    }
                }
            }
        )
    }

    override fun initView() {
        initButton()
        initViewModel()
        initAdapter()
        initTabLayout()
        initFlow()
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.reviewWriteTextView.setOnClickListener {
            AddReviewDialog.getInstance(storeId = storeId).show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
    }

    private fun initAdapter() {
        binding.reviewRecyclerView.adapter = moreReviewAdapter
    }

    private fun initTabLayout() {
        binding.sortTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                reviewSortType = when (tab.position) {
                    0 -> {
                        ReviewSortType.LATEST
                    }
                    1 -> {
                        ReviewSortType.HIGHEST_RATING
                    }
                    else -> {
                        ReviewSortType.LOWEST_RATING
                    }
                }
                viewModel.getReview(storeId, reviewSortType)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initViewModel() {
        viewModel.getReview(storeId, reviewSortType)
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.reviewPagingData.collectLatest {
                        it?.let { pagingData ->
                            moreReviewAdapter.submitData(pagingData)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val STORE_ID = "store_id"

        fun getInstance(context: Context, storeId: Int) = Intent(context, StoreReviewDetailActivity::class.java).apply {
            putExtra(STORE_ID, storeId)
        }
    }
}