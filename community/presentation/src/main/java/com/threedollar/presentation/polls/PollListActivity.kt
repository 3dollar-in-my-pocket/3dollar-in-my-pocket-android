package com.threedollar.presentation.polls

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.databinding.ActivityPollListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PollListActivity : ComponentActivity() {

    private lateinit var binding: ActivityPollListBinding
    private val viewModel: PollListViewModel by viewModels()
    private val adapter by lazy {
        PollListAdapter({ pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
        }, {})
    }
    private val pollItems = mutableListOf<PollItem>()
    private var categoryId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPollListBinding.inflate(getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        setContentView(binding.root)
        categoryId = intent.getStringExtra("category").orEmpty()
        if (categoryId.isEmpty()) {
            finish()
        }
        lifecycleScope.launch {
            launch {
                viewModel.pollSelected.collect {
                    val pollId = it.first
                    val optionId = it.second
                    val selectPoll = pollItems.find { it.poll.pollId == pollId } ?: return@collect
                    var firstChoice = selectPoll.poll.options[0]
                    var secondChoice = selectPoll.poll.options[1]
                    val editFirstCount = if (firstChoice.optionId == optionId) 1 else -1
                    val editSecondCount = if (secondChoice.optionId == optionId) 1 else -1
                    firstChoice = firstChoice.copy(
                        choice = firstChoice.choice.copy(
                            selectedByMe = firstChoice.optionId == optionId,
                            count = firstChoice.choice.count + editFirstCount
                        )
                    )
                    secondChoice = secondChoice.copy(
                        choice = secondChoice.choice.copy(
                            selectedByMe = secondChoice.optionId == optionId,
                            count = secondChoice.choice.count + editSecondCount
                        )
                    )
                    pollItems[pollItems.indexOfFirst { it.poll.pollId == pollId }] =
                        selectPoll.copy(poll = selectPoll.poll.copy(options = listOf(firstChoice, secondChoice)))
                }
            }
            launch {
                viewModel.pollItems.collect {
                    adapter.submitData(it)

                }
            }
        }
    }

    private fun selectedPoll(isLast: Boolean) {
        binding.twPollLatest.isSelected = isLast
        binding.twPollPopular.isSelected = !isLast
        binding.vwPollLatest.isVisible = isLast
        binding.vwPollPopular.isVisible = !isLast
        viewModel.getPollItems(categoryId, if (isLast) PollSort.Latest.type else PollSort.Popular.type)
    }
}