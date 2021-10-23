package com.zion830.threedollars.customview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNaverMapBinding
import com.zion830.threedollars.repository.model.v2.response.store.StoreInfo
import com.zion830.threedollars.utils.*


open class NaverMapFragment : Fragment(R.layout.fragment_naver_map), OnMapReadyCallback {

    var naverMap: NaverMap? = null

    var currentPosition: LatLng? = null

    protected lateinit var binding: FragmentNaverMapBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = arrayListOf<Marker>()

    private var listener: OnMapTouchListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = requireActivity() as? OnMapTouchListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_naver_map, container, false)
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as? MapFragment?
            ?: MapFragment.newInstance().also { childFragmentManager.beginTransaction().add(R.id.fragment_map, it).commit() }
        mapFragment.getMapAsync(this)

        val frameLayout = TouchableWrapper(requireActivity(), null, 0, listener)
        frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        (binding.root as? ViewGroup)?.addView(
            frameLayout,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )

        return binding.root
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        initMapUiSetting(map)
    }

    private fun initMapUiSetting(map: NaverMap) {
        binding.btnFindLocation.setOnClickListener {
            requireActivity().requestPermissionIfNeeds()
            moveToCurrentLocation(true)
        }

        map.locationSource = FusedLocationSource(this, NaverMapUtils.LOCATION_PERMISSION_REQUEST_CODE)
        map.locationTrackingMode = LocationTrackingMode.Follow
        map.uiSettings.isZoomControlEnabled = false
        map.uiSettings.isScaleBarEnabled = false
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

    fun addStoreMarkers(@DrawableRes drawableRes: Int, storeInfoList: List<StoreInfo>, onClick: (marker: StoreInfo) -> Unit = {}) {
        if (naverMap == null) {
            return
        }

        markers.forEach { it.map = null }
        markers.clear()

        val newMarkers = storeInfoList.map { storeInfo ->
            Marker().apply {
                this.position = LatLng(storeInfo.latitude, storeInfo.longitude)
                this.icon = OverlayImage.fromResource(drawableRes)
                this.map = naverMap
                setOnClickListener {
                    onClick(storeInfo)
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
            } else {
                showToast(R.string.find_location_error)
                moveCamera(NaverMapUtils.DEFAULT_LOCATION)
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
            showToast(R.string.find_location_error)
            moveCamera(NaverMapUtils.DEFAULT_LOCATION)
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