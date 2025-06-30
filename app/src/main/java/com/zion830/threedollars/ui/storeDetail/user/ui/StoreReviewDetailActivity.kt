package com.zion830.threedollars.ui.storeDetail.user.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.google.android.material.tabs.TabLayout
import com.home.domain.data.store.ReviewContentModel
import com.home.domain.data.store.ReviewSortType
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_EDIT_REVIEW
import com.threedollar.common.utils.Constants.CLICK_REPORT
import com.threedollar.common.utils.Constants.CLICK_SORT
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.databinding.ActivityStoreReviewDetailBinding
import com.zion830.threedollars.ui.dialog.AddReviewDialog
import com.zion830.threedollars.ui.dialog.ReportReviewDialog
import com.zion830.threedollars.ui.storeDetail.user.adapter.MoreReviewAdapter
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

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
                    val bundle = Bundle().apply {
                        putString("screen", "review_list")
                        putString("review_id", item.review.reviewId.toString())
                    }
                    EventTracker.logEvent(if (item.review.isOwner) CLICK_EDIT_REVIEW else CLICK_REPORT, bundle)
                    if (item.review.isOwner) {
                        AddReviewDialog.getInstance(item, storeId).show(supportFragmentManager, AddReviewDialog::class.java.name)
                    } else {
                        if (item.reviewReport.reportedByMe) {
                            showAlreadyReportDialog()
                        } else {
                            ReportReviewDialog.getInstance(item, storeId).apply {
                                setReportReasons(viewModel.reportReasons.value ?: emptyList())
                                setOnReportClickListener { sId, rId, request ->
                                    viewModel.reportReview(sId, rId, request)
                                }
                            }.show(supportFragmentManager, ReportReviewDialog::class.java.name)
                        }
                    }
                }
            },
        )
    }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        initButton()
        initViewModel()
        initAdapter()
        initTabLayout()
        initFlow()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "StoreReviewDetailActivity", screenName = "review_list")
    }

    private fun initButton() {
        binding.backButton.onSingleClick {
            setResult(RESULT_OK)
            finish()
        }

        binding.reviewWriteTextView.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "review_list")
            }
            EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
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
                val bundle = Bundle().apply {
                    putString("screen", "review_list")
                    putString("type", reviewSortType.name)
                }
                EventTracker.logEvent(CLICK_SORT, bundle)
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
                            binding.reviewRecyclerView.postDelayed({
                                binding.reviewRecyclerView.smoothScrollToPosition(0)
                            }, 500)
                        }
                    }
                }

                launch {
                    viewModel.serverError.collect {
                        it?.let { showToast(it) }
                    }
                }

                launch {
                    viewModel.reviewSuccessEvent.collect {
                        if (it) {
                            viewModel.getReview(storeId, reviewSortType)
                        }
                    }
                }
            }
        }

        viewModel.addReviewResult.observe(this) {
            if (it) {
                viewModel.getReview(storeId, reviewSortType)
            }
        }
    }

    private fun showAlreadyReportDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("신고")
        builder.setMessage("이미 신고한 댓글입니다!")

        builder.setPositiveButton("확인") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        const val STORE_ID = "store_id"

        fun getInstance(context: Context, storeId: Int) = Intent(context, StoreReviewDetailActivity::class.java).apply {
            putExtra(STORE_ID, storeId)
        }
    }
}
