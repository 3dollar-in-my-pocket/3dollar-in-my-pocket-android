package com.zion830.threedollars.ui.store_detail

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreByMenuBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.StoreList
import com.zion830.threedollars.ui.store_detail.adapter.SearchByDistanceRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.adapter.SearchByRatingRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreByMenuViewModel
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

class StoreByMenuActivity : BaseActivity<ActivityStoreByMenuBinding, StoreByMenuViewModel>(R.layout.activity_store_by_menu) {

    override val viewModel: StoreByMenuViewModel by viewModels()

    private var currentPosition: LatLng = DEFAULT_LOCATION

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mapFragment: SupportMapFragment

    private var googleMap: GoogleMap? = null

    private var menuType: MenuType = MenuType.BUNGEOPPANG

    private val storeByDistanceAdapters = arrayListOf<SearchByDistanceRecyclerAdapter>()

    private val storeByRatingAdapters = arrayListOf<SearchByRatingRecyclerAdapter>()

    override fun initView() {
        initAdapter()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            initMap(map)
            binding.btnFindLocation.setOnClickListener {
                initMap(map, googleMap?.cameraPosition?.zoom ?: DEFAULT_ZOOM)
            }
            viewModel.nearStoreInfo.observe(this@StoreByMenuActivity) { storeInRange ->
                map.setMarkers(storeInRange.map { LatLng(it.latitude, it.longitude) })
            }
            viewModel.changeCategory(menuType, currentPosition)
        }

        menuType = MenuType.of(intent.getStringExtra(KEY_MENU))

        binding.btnMenu1.setOnClickListener {
            viewModel.changeCategory(MenuType.BUNGEOPPANG, currentPosition)
        }
        binding.btnMenu2.setOnClickListener {
            viewModel.changeCategory(MenuType.TAKOYAKI, currentPosition)
        }
        binding.btnMenu3.setOnClickListener {
            viewModel.changeCategory(MenuType.GYERANPPANG, currentPosition)
        }
        binding.btnMenu4.setOnClickListener {
            viewModel.changeCategory(MenuType.HOTTEOK, currentPosition)
        }
        binding.btnSortByDistance.setOnClickListener {
            viewModel.changeSortType(SortType.DISTANCE, currentPosition)
        }
        binding.btnSortByScore.setOnClickListener {
            viewModel.changeSortType(SortType.RATING, currentPosition)
        }
        viewModel.storeByRating.observe(this) {
            googleMap?.setMarkers(it.getAllStores().map { store -> LatLng(store.latitude, store.longitude) })
            storeByRatingAdapters[0].submitList(it.getStoresOver3())
            storeByRatingAdapters[1].submitList(it.storeList2)
            storeByRatingAdapters[2].submitList(it.storeList1)
            storeByRatingAdapters[3].submitList(it.storeList0)
        }
        viewModel.storeByDistance.observe(this) {
            googleMap?.setMarkers(it.getAllStores().map { store -> LatLng(store.latitude, store.longitude) })
            storeByDistanceAdapters[0].submitList(it.storeList50)
            storeByDistanceAdapters[1].submitList(it.storeList100)
            storeByDistanceAdapters[2].submitList(it.storeList500)
            storeByDistanceAdapters[3].submitList(it.storeList1000)
        }
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initAdapter() {
        val searchByDistanceListener = object : OnItemClickListener<StoreList> {
            override fun onClick(item: StoreList) {
                val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.id)
                startActivity(intent)
            }
        }
        val searchByRatingListener = object : OnItemClickListener<StoreList> {
            override fun onClick(item: StoreList) {
                val intent = StoreDetailActivity.getIntent(this@StoreByMenuActivity, item.id)
                startActivity(intent)
            }
        }
        storeByDistanceAdapters.apply {
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
            add(SearchByDistanceRecyclerAdapter(searchByDistanceListener))
        }
        storeByRatingAdapters.apply {
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
            add(SearchByRatingRecyclerAdapter(searchByRatingListener))
        }
        binding.rvDistance1.adapter = storeByDistanceAdapters[0]
        binding.rvDistance2.adapter = storeByDistanceAdapters[1]
        binding.rvDistance3.adapter = storeByDistanceAdapters[2]
        binding.rvDistance4.adapter = storeByDistanceAdapters[3]
        binding.rvRating1.adapter = storeByRatingAdapters[0]
        binding.rvRating2.adapter = storeByRatingAdapters[1]
        binding.rvRating3.adapter = storeByRatingAdapters[2]
        binding.rvRating4.adapter = storeByRatingAdapters[3]
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
                viewModel.requestStoreInfo(currentPosition)
                showToast(R.string.find_location_error)
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    private fun setLocationText() {
        binding.tvLocation.text = getCurrentLocationName(currentPosition)
        binding.tvLocation.visibility = if (binding.tvLocation.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun moveCameraToCurrentPosition(position: LatLng, zoomLevel: Float?) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel ?: DEFAULT_ZOOM))
    }

    companion object {
        private const val KEY_MENU = "KEY_MENU"

        fun getIntent(context: Context, menuType: String) = Intent(context, StoreByMenuActivity::class.java).apply {
            putExtra(KEY_MENU, menuType)
        }
    }
}