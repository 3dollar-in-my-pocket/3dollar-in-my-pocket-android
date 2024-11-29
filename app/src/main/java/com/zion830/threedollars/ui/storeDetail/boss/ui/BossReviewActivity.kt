package com.zion830.threedollars.ui.storeDetail.boss.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckReviewBinding
import com.zion830.threedollars.ui.storeDetail.boss.adapter.BossReviewSummitRecyclerAdapter
import com.zion830.threedollars.ui.storeDetail.boss.viewModel.BossStoreDetailViewModel
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import javax.inject.Inject

@AndroidEntryPoint
class BossReviewActivity :
    BaseActivity<ActivityFoodTruckReviewBinding, BossStoreDetailViewModel>({ ActivityFoodTruckReviewBinding.inflate(it) }) {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: BossStoreDetailViewModel by viewModels()

    private val bossReviewSummitRecyclerAdapter: BossReviewSummitRecyclerAdapter by lazy {
        BossReviewSummitRecyclerAdapter {
            if (selectReviewSet.contains(it.feedbackType)) {
                selectReviewSet.remove(it.feedbackType)
            } else {
                selectReviewSet.add(it.feedbackType)
            }
        }
    }

    private var selectReviewSet = mutableSetOf<String>()

    private var storeId = ""

    override fun initView() {

        storeId = intent.getStringExtra(KEY_STORE_ID).toString()

        binding.btnBack.onSingleClick {
            val intent = BossStoreDetailActivity.getIntent(this, storeId)
            startActivity(intent)
            finish()
        }

        binding.completeTextView.onSingleClick {
            if (selectReviewSet.isEmpty()) {
                showToast("리뷰를 선택해주세요.")
            } else {
                val bundle = Bundle().apply {
                    putString("screen", "boss_store_review")
                    putString("store_id", storeId)
                }
                EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
                viewModel.postBossStoreFeedback(bossStoreId = storeId, bossStoreFeedbackRequest = selectReviewSet.toList())
            }
        }
        bossReviewSummitRecyclerAdapter.submitList(sharedPrefUtils.getList<FeedbackTypeResponse>(SharedPrefUtils.BOSS_FEED_BACK_LIST))
        binding.feedbackRecyclerView.adapter = bossReviewSummitRecyclerAdapter

        iniFlows()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "BossReviewActivity",screenName = "boss_store_review")
    }

    private fun iniFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.postFeedback.collect {
                        if (it?.ok == true) {
                            showCustomBlackToast(getString(R.string.review_toast))
                            val intent = BossStoreDetailActivity.getIntent(this@BossReviewActivity, storeId)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"

        fun getIntent(context: Context, storeId: String) =
            Intent(context, BossReviewActivity::class.java).apply {
                putExtra(KEY_STORE_ID, storeId)
            }
    }
}