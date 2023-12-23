package com.threedollar.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.EventTrackerListener
import com.threedollar.common.utils.Constants
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
class CommunityFragment : BaseFragment<FragmentCommunityBinding, CommunityViewModel>() {
    override val viewModel: CommunityViewModel by viewModels()

    @Inject
    lateinit var activityStarter: ActivityStarter

    @Inject
    lateinit var eventTrackerListener: EventTrackerListener
    private val pollAdapter: CommunityPollAdapter by lazy {
        CommunityPollAdapter(choicePoll = { pollId, optionId ->
            viewModel.votePoll(pollId, optionId)
            val bundle = Bundle().apply {
                putString("screen", "community")
                putString("poll_id", pollId)
                putString("option_id", optionId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL_OPTION, bundle)
        }, clickPoll = {
            registerPollDetail.launch(Intent(requireActivity(), PollDetailActivity::class.java).apply {
                putExtra("id", it.poll.pollId)
            })
            val bundle = Bundle().apply {
                putString("screen", "community")
                putString("poll_id", it.poll.pollId)
            }
            eventTrackerListener.logEvent(Constants.CLICK_POLL, bundle)
        })
    }
    private val storeAdapter by lazy {
        CommunityStoreAdapter {
            if (it.storeType == "BOSS_STORE") {
                activityStarter.startBossDetailActivity(requireContext(), it.storeId)
            } else {
                activityStarter.startStoreDetailActivity(requireContext(), it.storeId.toIntOrNull())
            }
            val bundle = Bundle().apply {
                putString("screen", "community")
                putString("store_id", it.storeId)
                putString("type", it.storeType)
            }
            eventTrackerListener.logEvent(Constants.CLICK_STORE, bundle)
        }
    }
    private var choiceNeighborhood: Neighborhoods.Neighborhood.District? = null
    private var seoulNeighborhoods: Neighborhoods.Neighborhood? = null
    private val pollItems = mutableListOf<PollItem>()
    private var categoryId = ""
    private val registerPollDetail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            it.data?.let { intent ->
                val pollItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra("pollItem", PollItem::class.java)
                else intent.getParcelableExtra("pollItem")
                if (pollItem == null) return@registerForActivityResult
                val index = pollItems.indexOfFirst { item -> item.poll.pollId == pollItem.poll.pollId }
                if (index > -1) {
                    pollItems[index] = pollItem
                    pollAdapter.submitList(pollItems.toList())
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCommunityBinding =
        FragmentCommunityBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "CommunityFragment", screenName = "community")
    }

    override fun initView() {
        selectedPopular(true)
        initAdapter()
        initButton()
        initFlow()
    }

    private fun initAdapter() {
        binding.recyclerPoll.adapter = pollAdapter
        binding.recyclerPopularStore.adapter = storeAdapter
    }

    private fun initButton() {
        binding.twAreaChoice.onSingleClick {
            choiceNeighborhood?.let { neighborhood ->
                seoulNeighborhoods?.let { seoulNeighborhoods ->
                    NeighborHoodsChoiceDialog().setNeighborHoods(seoulNeighborhoods).setChoiceNeighborhood(neighborhood).setItemClick {
                        binding.twAreaChoice.text = it.description
                        choiceNeighborhood = it
                        binding.twAreaChoice.text = it.description
                        viewModel.getPopularStores(
                            if (binding.twPopularMostReview.isSelected) PopularStoreCriteria.MostReview.type else PopularStoreCriteria.MostVisits.type,
                            it.district
                        )
                    }.show(childFragmentManager, "")
                    val bundle = Bundle().apply {
                        putString("screen", "community")
                    }
                    eventTrackerListener.logEvent(Constants.CLICK_DISTRICT, bundle)
                }

            }
        }
        binding.clPopularMostReview.onSingleClick {
            if (!binding.twPopularMostReview.isSelected) {
                selectedPopular(true)
                val bundle = Bundle().apply {
                    putString("screen", "community")
                    putString("value", "MOST_REVIEWS")
                }
                eventTrackerListener.logEvent(Constants.CLICK_POPULAR_FILTER, bundle)
            }
        }
        binding.clPopularMostVisits.onSingleClick {
            if (!binding.twPopularMostVisits.isSelected) {
                selectedPopular(false)
                val bundle = Bundle().apply {
                    putString("screen", "community")
                    putString("value", "MOST_VISITS")
                }
                eventTrackerListener.logEvent(Constants.CLICK_POPULAR_FILTER, bundle)
            }
        }
        binding.twPollListTitle.onSingleClick {
            if (categoryId.isNotEmpty()) {
                startActivity(Intent(requireContext(), PollListActivity::class.java).apply {
                    putExtra("category", categoryId)
                })
            }
        }
    }

    private fun initFlow() {
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