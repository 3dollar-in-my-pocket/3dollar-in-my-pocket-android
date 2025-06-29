package com.zion830.threedollars.ui.storeDetail.boss.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityBossFeedbackDetailBinding
import com.zion830.threedollars.ui.storeDetail.boss.adapter.BossFeedBackDetailAdapter
import com.zion830.threedollars.ui.storeDetail.boss.viewModel.BossStoreDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class BossFeedBackDetailActivity :
    BaseActivity<ActivityBossFeedbackDetailBinding, BossStoreDetailViewModel>({ ActivityBossFeedbackDetailBinding.inflate(it) }) {
    override val viewModel: BossStoreDetailViewModel by viewModels()

    private val storeId: String? by lazy { intent.getStringExtra(STORE_ID) }

    private val feedbackAdapter by lazy { BossFeedBackDetailAdapter() }

    override fun initView() {
        binding.btnBack.onSingleClick { finish() }
        binding.twFeedbackWrite.onSingleClick {
            // 피드백 작성 화면 이동 or 다이얼로그
        }
        binding.recyclerView.adapter = feedbackAdapter

        storeId?.let {
            viewModel.getFoodTruckStoreDetail(it, 0.0, 0.0)
        }

        observeFeedbacks()
    }

    private fun observeFeedbacks() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bossStoreDetailModel.collect { detail ->
                    feedbackAdapter.submitList(detail.feedbackModels)
                    binding.twFeedbackCount.text = createBoldFeedbackText(detail.feedbackModels.size)
                }
            }
        }
    }

    private fun createBoldFeedbackText(count: Int): SpannableString {
        val text = getString(R.string.str_feedback_count_html, count)
        val spannable = SpannableString(text)

        val boldStart = 0
        val boldEnd = 3
        spannable.setSpan(StyleSpan(Typeface.BOLD), boldStart, boldEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannable
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "BossFeedBackDetailActivity", screenName = "boss_feedback_detail")
    }

    companion object {
        private const val STORE_ID = "storeId"
        fun getIntent(context: Context, storeId: String? = null) =
            Intent(context, BossFeedBackDetailActivity::class.java).apply {
                storeId?.let {
                    putExtra(STORE_ID, it)
                }
            }
    }
}