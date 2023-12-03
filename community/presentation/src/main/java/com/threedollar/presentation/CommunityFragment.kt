package com.threedollar.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.domain.data.Neighborhoods
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.databinding.FragmentCommunityBinding
import com.threedollar.presentation.dialog.NeighborHoodsChoiceDialog
import com.threedollar.presentation.poll.PollDetailActivity
import com.threedollar.presentation.polls.PollListActivity
import com.threedollar.presentation.utils.selectedPoll

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import javax.inject.Inject

@AndroidEntryPoint
class CommunityFragment : Fragment(R.layout.fragment_community) {
    private lateinit var binding: FragmentCommunityBinding
    private val viewModel: CommunityViewModel by viewModels()

    @Inject
    lateinit var activityStarter: ActivityStarter
    private val pollAdapter by lazy {
        CommunityPollAdapter(choicePoll = { pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
        }, clickPoll = {
            startActivity(Intent(requireActivity(), PollDetailActivity::class.java).apply {
                putExtra("id", it.poll.pollId)
            })
        })
    }
    private val storeAdapter by lazy {
        CommunityStoreAdapter {
            activityStarter.startStoreDetailActivity(requireContext(), it.storeId.toIntOrNull())
        }
    }
    private var choiceNeighborhood: Neighborhoods.Neighborhood.District? = null
    private var seoulNeighborhoods: Neighborhoods.Neighborhood? = null
    private val pollItems = mutableListOf<PollItem>()
    private var categoryId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCommunityBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedPopular(true)
        binding.recyclerPoll.adapter = pollAdapter
        binding.recyclerPopularStore.adapter = storeAdapter
        binding.twAreaChoice.onSingleClick {
            choiceNeighborhood?.let { neighborhood ->
                seoulNeighborhoods?.let {
                    NeighborHoodsChoiceDialog().setNeighborHoods(it).setChoiceNeighborhood(neighborhood).setItemClick {
                        binding.twAreaChoice.text = it.description
                        choiceNeighborhood = it
                        binding.twAreaChoice.text = it.description
                        viewModel.getPopularStores(
                            if (binding.twPopularMostReview.isSelected) PopularStoreCriteria.MostReview.type else PopularStoreCriteria.MostVisits.type,
                            it.district
                        )
                    }.show(childFragmentManager, "")
                }

            }
        }
        binding.clPopularMostReview.onSingleClick {
            if (!binding.twPopularMostReview.isSelected) selectedPopular(true)
        }
        binding.clPopularMostVisits.onSingleClick {
            if (!binding.twPopularMostVisits.isSelected) selectedPopular(false)
        }
        binding.twPollListTitle.onSingleClick {
            if (categoryId.isNotEmpty()) {
                startActivity(Intent(requireContext(), PollListActivity::class.java).apply {
                    putExtra("category", categoryId)
                })
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.categoryList.collect {
                        if (it.isEmpty()) return@collect
                        val category = it.first()
                        categoryId = category.categoryId
                        viewModel.getPollItems(category.categoryId)
                        binding.twPollTitle.text = category.content
                    }
                }
                launch {
                    viewModel.pollItems.collect {
                        if (it.isEmpty()) return@collect
                        pollItems.addAll(it.toList())
                        pollAdapter.submitList(pollItems)
                    }
                }
                launch {
                    viewModel.pollSelected.collect {
                        val pollId = it.first
                        val optionId = it.second
                        val selectPoll = pollItems.find { it.poll.pollId == pollId } ?: return@collect
                        pollItems[pollItems.indexOfFirst { it.poll.pollId == pollId }] = selectedPoll(selectPoll, optionId)
                        pollAdapter.submitList(pollItems)
                        pollAdapter.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.popularStores.collect {
                        storeAdapter.submitList(it.toList())
                    }
                }
                launch {
                    viewModel.neighborhoods.collect {
                        if (it.neighborhoods.isEmpty()) return@collect
                        val seoul = it.neighborhoods.find { it.province == "SEOUL" }
                        seoul?.let {
                            seoulNeighborhoods = it
                            choiceNeighborhood = it.districts.first()
                            binding.twAreaChoice.text = it.districts.first().description
                            viewModel.getPopularStores(PopularStoreCriteria.MostReview.type, it.districts.first().district)
                        }
                    }
                }
                launch {
                    viewModel.toast.collect {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun selectedPopular(isReview: Boolean) {
        binding.twPopularMostReview.isSelected = isReview
        binding.twPopularMostVisits.isSelected = !isReview
        binding.vwPopularMostReview.isVisible = isReview
        binding.vwPopularMostVisits.isVisible = !isReview
        choiceNeighborhood?.let {
            viewModel.getPopularStores(if (isReview) PopularStoreCriteria.MostReview.type else PopularStoreCriteria.MostVisits.type, it.district)
        }
    }

}