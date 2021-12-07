package com.zion830.threedollars.ui.home

import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSnapHelper
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.ui.addstore.view.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.home.adapter.NearStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.showToast
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private val storeDetailViewModel: StoreDetailViewModel by viewModels()

    private lateinit var adapter: NearStoreRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    fun getMapCenterLatLng() = try {
        naverMapFragment.getMapCenterLatLng()
    } catch (e: Exception) {
        null
    }

    override fun initView() {
        naverMapFragment = NearStoreNaverMapFragment {
            binding.tvRetrySearch.isVisible = true
        }
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        viewModel.addressText.observe(viewLifecycleOwner) {
            binding.tvAddress.text = it ?: getString(R.string.location_no_address)
        }
        searchViewModel.searchResultLocation.observe(viewLifecycleOwner) {
            naverMapFragment.moveCamera(it)
            binding.tvAddress.text =
                getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }
        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { store ->
            binding.layoutEmpty.isVisible = store.isNullOrEmpty()
            adapter.submitList(store)
        }
        binding.layoutAddress.setOnClickListener {
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment(),
                SearchAddressFragment::class.java.name
            )
        }

        adapter = NearStoreRecyclerAdapter(object : OnItemClickListener<StoreInfo?> {
            override fun onClick(item: StoreInfo?) {
                if (item == null) {
                    return
                }
                storeDetailViewModel.requestStoreInfo(item.storeId, item.latitude, item.longitude)
            }
        }) {

        }
        storeDetailViewModel.isExistStoreInfo.observe(viewLifecycleOwner) { isExistStore ->
            val storeId = isExistStore.first
            val isExist = isExistStore.second
            if (isExist) {
                val intent = StoreDetailActivity.getIntent(requireContext(), storeId)
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
            } else {
                showToast(R.string.exist_store_error)
            }
        }
        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { res ->
            naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, res ?: listOf()) {
                onStoreClicked(it)
            }
        }

        binding.rvStore.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvStore)
        binding.rvStore.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (position >= 0) {
                            naverMapFragment.updateMarkerIcon(
                                R.drawable.ic_store_off,
                                adapter.focusedIndex
                            )
                            adapter.focusedIndex = position
                            naverMapFragment.updateMarkerIcon(
                                R.drawable.ic_marker,
                                adapter.focusedIndex
                            )
                            adapter.notifyDataSetChanged()
                            naverMapFragment.moveCameraWithAnim(adapter.getItemLocation(position))
                        }
                    }
                })
        )
        binding.tvRetrySearch.setOnClickListener {
            viewModel.requestStoreInfo(naverMapFragment.getMapCenterLatLng())
            binding.tvRetrySearch.isVisible = false
        }
        binding.ibToss.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.toss_scheme)))
            startActivity(browserIntent)
            hackleApp.track(Constants.TOSS_BTN_CLICKED)
        }
    }

    private fun onStoreClicked(storeInfo: StoreInfo) {
        val position = adapter.getItemPosition(storeInfo)
        if (position >= 0) {
            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
            adapter.focusedIndex = position
            naverMapFragment.updateMarkerIcon(R.drawable.ic_marker, adapter.focusedIndex)
            naverMapFragment.moveCameraWithAnim(LatLng(storeInfo.latitude, storeInfo.longitude))

            adapter.notifyDataSetChanged()
            binding.rvStore.scrollToPosition(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            naverMapFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        naverMapFragment.getMapCenterLatLng().let { viewModel.requestStoreInfo(it) }
    }
}