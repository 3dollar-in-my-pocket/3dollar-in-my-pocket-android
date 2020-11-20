package com.zion830.threedollars.ui.home

import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.utils.*
import zion830.com.common.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mapFragment: SupportMapFragment

    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            initMap(it)
        }
    }

    private fun initMap(googleMap: GoogleMap) {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    setLocation(googleMap, it.toLatLng())
                    binding.tvMyLocation.text = getCurrentLocationName(it.toLatLng())
                }
                googleMap.isMyLocationEnabled = true
                return
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
        setLocation(googleMap)
        setLocationText(DEFAULT_LOCATION)
    }

    private fun setLocationText(location: LatLng?) {
        if (location == null) {
            binding.tvMyLocation.visibility = View.INVISIBLE
        } else {
            binding.tvMyLocation.visibility = View.VISIBLE
            binding.tvMyLocation.text = getCurrentLocationName(location)
        }
    }

    private fun setLocation(googleMap: GoogleMap, position: LatLng = DEFAULT_LOCATION) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
    }
}