package com.zion830.threedollars.ui.home

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSnapHelper
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.repository.model.response.AllStoreResponseItem
import com.zion830.threedollars.ui.addstore.view.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.home.adapter.NearStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.getCurrentLocationName
import zion830.com.common.base.BaseFragment
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: NearStoreRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    override fun initView() {
        naverMapFragment = NearStoreNaverMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        viewModel.addressText.observe(viewLifecycleOwner) {
            binding.tvAddress.text = it ?: getString(R.string.location_no_address)
        }
        searchViewModel.searchResultLocation.observe(viewLifecycleOwner) {
            naverMapFragment.moveCameraWithAnim(it)
            binding.tvAddress.text = getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }
        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { store ->
            adapter.submitList(store)
        }
        binding.layoutAddress.setOnClickListener {
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment(),
                SearchAddressFragment::class.java.name
            )
        }

        adapter = NearStoreRecyclerAdapter(object : OnItemClickListener<AllStoreResponseItem> {
            override fun onClick(item: AllStoreResponseItem) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.id)
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
            }
        })

        binding.rvStore.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvStore)
        binding.rvStore.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (position >= 0) {
                            adapter.focusedIndex = position
                            adapter.notifyDataSetChanged()
                            naverMapFragment.moveCameraWithAnim(adapter.getItemLocation(position))
                        }
                    }
                })
        )
        binding.tvRetrySearch.setOnClickListener {
            naverMapFragment.currentPosition?.let { latLng -> viewModel.requestStoreInfo(latLng) }
        }
        binding.ibToss.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.toss_scheme)))
            startActivity(browserIntent)
            hackleApp.track(Constants.TOSS_BTN_CLICKED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        naverMapFragment.onActivityResult(requestCode, resultCode, data)
        naverMapFragment.currentPosition?.let { latLng -> viewModel.requestStoreInfo(latLng) }
    }
}