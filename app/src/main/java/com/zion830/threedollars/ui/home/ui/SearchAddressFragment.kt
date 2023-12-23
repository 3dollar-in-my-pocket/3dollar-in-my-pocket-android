package com.zion830.threedollars.ui.home.ui

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.network.data.kakao.Document
import com.zion830.threedollars.databinding.FragmentSearchByAddressBinding
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.home.viewModel.SearchAddressViewModel
import com.zion830.threedollars.ui.home.adapter.SearchAddressRecyclerAdapter
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchAddressFragment : BaseFragment<FragmentSearchByAddressBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: SearchAddressRecyclerAdapter

    override fun initView() {
        binding.etSearch.requestFocus()

        initAdapter()
        initButton()
        initFlow()
        searchViewModel.searchResult.observe(viewLifecycleOwner) {
            adapter.submitList(it?.documents ?: emptyList())
            binding.tvEmpty.isVisible = it?.documents.isNullOrEmpty()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                binding.btnSearch.performClick()
            }
            false
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "SearchAddressFragment", screenName = null)
    }

    private fun initAdapter() {
        adapter = SearchAddressRecyclerAdapter(object : OnItemClickListener<Document> {
            override fun onClick(item: Document) {
                val location = LatLng(item.y.toDouble(), item.x.toDouble())
                viewModel.requestHomeItem(location)
                searchViewModel.updateLatLng(location)
                activity?.supportFragmentManager?.popBackStack()
                searchViewModel.clear()
            }
        })
        binding.rvSearchResult.adapter = adapter
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnSearch.setOnClickListener {
            if (binding.etSearch.text.isNullOrBlank()) {
                showToast("검색어가 없습니다.")
            } else {
                searchViewModel.search(binding.etSearch.text.toString(), viewModel.currentLocation.value ?: NaverMapUtils.DEFAULT_LOCATION)
            }
        }
    }

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
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchByAddressBinding =
        FragmentSearchByAddressBinding.inflate(inflater, container, false)

    companion object {
        fun newInstance() = SearchAddressFragment()
    }
}