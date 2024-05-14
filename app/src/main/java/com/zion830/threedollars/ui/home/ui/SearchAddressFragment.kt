package com.zion830.threedollars.ui.home.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.home.domain.data.place.PlaceModel
import com.home.domain.request.PlaceRequest
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.kakao.Document
import com.zion830.threedollars.databinding.FragmentSearchByAddressBinding
import com.zion830.threedollars.ui.home.adapter.RecentSearchAdapter
import com.zion830.threedollars.ui.home.adapter.SearchAddressRecyclerAdapter
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchAddressFragment : BaseFragment<FragmentSearchByAddressBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var searchAddressRecyclerAdapter: SearchAddressRecyclerAdapter

    private lateinit var recentSearchAdapter: RecentSearchAdapter

    override fun initView() {
        binding.etSearch.requestFocus()
        searchViewModel.getPlace()

        initAdapter()
        initButton()
        initFlow()
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.recentSearchLinearLayout.isVisible = true
                    binding.searchResultLinearLayout.isVisible = false
                } else {
                    binding.recentSearchLinearLayout.isVisible = false
                    binding.searchResultLinearLayout.isVisible = true
                    searchViewModel.updateSearchAddress(binding.etSearch.text.toString())
                }
            }

            override fun afterTextChanged(e: Editable?) {}
        })
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SearchAddressFragment", screenName = null)
    }

    private fun initAdapter() {
        searchAddressRecyclerAdapter = SearchAddressRecyclerAdapter(object : OnItemClickListener<Document> {
            override fun onClick(item: Document) {
                val location = LatLng(item.y.toDouble(), item.x.toDouble())
                searchViewModel.postPlace(
                    placeRequest = PlaceRequest(
                        location = PlaceRequest.Location(latitude = location.latitude, longitude = location.longitude),
                        placeName = item.placeName,
                        addressName = item.addressName,
                        roadAddressName = item.roadAddressName
                    )
                )
                viewModel.requestHomeItem(location)
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        })
        binding.rvSearchResult.adapter = searchAddressRecyclerAdapter

        recentSearchAdapter = RecentSearchAdapter(searchClickListener = object : OnItemClickListener<PlaceModel> {
            override fun onClick(item: PlaceModel) {
                val location = LatLng(item.location.latitude, item.location.longitude)
                searchViewModel.postPlace(
                    placeRequest = PlaceRequest(
                        location = PlaceRequest.Location(latitude = location.latitude, longitude = location.longitude),
                        placeName = item.placeName,
                        addressName = item.addressName,
                        roadAddressName = item.roadAddressName
                    )
                )
                viewModel.requestHomeItem(location)
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        },
            deleteClickListener = object : OnItemClickListener<PlaceModel> {
                override fun onClick(item: PlaceModel) {
                    searchViewModel.deletePlace(item.placeId)
                }

            }
        )

        binding.recentSearchRecyclerView.adapter = recentSearchAdapter
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @OptIn(FlowPreview::class)
    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
                launch {
                    searchViewModel.searchResult.collect {
                        searchAddressRecyclerAdapter.submitList(it?.documents ?: emptyList())
                        binding.tvEmpty.isVisible = it?.documents.isNullOrEmpty()
                    }
                }
                launch {
                    searchViewModel.searchAddress.debounce(1000L).collect {
                        searchViewModel.search(it)
                    }
                }
                launch {
                    searchViewModel.recentSearchPagingData.collectLatest {
                        it?.let { pagingData ->
                            recentSearchAdapter.submitData(PagingData.empty())
                            recentSearchAdapter.submitData(pagingData)
                            binding.recentSearchRecyclerView.postDelayed({
                                binding.recentSearchRecyclerView.smoothScrollToPosition(0)
                            }, 500)
                        }
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchByAddressBinding =
        FragmentSearchByAddressBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = SearchAddressFragment()
    }
}