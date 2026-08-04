package com.zion830.threedollars.ui.map.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
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
import com.threedollar.common.serverdriven.ext.displayText
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.MarkerModel
import com.threedollar.common.serverdriven.model.HomeListCardModel
import com.threedollar.common.serverdriven.model.SDChipModel
import com.threedollar.common.serverdriven.model.SDImageModel
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentNaverMapBinding
import com.zion830.threedollars.ui.dialog.MarkerClickDialog
import com.zion830.threedollars.ui.home.ui.markerChipForSelection
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.TouchableWrapper
import com.zion830.threedollars.utils.isLocationServiceEnabled
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.requestPermissionIfNeeds
import com.zion830.threedollars.utils.urlToBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
open class NaverMapFragment : Fragment(R.layout.fragment_naver_map), OnMapReadyCallback {
    var naverMap: NaverMap? = null

    var currentPosition: MutableLiveData<LatLng> = MutableLiveData()
    var mapPosition: MutableLiveData<LatLng> = MutableLiveData()
    var mapViewPortDistance: MutableLiveData<Double> = MutableLiveData(DEFAULT_DISTANCE_M)

    protected lateinit var binding: FragmentNaverMapBinding

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = arrayListOf<Marker>()

    var listener: OnMapTouchListener? = null

    private var isShowOverlay = true

    /**
     * 지도가 최초 카메라 위치로 이동했는지 여부.
     *
     * 지도 SDK는 준비 직후 자체 기본 위치(서울시청)에 카메라를 두고 변경 이벤트를 발생시킨다.
     * 이 값을 저장된 지도 위치로 남기면 다음 실행에서 현재 위치를 요청하지 않고 그 위치로 이동해버리므로,
     * 실제 위치가 정해지기 전까지는 [mapPosition]을 갱신하지 않는다.
     */
    private var isInitialCameraPlaced = false

    /**
     * 서버가 내려준 최초 지도 줌 레벨.
     *
     * 첫 카메라 배치보다 응답이 먼저 도착하면 그 배치에 함께 적용하고, 늦게 도착하면 줌만 따로 맞춘다.
     * 어느 쪽이든 최초 1회만 적용하고, 이후 사용자가 조작한 줌은 건드리지 않는다.
     */
    private var initialZoomLevel: Double? = null
    private var isInitialZoomLevelApplied = false

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
                childFragmentManager.beginTransaction().add(R.id.fragment_map, it).commitNow()
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
            if (isInitialCameraPlaced) {
                mapPosition.value = map.cameraPosition.target
            }
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
                this.tag = item.storeModel.storeId
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

    fun addHomeListMarkers(
        @DrawableRes drawableRes: Int,
        list: List<HomeListCardModel.BasicCard>,
        onClick: (marker: HomeListCardModel.BasicCard) -> Unit = {},
    ) {
        if (naverMap == null) {
            return
        }

        markers.forEach { it.map = null }
        markers.clear()

        val newMarkers = list.map { item ->
            Marker().apply {
                this.position = LatLng(item.marker.location.latitude, item.marker.location.longitude)
                this.tag = item.cardId
                applyHomeListMarkerIcon(marker = this, chip = item.marker.unfocused, drawableRes = drawableRes)
                this.map = naverMap
                setOnClickListener {
                    onClick(item)
                    true
                }
            }
        }
        markers.addAll(newMarkers)
    }

    fun updateHomeListMarkerIcon(
        @DrawableRes drawableRes: Int,
        position: Int,
        card: HomeListCardModel.BasicCard?,
        isSelected: Boolean,
    ) {
        if (markers.size <= position) return
        val marker = markers[position]
        val chip = card?.markerChipForSelection(isSelected)
        applyHomeListMarkerIcon(marker = marker, chip = chip, drawableRes = drawableRes)
        marker.map = naverMap
    }

    private fun applyHomeListMarkerIcon(
        marker: Marker,
        chip: SDChipModel?,
        @DrawableRes drawableRes: Int,
    ) {
        val image = chip?.image
        if (image != null) {
            loadMarkerImage(marker, image)
            return
        }
        val markerText = chip?.markerText().orEmpty()
        if (markerText.isBlank()) {
            marker.icon = OverlayImage.fromResource(drawableRes)
            return
        }
        val bitmap = createChipMarkerBitmap(chip ?: return)
        marker.icon = OverlayImage.fromBitmap(bitmap)
        marker.width = bitmap.width
        marker.height = bitmap.height
    }

    private fun loadMarkerImage(marker: Marker, image: SDImageModel) {
        Glide.with(requireContext())
            .asBitmap()
            .load(image.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    marker.icon = OverlayImage.fromBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
        image.style?.width?.let { marker.width = context?.convertDpToPx(it.toFloat())?.toInt() ?: marker.width }
        image.style?.height?.let { marker.height = context?.convertDpToPx(it.toFloat())?.toInt() ?: marker.height }
    }

    private fun createChipMarkerBitmap(chip: SDChipModel): Bitmap {
        val textView = TextView(requireContext()).apply {
            text = chip.markerText()
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(chip.text.fontColor.toAndroidColor(Color.WHITE))
            setPadding(
                context.convertDpToPx(10f).toInt(),
                context.convertDpToPx(6f).toInt(),
                context.convertDpToPx(10f).toInt(),
                context.convertDpToPx(6f).toInt(),
            )
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = context.convertDpToPx(14f)
                setColor(chip.style?.backgroundColor.toAndroidColor(Color.rgb(255, 133, 143)))
                chip.style?.border?.let { border ->
                    setStroke(
                        context.convertDpToPx((border.width ?: 1.0).toFloat()).toInt(),
                        border.color.toAndroidColor(Color.TRANSPARENT),
                    )
                }
            }
        }
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthSpec, heightSpec)
        textView.layout(0, 0, textView.measuredWidth, textView.measuredHeight)
        return Bitmap.createBitmap(textView.measuredWidth, textView.measuredHeight, Bitmap.Config.ARGB_8888).also { bitmap ->
            textView.draw(Canvas(bitmap))
        }
    }

    fun updateMarkerPosition(storeId: String, latitude: Double, longitude: Double) {
        markers.find { it.tag == storeId }?.position = LatLng(latitude, longitude)
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
    fun moveToCurrentLocation(
        showAnim: Boolean = false,
        onLocationLoaded: (LatLng?) -> Unit = {},
    ) {
        try {
            requestCurrentLocation { position ->
                if (position != null) {
                    currentPosition.value = position
                    naverMap?.locationOverlay?.isVisible = true
                    naverMap?.locationOverlay?.position = position
                    if (showAnim) {
                        moveCameraWithAnim(position)
                    } else {
                        moveCamera(position)
                    }
                    onMyLocationLoaded(position)
                }
                onLocationLoaded(position)
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
            moveCamera(NaverMapUtils.DEFAULT_LOCATION)
            onLocationLoaded(null)
        }
    }

    @SuppressLint("MissingPermission")
    fun updateMyLatestLocation(onMyLocationLoaded: (LatLng?) -> Unit) {
        try {
            requestCurrentLocation { position ->
                if (position != null) {
                    currentPosition.value = position
                }
                onMyLocationLoaded(position)
            }
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message ?: "")
            onMyLocationLoaded(null)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestCurrentLocation(onLocationLoaded: (LatLng?) -> Unit) {
        if (!isLocationAvailable() || !isLocationServiceEnabled()) {
            onLocationLoaded(null)
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationLoaded(location.toLatLng().also { cacheUserLocation(it) })
                } else {
                    requestFreshCurrentLocation(onLocationLoaded)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(this::class.java.name, exception.message ?: "")
                requestFreshCurrentLocation(onLocationLoaded)
            }
    }

    private fun cacheUserLocation(position: LatLng) {
        sharedPrefUtils.saveUserLastLocation(latitude = position.latitude, longitude = position.longitude)
    }

    /**
     * 마지막으로 확인된 사용자 위치. 저장된 값이 없으면 null.
     *
     * 위치 획득에 실패했을 때 서울 중심 좌표로 떨어지기 전에 우선 사용한다.
     */
    fun getCachedUserLocation(): LatLng? =
        sharedPrefUtils.getUserLastLocation()?.let { (latitude, longitude) -> LatLng(latitude, longitude) }

    @SuppressLint("MissingPermission")
    private fun requestFreshCurrentLocation(onLocationLoaded: (LatLng?) -> Unit) {
        val cancellationTokenSource = CancellationTokenSource()
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setDurationMillis(CURRENT_LOCATION_TIMEOUT_MILLIS)
            .build()
        fusedLocationProviderClient
            .getCurrentLocation(request, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                onLocationLoaded(location?.toLatLng()?.also { cacheUserLocation(it) })
            }
            .addOnFailureListener { exception ->
                Log.e(this::class.java.name, exception.message ?: "")
                onLocationLoaded(null)
            }
            .addOnCanceledListener {
                onLocationLoaded(null)
            }
    }

    private fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)

    private companion object {
        /**
         * 위치 수신 대기 시간.
         *
         * 실내나 측위가 느린 환경에서 5초는 첫 fix를 받기에 부족해 기본 위치로 떨어지는 경우가 잦았다.
         * iOS는 타임아웃 없이 첫 fix까지 기다린다.
         */
        const val CURRENT_LOCATION_TIMEOUT_MILLIS = 10_000L
    }

    fun setIsShowOverlay(isVisible: Boolean) {
        isShowOverlay = isVisible
    }

    /**
     * 서버가 내려준 최초 지도 줌 레벨을 적용한다.
     *
     * 아직 첫 카메라 배치 전이면 값만 보관했다가 그 배치에 함께 적용하고,
     * 이미 배치된 뒤라면 줌만 애니메이션으로 맞춘다.
     */
    fun applyInitialZoomLevel(zoomLevel: Double) {
        if (isInitialZoomLevelApplied) return

        initialZoomLevel = zoomLevel
        val map = naverMap
        if (isInitialCameraPlaced && map != null) {
            isInitialZoomLevelApplied = true
            map.moveCamera(CameraUpdate.zoomTo(zoomLevel).animate(CameraAnimation.Easing))
        }
    }

    fun moveCamera(position: LatLng) {
        if (naverMap == null) {
            return
        }

        isInitialCameraPlaced = true
        naverMap?.moveCamera(cameraUpdateForMove(position))
    }

    fun moveCameraWithAnim(position: LatLng) {
        if (naverMap == null) {
            return
        }

        isInitialCameraPlaced = true
        naverMap?.moveCamera(cameraUpdateForMove(position).animate(CameraAnimation.Easing))
    }

    private fun cameraUpdateForMove(position: LatLng): CameraUpdate {
        val zoomLevel = initialZoomLevel
        if (isInitialZoomLevelApplied || zoomLevel == null) {
            return CameraUpdate.scrollTo(position)
        }

        isInitialZoomLevelApplied = true
        return CameraUpdate.scrollAndZoomTo(position, zoomLevel)
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

private fun SDChipModel.markerText(): String {
    return displayText()
}

private fun String?.toAndroidColor(fallback: Int): Int {
    return runCatching {
        this?.toColorInt() ?: fallback
    }.getOrDefault(fallback)
}
