package com.zion830.threedollars.ui.home.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.google.android.gms.ads.AdRequest
import com.naver.maps.geometry.LatLng
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.domain.home.data.place.PlaceModel
import com.threedollar.domain.home.request.PlaceRequest
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
import zion830.com.common.base.onSingleClick

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
        initAdmob()
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                binding.recentSearchLinearLayout.isVisible = true
                binding.searchResultLinearLayout.isVisible = false
            } else {
                binding.recentSearchLinearLayout.isVisible = false
                binding.searchResultLinearLayout.isVisible = true
                searchViewModel.updateSearchAddress(binding.etSearch.text.toString())
            }
        }
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.SEARCH_ADDRESS, this::class.java.simpleName)
    }

    private fun initAdapter() {
        searchAddressRecyclerAdapter = SearchAddressRecyclerAdapter(object : OnItemClickListener<Document> {
            override fun onClick(item: Document) {
                searchViewModel.sendClickAddress("SEARCH")

                val location = LatLng(item.y.toDouble(), item.x.toDouble())
                searchViewModel.postPlace(
                    placeRequest = PlaceRequest(
                        location = PlaceRequest.Location(latitude = location.latitude, longitude = location.longitude),
                        placeName = item.placeName,
                        addressName = item.addressName,
                        roadAddressName = item.roadAddressName
                    )
                )
                viewModel.fetchAroundStores()
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        })
        binding.rvSearchResult.adapter = searchAddressRecyclerAdapter

        recentSearchAdapter = RecentSearchAdapter(searchClickListener = object : OnItemClickListener<PlaceModel> {
            override fun onClick(item: PlaceModel) {
                searchViewModel.sendClickAddress("RECENT")

                val location = LatLng(item.location.latitude, item.location.longitude)
                searchViewModel.postPlace(
                    placeRequest = PlaceRequest(
                        location = PlaceRequest.Location(latitude = location.latitude, longitude = location.longitude),
                        placeName = item.placeName,
                        addressName = item.addressName,
                        roadAddressName = item.roadAddressName
                    )
                )
                viewModel.fetchAroundStores()
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
        binding.btnBack.onSingleClick {
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

    private fun initAdmob() {
        val adRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchByAddressBinding =
        FragmentSearchByAddressBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = SearchAddressFragment()
    }
}