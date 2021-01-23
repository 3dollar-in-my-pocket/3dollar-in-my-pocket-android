package com.zion830.threedollars.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSnapHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.StyleSpan
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.AllStoreResponseItem
import com.zion830.threedollars.ui.home.adapter.NearStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreByMenuActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModels()

    private var currentPosition: LatLng = DEFAULT_LOCATION

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var adapter: NearStoreRecyclerAdapter

    private var googleMap: GoogleMap? = null

    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            initMap(map)
            binding.btnFindLocation.setOnClickListener {
                requireActivity().requestPermissionIfNeeds()
                initMap(map, googleMap?.cameraPosition?.zoom ?: DEFAULT_ZOOM)
            }
            viewModel.nearStoreInfo.observe(this@HomeFragment) { store ->
                val storeInRange = store.filter { it.distance <= 2000 }
                map.setMarkers(storeInRange.map { LatLng(it.latitude, it.longitude) })
                adapter.submitList(storeInRange)
            }
        }

        // 주변 음식점 리스트
        adapter = NearStoreRecyclerAdapter(object : OnItemClickListener<AllStoreResponseItem> {
            override fun onClick(item: AllStoreResponseItem) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.id)
                startActivity(intent)
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
                            moveCameraToCurrentPosition(adapter.getItemLocation(position), googleMap?.cameraPosition?.zoom)
                        }
                    }
                })
        )

        // 3천원 글자 두껍게 바꾸기
        val span = binding.tvMsg2.text as Spannable
        span.setSpan(StyleSpan(Typeface.BOLD), 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvMsg2.text = span

        // 상단 버튼바
        binding.btnMenu1.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.BUNGEOPPANG.key))
        }
        binding.btnMenu2.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.TAKOYAKI.key))
        }
        binding.btnMenu3.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.GYERANPPANG.key))
        }
        binding.btnMenu4.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.HOTTEOK.key))
        }
    }

    private fun initMap(googleMap: GoogleMap, zoomLevel: Float = DEFAULT_ZOOM) {
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    currentPosition = it.toLatLng()
                    moveCameraToCurrentPosition(it.toLatLng(), zoomLevel)
                    setLocationText()
                    viewModel.requestStoreInfo(currentPosition)
                }
                googleMap.isMyLocationEnabled = true
            } else {
                setLocationText()
                showToast(R.string.find_location_error)
                viewModel.requestStoreInfo(currentPosition)
                moveCameraToCurrentPosition(currentPosition, zoomLevel)
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    private fun setLocationText() {
        binding.tvMyLocation.text = getCurrentLocationName(currentPosition)
        binding.tvMyLocation.visibility = if (binding.tvMyLocation.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun moveCameraToCurrentPosition(position: LatLng, zoomLevel: Float?) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MainActivity.ADD_STORE -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.requestStoreInfo(currentPosition)
                }
            }
        }
    }
}