package com.threedollar.presentation.polls

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.threedollar.domain.data.PollItem
import com.threedollar.domain.data.PollList
import com.threedollar.network.data.poll.request.PollCreateApiRequest
import com.threedollar.presentation.R
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

@AndroidEntryPoint
class PollListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPollListBinding
    private val viewModel: PollListViewModel by viewModels()
    private val adapter by lazy {
        PollListAdapter({ pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
        }, {
            startActivity(Intent(this, PollDetailActivity::class.java).apply {
                putExtra("id", it.poll.pollId)
            })
        })
    }
    private val pollItems = mutableListOf<PollItem?>()
    private var pollList: PollList? = null
    private var isLoading = false
    private var categoryId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPollListBinding.inflate(getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        setContentView(binding.root)
        categoryId = intent.getStringExtra("category").orEmpty()
        if (categoryId.isEmpty()) {
            finish()
        }
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
                    PollCreateApiRequest(
                        title = title,
                        options = listOf(PollCreateApiRequest.Option(first), PollCreateApiRequest.Option(second)),
                        categoryId = categoryId,
                        startDateTime = formattedDate
                    )
                )
            }.show(supportFragmentManager, "")
        }
        binding.imgClose.onSingleClick { finish() }
        lifecycleScope.launch {
            launch {
                viewModel.userPollPolicy.collect {
                    binding.llPollCreate.isEnabled = it.currentCount < it.limitCount
                    binding.llPollCreate.isSelected = it.currentCount < it.limitCount
                    binding.twCreateCount.text = getString(R.string.str_create_count, it.currentCount, it.limitCount)
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

    override fun onResume() {
        super.onResume()
        if (binding.twPollLatest.isSelected || binding.twPollPopular.isSelected) {
            selectedPollMenu(binding.twPollLatest.isSelected)
        } else {
            selectedPollMenu(true)
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