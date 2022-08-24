package com.zion830.threedollars.ui.food_truck_store_detail

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckReviewBinding
import com.zion830.threedollars.databinding.CustomFoodTruckToastBinding
import com.zion830.threedollars.repository.model.v2.request.BossStoreFeedbackRequest
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import gun0912.tedimagepicker.util.ToastUtil.context
import zion830.com.common.base.BaseActivity

class FoodTruckReviewActivity :
    BaseActivity<ActivityFoodTruckReviewBinding, FoodTruckStoreDetailViewModel>(
        R.layout.activity_food_truck_review
    ) {
    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private lateinit var foodTruckReviewSummitRecyclerAdapter: FoodTruckReviewSummitRecyclerAdapter

    private var selectReviewSet = mutableSetOf<String>()

    private var storeId = ""

    override fun initView() {

        storeId = intent.getStringExtra(KEY_STORE_ID).toString()

        binding.btnBack.setOnClickListener {
            val intent = FoodTruckStoreDetailActivity.getIntent(this,storeId)
            startActivity(intent)
            finish()
        }

        binding.completeTextView.setOnClickListener {
            if (selectReviewSet.isEmpty()) {
                showToast("리뷰를 선택해주세요.")
            } else {
                viewModel.postBossStoreFeedback(
                    bossStoreId = storeId,
                    bossStoreFeedbackRequest = BossStoreFeedbackRequest(selectReviewSet.toList())
                )
            }
        }
        foodTruckReviewSummitRecyclerAdapter = FoodTruckReviewSummitRecyclerAdapter {
            if (it.feedbackType == null) return@FoodTruckReviewSummitRecyclerAdapter

            if (selectReviewSet.contains(it.feedbackType)) {
                selectReviewSet.remove(it.feedbackType)
            } else {
                selectReviewSet.add(it.feedbackType)
            }
        }
        foodTruckReviewSummitRecyclerAdapter.submitList(SharedPrefUtils.getFeedbackType())
        binding.feedbackRecyclerView.adapter = foodTruckReviewSummitRecyclerAdapter

        viewModel.postFeedbackResponse.observe(this) {
            if (it.isSuccessful) {
                val inflater = LayoutInflater.from(context)
                val binding: CustomFoodTruckToastBinding =
                    DataBindingUtil.inflate(inflater, R.layout.custom_food_truck_toast, null, false)

                Toast(context).apply {
                    setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                    duration = Toast.LENGTH_LONG
                    view = binding.root
                }.show()
                val intent = FoodTruckStoreDetailActivity.getIntent(this,storeId)
                startActivity(intent)
                finish()
            } else {
                if (it.code() == 409) {
                    showToast("오늘 이미 피드백을 추가한 가게입니다.\n내일 다시 인증해주세요 :)")
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