package com.zion830.threedollars.ui.storeDetail.boss.ui

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.domain.home.data.store.ReviewContentModel
import com.threedollar.domain.home.data.store.ReviewSortType
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityBossReviewDetailBinding
import com.zion830.threedollars.ui.dialog.AddReviewDialog
import com.zion830.threedollars.ui.dialog.OnReviewEditCallback
import com.zion830.threedollars.ui.dialog.ReportReviewDialog
import com.zion830.threedollars.ui.dialog.ReviewPhotoDialog
import com.zion830.threedollars.ui.storeDetail.boss.adapter.FoodTruckReviewAdapter
import com.zion830.threedollars.ui.storeDetail.boss.listener.OnReviewImageClickListener
import com.zion830.threedollars.ui.storeDetail.boss.viewModel.BossReviewDetailViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class BossReviewDetailActivity :
    BaseActivity<ActivityBossReviewDetailBinding, BossReviewDetailViewModel>({ ActivityBossReviewDetailBinding.inflate(it) }) {
    override val viewModel: BossReviewDetailViewModel by viewModels()
    private val storeId: Int by lazy { intent.getIntExtra(STORE_ID, 0) }
    private val foodTruckReviewAdapter: FoodTruckReviewAdapter by lazy {
        FoodTruckReviewAdapter(
            onReviewImageClickListener = object : OnReviewImageClickListener {
                override fun onImageClick(clickedImage: ImageModel, allImages: List<ImageModel>, clickedIndex: Int) {
                    ReviewPhotoDialog.getInstance(allImages, clickedIndex)
                        .show(supportFragmentManager, "ReviewPhotoDialog")
                }
            },
            onReviewReportClickListener = object : OnItemClickListener<ReviewContentModel> {
                override fun onClick(item: ReviewContentModel) {
                    if (item.reviewReport.reportedByMe) {
                        showAlreadyReportDialog()
                    } else {
                        val sid = storeId
                        ReportReviewDialog.getInstance(item, sid).apply {
                            setReportReasons(viewModel.reportReasons.value ?: emptyList())
                            setOnReportClickListener { sId, rId, request ->
                                viewModel.reportReview(sId, rId, request)
                            }
                        }.show(supportFragmentManager, ReportReviewDialog::class.java.name)
                    }
                }
            },
            onReviewEditClickListener = object : OnItemClickListener<ReviewContentModel> {
                override fun onClick(item: ReviewContentModel) {
                    val dialog = AddReviewDialog.getInstance(
                        item, 
                        storeId,
                        object : OnReviewEditCallback {
                            override fun onReviewEdited(updatedReview: ReviewContentModel) {
                                viewModel.updateReviewInPagingData(updatedReview)
                            }
                        }
                    )
                    dialog.show(supportFragmentManager, "AddReviewDialog")
                }
            },
            onReviewLikeClickListener = object : OnItemClickListener<ReviewContentModel> {
                override fun onClick(item: ReviewContentModel) {
                    val sticker = item.stickers.firstOrNull()
                    if (sticker != null) {
                        viewModel.putLike(storeId, item.review.reviewId.toString(), if (sticker.reactedByMe) "" else sticker.stickerId)
                    }
                }
            },
            onMoreClickListener = {},
            isMore = false
        )
    }

    override fun initView() {
        setLightSystemBars()
        binding.reviewRecyclerView.adapter = foodTruckReviewAdapter
        binding.btnBack.onSingleClick { finish() }
        binding.twReviewWrite.onSingleClick { 
            moveFoodTruckReviewWriteActivity()
        }
        initFlow()
        initTabLayout()
        viewModel.getReviewList(storeId, ReviewSortType.LATEST)
    }

    private fun initTabLayout() {
        binding.tabReviewSort.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.getReviewList(
                    storeId, when (tab.position) {
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
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "BossReviewDetailActivity", screenName = "boss_review_list")
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.reviewPagingData.collectLatest {
                        it?.let { pagingData ->
                            foodTruckReviewAdapter.submitData(pagingData)
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
                    viewModel.feedbackExists.collect { exists ->
                        exists?.let {
                            if (it) {
                                showToast(getString(CommonR.string.already_reviewed_today))
                            } else {
                                val intent = BossReviewWriteActivity.getIntent(this@BossReviewDetailActivity, storeId.toString())
                                startActivity(intent)
                            }
                            // Reset the state after handling
                            viewModel.resetFeedbackExistsState()
                        }
                    }
                }
            }
        }
    }

    private fun moveFoodTruckReviewWriteActivity() {
        // 피드백 존재 여부 확인
        viewModel.checkFeedbackExists(storeId.toString())
    }

    private fun showAlreadyReportDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(getString(CommonR.string.review_report_dialog_title))
        builder.setMessage(getString(CommonR.string.review_report_already_message))
        builder.setPositiveButton(CommonR.string.report_confirm) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    companion object {
        const val STORE_ID = "storeId"
        fun getIntent(context: Context, storeId: String? = null, deepLinkStoreId: String? = null) =
            Intent(context, BossReviewDetailActivity::class.java).apply {
                storeId?.toIntOrNull()?.let { putExtra(STORE_ID, it) }
                deepLinkStoreId?.toIntOrNull()?.let { putExtra(STORE_ID, it) }
            }
    }
}
