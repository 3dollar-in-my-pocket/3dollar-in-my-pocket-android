package com.threedollar.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.databinding.FragmentCommunityBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class CommunityFragment : Fragment(R.layout.fragment_community) {
    private lateinit var binding: FragmentCommunityBinding
    private val viewModel: CommunityViewModel by viewModels()
    private val pollAdapter by lazy {
        CommunityPollAdapter(choicePoll = { pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
        }, clickPoll = {})
    }
    private val pollItems = mutableListOf<PollItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCommunityBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPollCategories()

        binding.viewPagerPoll.setPageTransformer { page, position ->
            val v = abs(position)
            page.scaleX = 0.8f + 0.2f * v
            page.scaleY = 0.8f + 0.2f * v
            page.alpha = 0.5f + (1 - v)
        }
        binding.viewPagerPoll.adapter = pollAdapter


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.categoryList.collect {
                        val category = it.first()
                        viewModel.getPollItems(category.categoryId)
                        binding.twPollTitle.text = category.title
                    }
                }
                launch {
                    viewModel.neighborhoods.collect {

                    }
                }
                launch {
                    viewModel.pollItems.collect {
                        pollItems.addAll(it.toList().subList(0, 4))
                        pollAdapter.submitList(pollItems)
                    }
                }
                launch {
                    viewModel.pollSelected.collect {
                        val pollId = it.first
                        val optionId = it.second
                        val selectPoll = pollItems.find { it.poll.pollId == pollId } ?: return@collect
                        var firstChoice = selectPoll.poll.options[0]
                        var secondChoice = selectPoll.poll.options[1]
                        firstChoice = firstChoice.copy(choice = firstChoice.choice.copy(selectedByMe = firstChoice.optionId == optionId))
                        secondChoice = secondChoice.copy(choice = secondChoice.choice.copy(selectedByMe = secondChoice.optionId == optionId))
                        pollItems[pollItems.indexOfFirst { it.poll.pollId == pollId }] =
                            selectPoll.copy(poll = selectPoll.poll.copy(options = listOf(firstChoice, secondChoice)))
                        pollAdapter.submitList(pollItems)
                    }
                }
                launch {
                    viewModel.popularStores.collect {
                        it.toList().subList(0, 9)
                    }
                }
            }
        }

    }

}