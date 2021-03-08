package com.zion830.threedollars.customview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
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
import com.zion830.threedollars.utils.*


open class NaverMapFragment : Fragment(R.layout.fragment_naver_map), OnMapReadyCallback {

    protected lateinit var binding: FragmentNaverMapBinding

    private var naverMap: NaverMap? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = arrayListOf<Marker>()

    protected var currentPosition: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_naver_map, container, false)
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as? MapFragment?
            ?: MapFragment.newInstance().also { childFragmentManager.beginTransaction().add(R.id.fragment_map, it).commit() }
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        initMapUiSetting(map)
    }

    private fun initMapUiSetting(map: NaverMap) {
        binding.btnFindLocation.setOnClickListener {
            requireActivity().requestPermissionIfNeeds()
            moveToCurrentLocation()
        }

        map.locationSource = FusedLocationSource(this, NaverMapUtils.LOCATION_PERMISSION_REQUEST_CODE)
        map.locationTrackingMode = LocationTrackingMode.Follow
        map.uiSettings.isZoomControlEnabled = false
    }

    protected fun addMarker(@DrawableRes drawableRes: Int, position: LatLng) {
        if (naverMap == null) {
            return
        }

        markers.add(Marker().apply {
            this.position = position
            this.icon = OverlayImage.fromResource(drawableRes)
            this.map = naverMap
        })
    }

    protected fun addMarkers(@DrawableRes drawableRes: Int, positions: List<LatLng>) {
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

    fun moveToCurrentLocation() {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    currentPosition = LatLng(it.latitude, it.longitude)
                    moveCameraWithAnim(currentPosition!!)
                    onMyLocationLoaded(currentPosition!!)
                }
            } else {
                showToast(R.string.find_location_error)
                moveCameraWithAnim(NaverMapUtils.DEFAULT_LOCATION)
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    protected fun moveCamera(position: LatLng) {
        if (naverMap == null) {
            return
        }

        val cameraUpdate = CameraUpdate.scrollTo(position)
        naverMap!!.moveCamera(cameraUpdate)
    }

    fun moveCameraWithAnim(position: LatLng) {
        if (naverMap == null) {
            return
        }

        val cameraUpdate = CameraUpdate.scrollTo(position).animate(CameraAnimation.Easing)
        naverMap!!.moveCamera(cameraUpdate)
    }

    open fun onMyLocationLoaded(position: LatLng) {
        // do nothing
    }
}