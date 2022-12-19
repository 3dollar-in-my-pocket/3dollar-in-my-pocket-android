package com.zion830.threedollars.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNaverMapBinding
import com.zion830.threedollars.datasource.model.v2.response.AdAndStoreItem
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
open class NaverMapFragment : Fragment(R.layout.fragment_naver_map), OnMapReadyCallback {
    var naverMap: NaverMap? = null

    var currentPosition: LatLng? = null

    protected lateinit var binding: FragmentNaverMapBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = arrayListOf<Marker>()

    var listener: OnMapTouchListener? = null

    fun setOnMapTouchListener(mapListener: OnMapTouchListener) {
        listener = mapListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_naver_map, container, false)
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as? MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.fragment_map, it).commit()
            }
        mapFragment.getMapAsync(this)

        val frameLayout = TouchableWrapper(requireActivity(), null, 0, listener)
        frameLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        (binding.root as? ViewGroup)?.addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        return binding.root
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        initMapUiSetting(map)
    }

    private fun initMapUiSetting(map: NaverMap) {
        binding.btnFindLocation.setOnClickListener {
            FirebaseAnalytics.getInstance(requireContext())
                .logEvent("CURRENT_LOCATION_BTN_CLICKED") {}
            requireActivity().requestPermissionIfNeeds()
            moveToCurrentLocation(false)
        }

        map.locationSource =
            FusedLocationSource(this, NaverMapUtils.LOCATION_PERMISSION_REQUEST_CODE)
        map.locationTrackingMode = LocationTrackingMode.Follow
        map.uiSettings.isZoomControlEnabled = false
        map.uiSettings.isScaleBarEnabled = false
        map.addOnLocationChangeListener {
            map.locationOverlay.bearing = 0f
        }
        val storeMarker = GlobalApplication.storeMarker
        if (storeMarker.imageUrl.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.Main) {
                map.locationOverlay.icon = OverlayImage.fromBitmap(withContext(Dispatchers.IO) {
                    storeMarker.imageUrl.urlToBitmap().get()
                })
            }
            map.locationOverlay.setOnClickListener {
                val dialog = MarkerClickDialog()
                dialog.show(parentFragmentManager, dialog.tag)
                return@setOnClickListener false
            }
        }
    }

    fun addMarker(@DrawableRes drawableRes: Int, position: LatLng) {
        if (naverMap == null) {
            return
        }

        markers.add(Marker().apply {
            this.position = position
            this.icon = OverlayImage.fromResource(drawableRes)
            this.map = naverMap
        })
    }

    fun removeAllMarker() {
        markers.forEach {
            it.map = null
        }
        markers.clear()
    }

    fun updateMarkerIcon(@DrawableRes drawableRes: Int, position: Int) {
        if (markers.size > position) {
            markers[position].icon = OverlayImage.fromResource(drawableRes)
            markers[position].map = naverMap
        }
    }

    fun addStoreMarkers(
        @DrawableRes drawableRes: Int,
        list: List<AdAndStoreItem>,
        onClick: (marker: AdAndStoreItem) -> Unit = {}
    ) {
        if (naverMap == null) {
            return
        }

        markers.forEach { it.map = null }
        markers.clear()

        val newMarkers = list.map { item ->
            Marker().apply {

                this.position = if (item is StoreInfo) {
                    LatLng(item.latitude, item.longitude)
                } else {
                    val location = (item as BossNearStoreResponse.BossNearStoreModel).location
                    LatLng(location.latitude, location.longitude)
                }
                this.icon = if (item is StoreInfo) {
                    OverlayImage.fromResource(drawableRes)
                } else {
                    if ((item as BossNearStoreResponse.BossNearStoreModel).openStatus?.status == "CLOSED") {
                        OverlayImage.fromResource(R.drawable.ic_food_truck_off)
                    } else {
                        OverlayImage.fromResource(drawableRes)
                    }
                }
                this.map = naverMap
                setOnClickListener {
                    onClick(item)
                    true
                }
            }
        }
        markers.addAll(newMarkers)
    }

    fun addMarkers(@DrawableRes drawableRes: Int, positions: List<LatLng>) {
        if (naverMap == null) {
            return
        }

        markers.forEach { it.map = null }
        markers.clear()

        val newMarkers = positions.map { position ->
            Marker().apply {
                this.position = position
                this.icon = OverlayImage.fromResource(drawableRes)
                this.map = naverMap
            }
        }
        markers.addAll(newMarkers)
    }

    @SuppressLint("MissingPermission")
    fun moveToCurrentLocation(showAnim: Boolean = false) {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        currentPosition = LatLng(it.latitude, it.longitude)
                        currentPosition?.let { position ->
                            if (showAnim) {
                                moveCameraWithAnim(position)
                            } else {
                                moveCamera(position)
                            }
                            onMyLocationLoaded(position)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
            moveCamera(NaverMapUtils.DEFAULT_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    fun updateMyLatestLocation(onMyLocationLoaded: (LatLng?) -> Unit) {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        currentPosition = LatLng(it.latitude, it.longitude)
                        onMyLocationLoaded(currentPosition)
                    } else {
                        onMyLocationLoaded(null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    fun moveCamera(position: LatLng) {
        if (naverMap == null) {
            return
        }

        val cameraUpdate = CameraUpdate.scrollTo(position)
        naverMap?.moveCamera(cameraUpdate)
    }

    fun moveCameraWithAnim(position: LatLng) {
        if (naverMap == null) {
            return
        }

        val cameraUpdate = CameraUpdate.scrollTo(position).animate(CameraAnimation.Easing)
        naverMap?.moveCamera(cameraUpdate)
    }

    open fun onMyLocationLoaded(position: LatLng) {
        // do nothing
    }

    fun getMapCenterLatLng() = naverMap?.cameraPosition?.target ?: NaverMapUtils.DEFAULT_LOCATION
}