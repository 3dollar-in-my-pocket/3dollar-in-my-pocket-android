package com.threedollar.presentation.polls

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.listener.EventTrackerListener
import com.threedollar.common.utils.Constants
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.domain.model.PollCreateModel
import com.threedollar.presentation.R
import com.threedollar.common.R as CommonR
import com.threedollar.presentation.databinding.ActivityPollListBinding
import com.threedollar.presentation.dialog.CreatePollDialog
import com.threedollar.presentation.poll.PollDetailActivity
import com.threedollar.presentation.utils.selectedPoll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class PollListActivity : BaseActivity<ActivityPollListBinding, PollListViewModel>({ ActivityPollListBinding.inflate(it) }) {

    @Inject
    lateinit var eventTrackerListener: EventTrackerListener
    override val viewModel: PollListViewModel by viewModels()
    private val registerPollDetail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            it.data?.let { intent ->
                val pollItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra("pollItem", PollItem::class.java)
                else intent.getParcelableExtra("pollItem")
                val index = pollItems.indexOfFirst { item -> item?.poll?.pollId == pollItem?.poll?.pollId }
                if (index > -1) {
                    pollItems[index] = pollItem
                    adapter.submitList(pollItems.toList())
                }
            }
        }
    }

    private val adapter: PollListAdapter by lazy {
        PollListAdapter({ pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
            val bundle = Bundle().apply {
                putString("screen", "poll_list")
                putString("poll_id", pollId)
                putString("option_id", optionId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL_OPTION, bundle)
        }, {
            registerPollDetail.launch(Intent(this, PollDetailActivity::class.java).apply {
                putExtra("id", it.poll.pollId)
            })
            val bundle = Bundle().apply {
                putString("screen", "poll_list")
                putString("poll_id", it.poll.pollId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL, bundle)
        })
    }
    private val pollItems = mutableListOf<PollItem?>()
    private var pollList: PollList? = null
    private var isLoading = false
    private var categoryId = ""
    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "PollListActivity", screenName = "poll_list")
    }

    override fun initView() {
        categoryId = intent.getStringExtra("category").orEmpty()
        if (categoryId.isEmpty()) {
            finish()
        }
        selectedPollMenu(true)
        initAdapter()
        initButton()
        initFlow()
    }

    private fun initAdapter() {
        binding.recyclerPoll.adapter = adapter
        binding.recyclerPoll.itemAnimator = null
        binding.recyclerPoll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                layoutManager?.let {
                    val totalItemCount = it.itemCount
                    val lastVisibleItemPosition = it.findLastVisibleItemPosition()

                    if (!isLoading && totalItemCount <= (lastVisibleItemPosition + 3)) {
                        pollList?.let { pollList ->
                            if (pollList.cursor.hasMore) {
                                viewModel.getPollItems(
                                    categoryId,
                                    if (binding.twPollLatest.isSelected) PollSort.Latest.type else PollSort.Popular.type,
                                    pollList.cursor.nextCursor
                                )
                                isLoading = true
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initButton() {
        binding.clPollLatest.onSingleClick {
            if (!binding.twPollLatest.isSelected) selectedPollMenu(true)
        }
        binding.clPollPopular.onSingleClick {
            if (!binding.twPollPopular.isSelected) selectedPollMenu(false)
        }
        binding.llPollCreate.onSingleClick {
            CreatePollDialog().setCreatePoll { title, first, second ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)
                viewModel.createPoll(
                    PollCreateModel(
                        title = title,
                        content = "",
                        options = listOf(first, second),
                        categoryId = categoryId,
                        period = 0,
                        isAllowMultipleChoice = false
                    )
                )
                val bundle = Bundle().apply {
                    putString("screen", "create_poll")
                    putString("title", title)
                    putString("poll_first_option", first)
                    putString("poll_second_option", second)
                }
                eventTrackerListener.logEvent(Constants.CLICK_CREATE_POLL, bundle)
            }.show(supportFragmentManager, "")
            val bundle = Bundle().apply {
                putString("screen", "poll_list")
            }
            eventTrackerListener.logEvent(Constants.CLICK_CREATE_POLL, bundle)
        }
        binding.imgClose.onSingleClick { finish() }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            launch {
                viewModel.userPollPolicy.collect {
                    binding.llPollCreate.isEnabled = it.currentCount < it.limitCount
                    binding.llPollCreate.isSelected = it.currentCount < it.limitCount
                    binding.twCreateCount.text = getString(CommonR.string.str_create_count, it.currentCount, it.limitCount)
                }
            }
            launch {
                viewModel.createPoll.collect {
                    if (binding.twPollLatest.isSelected) {
                        isLoading = true
                        pollList = null
                        pollItems.clear()
                        adapter.submitList(emptyList())
                        viewModel.getPollItems(categoryId, PollSort.Latest.type, "")
                    }
                }
            }
            launch {
                viewModel.pollSelected.collect {
                    val pollId = it.first
                    val optionId = it.second
                    val selectPoll = pollItems.find { it?.poll?.pollId == pollId } ?: return@collect
                    pollItems[pollItems.indexOfFirst { it?.poll?.pollId == pollId }] = selectedPoll(selectPoll, optionId)
                    adapter.submitList(pollItems.toList())
                }
            }
            launch {
                viewModel.pollItems.collect {
                    val isFirstPollItems = pollList == null
                    isLoading = false
                    pollList = it
                    pollItems.addAll(it.pollItems)
                    if (isFirstPollItems) {
                        binding.recyclerPoll.adapter = null
                        binding.recyclerPoll.adapter = adapter
                    }
                    adapter.submitList(pollItems.toList())
                }
            }
            launch {
                viewModel.toast.collect {
                    Toast.makeText(this@PollListActivity, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectedPollMenu(isLast: Boolean) {
        binding.twPollLatest.isSelected = isLast
        binding.twPollPopular.isSelected = !isLast
        binding.vwPollLatest.isVisible = isLast
        binding.vwPollPopular.isVisible = !isLast
        isLoading = true
        pollList = null
        pollItems.clear()
        if (!isLast) pollItems.add(null)
        adapter.submitList(emptyList())
        adapter.settingPollSort(if (isLast) PollSort.Latest else PollSort.Popular)
        viewModel.getPollItems(categoryId, if (isLast) PollSort.Latest.type else PollSort.Popular.type, "")
    }
}