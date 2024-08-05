package com.threedollar.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdRequest
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.ActivityStarter
import com.threedollar.common.listener.EventTrackerListener
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_AD_CARD
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.data.AdvertisementModelV2
import com.threedollar.domain.data.PollItem
import com.threedollar.presentation.data.PollListData
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
    lateinit var sharedPrefUtils: SharedPrefUtils

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
        }, object : OnItemClickListener<AdvertisementModelV2> {
            override fun onClick(item: AdvertisementModelV2) {
                val bundle = Bundle().apply {
                    putString("screen", "community")
                    putString("advertisement_id", item.advertisementId.toString())
                }
                eventTrackerListener.logEvent(CLICK_AD_CARD, bundle)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link.url)))
            }
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

    private var advertisementModelV2: AdvertisementModelV2? = null
    private val pollItems = mutableListOf<PollListData>()
    private var categoryId = ""
    private val registerPollDetail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            it.data?.let { intent ->
                val pollItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) intent.getParcelableExtra("pollItem", PollItem::class.java)
                else intent.getParcelableExtra("pollItem")
                if (pollItem == null) return@registerForActivityResult
                val index = pollItems.indexOfFirst { pollListData -> pollListData.isSelectPoll(pollItem.poll.pollId) }
                if (index > -1) {
                    pollItems[index] = PollListData.Poll(pollItem)
                    pollAdapter.submitList(pollItems.toList())
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCommunityBinding.inflate(inflater)
        return binding.root
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
        initAdmob()
    }

    private fun initAdapter() {
        binding.recyclerPoll.adapter = pollAdapter
        binding.recyclerPopularStore.adapter = storeAdapter
    }

    private fun initAdmob() {
        val adRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)
    }

    private fun initButton() {
        binding.selectNeighborhoodTextView.onSingleClick {
            NeighborHoodsChoiceDialog()
                .setNeighborhoodModels(viewModel.neighborhoods.value)
                .setItemClick {
                    binding.selectNeighborhoodTextView.text = getString(R.string.select_neighborhood_default, it.description)
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
                val bundle = Bundle().apply {
                    putString("screen", "community")
                }
                eventTrackerListener.logEvent(Constants.CLICK_POLL_CATEGORY, bundle)
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
                        pollItems.addAll(it.toList().map { poll -> PollListData.Poll(poll) })
                        advertisementModelV2?.let { advertisement ->
                            if (pollItems.size > 3) pollItems.add(2, PollListData.Ad(advertisement))
                            else pollItems.add(PollListData.Ad(advertisement))
                        }
                        pollAdapter.submitList(pollItems)
                    }
                }
                launch {
                    viewModel.pollSelected.collect {
                        val pollId = it.first
                        val optionId = it.second
                        val selectPoll = pollItems.find { pollListData -> pollListData.isSelectPoll(pollId) } as? PollListData.Poll ?: return@collect
                        pollItems[pollItems.indexOfFirst { pollListData -> pollListData.isSelectPoll(pollId) }] =
                            PollListData.Poll(selectedPoll(selectPoll.pollItem, optionId))
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
                        if (it.isEmpty()) return@collect

                        if (sharedPrefUtils.getSelectNeighborhoodDescription().isEmpty() || sharedPrefUtils.getSelectNeighborhoodDistrict()
                                .isEmpty()
                        ) {
                            val selectNeighborhood = it.first()
                            sharedPrefUtils.saveSelectNeighborhoodDescription(selectNeighborhood.districts.first().description)
                            sharedPrefUtils.saveSelectNeighborhoodDistrict(selectNeighborhood.districts.first().district)
                        }
                        binding.selectNeighborhoodTextView.text =
                            getString(R.string.select_neighborhood_default, sharedPrefUtils.getSelectNeighborhoodDescription())
                        viewModel.getPopularStores(PopularStoreCriteria.MostReview.type, sharedPrefUtils.getSelectNeighborhoodDistrict())
                    }
                }
                launch {
                    viewModel.toast.collect {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
                launch {
                    viewModel.advertisements.collect {
                        advertisementModelV2 = it.firstOrNull()
                    }
                }
            }
        }
    }

    private fun PollListData.isSelectPoll(pollId: String) = if (this is PollListData.Poll) this.pollItem.poll.pollId == pollId else false
    private fun selectedPopular(isReview: Boolean) {
        binding.twPopularMostReview.isSelected = isReview
        binding.twPopularMostVisits.isSelected = !isReview
        binding.vwPopularMostReview.isVisible = isReview
        binding.vwPopularMostVisits.isVisible = !isReview
        viewModel.getPopularStores(
            criteria = if (isReview) PopularStoreCriteria.MostReview.type else PopularStoreCriteria.MostVisits.type,
            district = sharedPrefUtils.getSelectNeighborhoodDistrict()
        )
    }

}