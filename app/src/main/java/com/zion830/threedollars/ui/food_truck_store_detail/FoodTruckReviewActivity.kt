package com.zion830.threedollars.ui.food_truck_store_detail

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckReviewBinding
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FoodTruckReviewActivity :
    BaseActivity<ActivityFoodTruckReviewBinding, FoodTruckStoreDetailViewModel>({ ActivityFoodTruckReviewBinding.inflate(it) }) {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private val foodTruckReviewSummitRecyclerAdapter: FoodTruckReviewSummitRecyclerAdapter by lazy {
        FoodTruckReviewSummitRecyclerAdapter {
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

        binding.btnBack.setOnClickListener {
            val intent = FoodTruckStoreDetailActivity.getIntent(this, storeId)
            startActivity(intent)
            finish()
        }

        binding.completeTextView.setOnClickListener {
            if (selectReviewSet.isEmpty()) {
                showToast("리뷰를 선택해주세요.")
            } else {
                viewModel.postBossStoreFeedback(bossStoreId = storeId, bossStoreFeedbackRequest = selectReviewSet.toList())
            }
        }
        foodTruckReviewSummitRecyclerAdapter.submitList(sharedPrefUtils.getList<FeedbackTypeResponse>(SharedPrefUtils.BOSS_FEED_BACK_LIST))
        binding.feedbackRecyclerView.adapter = foodTruckReviewSummitRecyclerAdapter

        iniFlows()
    }

    private fun iniFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.postFeedback.collect {
                        if (it?.ok == true) {
                            showCustomBlackToast(getString(R.string.review_toast))
                            val intent = FoodTruckStoreDetailActivity.getIntent(this@FoodTruckReviewActivity, storeId)
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
            Intent(context, FoodTruckReviewActivity::class.java).apply {
                putExtra(KEY_STORE_ID, storeId)
            }
    }
}