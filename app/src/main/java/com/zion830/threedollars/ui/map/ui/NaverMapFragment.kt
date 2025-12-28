package com.zion830.threedollars.ui.map.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.home.domain.data.store.ContentModel
import com.home.domain.data.store.MarkerModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNaverMapBinding
import com.zion830.threedollars.ui.dialog.MarkerClickDialog
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.TouchableWrapper
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.requestPermissionIfNeeds
import com.zion830.threedollars.utils.urlToBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
open class NaverMapFragment : Fragment(R.layout.fragment_naver_map), OnMapReadyCallback {
    var naverMap: NaverMap? = null

    var currentPosition: MutableLiveData<LatLng> = MutableLiveData()
    var mapPosition: MutableLiveData<LatLng> = MutableLiveData()
    var mapViewPortDistance: MutableLiveData<Double> = MutableLiveData(DEFAULT_DISTANCE_M)

    protected lateinit var binding: FragmentNaverMapBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = arrayListOf<Marker>()

    var listener: OnMapTouchListener? = null

    private var isShowOverlay = true

    var onAdMarkerClicked: ((Int) -> Unit)? = null

    fun setOnMapTouchListener(mapListener: OnMapTouchListener) {
        listener = mapListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding = FragmentNaverMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as? MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.fragment_map, it).commit()
            }
        mapFragment.getMapAsync(this)

        val frameLayout = TouchableWrapper(requireActivity(), null, 0, listener)
        frameLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent,
            ),
        )
        (binding.root as? ViewGroup)?.addView(
            frameLayout,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        return binding.root
    }

    override fun onMapReady(map: NaverMap) {
        this.naverMap = map
        initMapUiSetting(map)
    }

    private fun initMapUiSetting(map: NaverMap) {
        binding.btnFindLocation.setOnClickListener {
            FirebaseAnalytics.getInstance(requireContext()).logEvent("click_current_location") {}
            requireActivity().requestPermissionIfNeeds()
            moveToCurrentLocation(false)
        }

        map.locationSource = if (isShowOverlay) FusedLocationSource(this, NaverMapUtils.LOCATION_PERMISSION_REQUEST_CODE) else null
        map.locationTrackingMode = LocationTrackingMode.Follow
        map.uiSettings.isZoomControlEnabled = false
        map.uiSettings.isScaleBarEnabled = false
        map.addOnLocationChangeListener {
            map.locationOverlay.bearing = 0f
        }
        map.addOnCameraChangeListener { _, _ ->
            mapPosition.value = map.cameraPosition.target
            map.contentBounds.let {
                val northWest = it.northWest
                val southEast = it.southEast
                val distanceM = calculateDistance(northWest, southEast)

                mapViewPortDistance.value = distanceM.toDouble()
            }
        }
        if (isShowOverlay) {
            val storeMarker = GlobalApplication.storeMarker ?: return
            if (storeMarker.image.url.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        storeMarker.image.url.let {
                            map.locationOverlay.icon = OverlayImage.fromBitmap(
                                withContext(Dispatchers.IO) {
                                    it.urlToBitmap().get()
                                })
                        }
                        map.locationOverlay.iconWidth = context?.convertDpToPx(44f)?.toInt() ?: 44
                        map.locationOverlay.iconHeight = context?.convertDpToPx(48f)?.toInt() ?: 48
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                map.locationOverlay.setOnClickListener {
                    onAdMarkerClicked?.invoke(storeMarker.advertisementId)
                    val dialog = MarkerClickDialog(latLng = currentPosition.value ?: LatLng.INVALID)
                    dialog.show(parentFragmentManager, dialog.tag)
                    return@setOnClickListener false
                }
            }
        }
    }

    fun addMarker(@DrawableRes drawableRes: Int, position: LatLng) {
        if (naverMap == null) {
            return
        }
        markers.add(
            Marker().apply {
                this.position = position
                this.icon = OverlayImage.fromResource(drawableRes)
                this.map = naverMap
            },
        )
    }

    fun clearMarker() {
        markers.forEach {
            it.map = null
        }
        markers.clear()
    }

    fun updateMarkerIcon(@DrawableRes drawableRes: Int, position: Int, markerModel: MarkerModel?, isSelected: Boolean) {
        if (markers.size > position) {
            if (markerModel == null) {
                markers[position].icon = OverlayImage.fromResource(drawableRes)
                markers[position].map = naverMap
            } else {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(
                        if (isSelected) {
                            markerModel.selected.imageUrl
                        } else {
                            markerModel.unSelected.imageUrl
                        }
                    )
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            markers[position].icon = OverlayImage.fromBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 로드가 취소되거나 메모리가 정리될 때 호출됩니다.
                        }
                    })

                val markerWidth = if (isSelected) {
                    markerModel.selected.width
                } else {
                    markerModel.unSelected.width
                }
                val markerHeight = if (isSelected) {
                    markerModel.selected.height
                } else {
                    markerModel.unSelected.height
                }

                markers[position].width = context?.convertDpToPx(markerWidth.toFloat())?.toInt() ?: markers[position].width
                markers[position].height = context?.convertDpToPx(markerHeight.toFloat())?.toInt() ?: markers[position].height
                markers[position].map = naverMap
            }
        }
    }

    fun addStoreMarkers(
        @DrawableRes drawableRes: Int,
        list: List<ContentModel>,
        onClick: (marker: ContentModel) -> Unit = {}
    ) {
        if (naverMap == null) {
            return
        }

        markers.forEach { it.map = null }
        markers.clear()

        val newMarkers = list.map { item ->
            Marker().apply {
                this.position = LatLng(item.storeModel.locationModel.latitude, item.storeModel.locationModel.longitude)
                if (item.markerModel == null) {
                    this.icon = OverlayImage.fromResource(drawableRes)
                } else {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(item.markerModel!!.unSelected.imageUrl)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                this@apply.icon = OverlayImage.fromBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // 로드가 취소되거나 메모리가 정리될 때 호출됩니다.
                            }
                        })
                    this.width = context?.convertDpToPx(item.markerModel!!.unSelected.width.toFloat())?.toInt() ?: this.width
                    this.height = context?.convertDpToPx(item.markerModel!!.unSelected.height.toFloat())?.toInt() ?: this.height
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
                        currentPosition.value = LatLng(it.latitude, it.longitude)
                        currentPosition.value?.let { position ->
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
                        currentPosition.value = LatLng(it.latitude, it.longitude)
                        onMyLocationLoaded(currentPosition.value)
                    } else {
                        onMyLocationLoaded(null)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    fun setIsShowOverlay(isVisible: Boolean) {
        isShowOverlay = isVisible
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

    private fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics,
        )
    }
}
