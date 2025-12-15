package com.zion830.threedollars.ui.write.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import base.compose.ColorWhite
import base.compose.Gray10
import base.compose.Gray100
import base.compose.Gray60
import base.compose.Gray70
import base.compose.Green
import base.compose.Pink
import base.compose.PretendardFontFamily
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.threedollar.common.compose.dialog.CommonDialog
import com.threedollar.common.compose.dialog.DialogButton
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.ui.dialog.NearExistBottomSheetLayout
import com.zion830.threedollars.ui.dialog.NearStoreInfo
import com.zion830.threedollars.ui.write.viewModel.AddStoreContract
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

@AndroidEntryPoint
class NewAddressFragment : Fragment() {

    private val viewModel: AddStoreViewModel by activityViewModels()
    private lateinit var callback: OnBackPressedCallback
    private val showExitDialog = mutableStateOf(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val showDialog by showExitDialog

                NewAddressScreen(
                    viewModel = viewModel,
                    showExitDialog = showDialog,
                    onDismissDialog = { showExitDialog.value = false },
                    onConfirmExit = {
                        showExitDialog.value = false
                        viewModel.processIntent(AddStoreContract.Intent.ResetState)
                        findNavController().navigate(R.id.navigation_home)
                    },
                    onBackClick = { showExitDialog.value = true },
                    onFinishClick = { address ->
                        val bundle = Bundle().apply {
                            putString("screen", "write_address")
                            putString("address", address)
                        }
                        EventTracker.logEvent(Constants.CLICK_SET_ADDRESS, bundle)
                        viewModel.processIntent(AddStoreContract.Intent.SetAddress(address))
                        viewModel.state.value.selectedLocation?.let { location ->
                            viewModel.processIntent(AddStoreContract.Intent.CheckNearStore(location))
                        }
                    },
                    onNavigateToDetail = {
                        findNavController().navigateSafe(R.id.action_navigation_write_to_navigation_write_detail)
                    },
                    onBossDownloadClick = {
                        BossDownloadDialog.newInstance()
                            .setOnConfirmListener {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("market://details?id=app.threedollars.manager")
                                    setPackage("com.android.vending")
                                }
                                try {
                                    startActivity(intent)
                                } catch (_: ActivityNotFoundException) {
                                    startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=app.threedollars.manager")
                                        )
                                    )
                                }
                            }
                            .show(parentFragmentManager, "BossDownloadDialog")
                    }
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog.value = true
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        initNavigationBar()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterialApi::class)
@Composable
fun NewAddressScreen(
    viewModel: AddStoreViewModel,
    showExitDialog: Boolean,
    onDismissDialog: () -> Unit,
    onConfirmExit: () -> Unit,
    onBackClick: () -> Unit,
    onFinishClick: (String) -> Unit,
    onBossDownloadClick: () -> Unit,
    onNavigateToDetail: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val selectedLocation = state.selectedLocation
    val address = selectedLocation?.let { getCurrentLocationName(it) } ?: ""

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val nearStoresState = remember { mutableStateOf<List<NearStoreInfo>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddStoreContract.Effect.NearStoreExists -> {
                    if (effect.exists) {
                        nearStoresState.value = effect.nearStores
                        sheetState.show()
                    } else {
                        onNavigateToDetail()
                    }
                }
                else -> {}
            }
        }
    }

    if (showExitDialog) {
        CommonDialog(
            title = stringResource(CommonR.string.exit_confirm_title),
            description = stringResource(CommonR.string.exit_confirm_message),
            dismissButton = DialogButton(
                text = stringResource(CommonR.string.exit_confirm_dismiss),
                onClick = onDismissDialog
            ),
            confirmButton = DialogButton(
                text = stringResource(CommonR.string.exit_confirm_exit),
                onClick = onConfirmExit,
                isPrimary = true
            ),
            onDismissRequest = onDismissDialog
        )
    }

    NearExistBottomSheetLayout(
        sheetState = sheetState,
        nearStores = nearStoresState.value,
        address = address,
        onDismiss = {
            coroutineScope.launch { sheetState.hide() }
        },
        onConfirm = {
            coroutineScope.launch { sheetState.hide() }
            onNavigateToDetail()
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopTitleBar(
                onBackClick = onBackClick
            )
            NaverMapSection(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                selectedLocation = selectedLocation,
                onCameraIdle = { latLng, distance ->
                    viewModel.processIntent(AddStoreContract.Intent.UpdateLocation(latLng))
                }
            )

            BottomAddressSheet(
                address = address,
                onFinishClick = onFinishClick,
                onBossDownloadClick = onBossDownloadClick
            )
        }
    }
}

@Composable
fun TopTitleBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = stringResource(CommonR.string.title_add_store),
            modifier = Modifier.align(Alignment.Center),
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Gray100
        )
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_close_gray100_24),
                contentDescription = stringResource(CommonR.string.close),
                tint = Color.Unspecified
            )
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun NaverMapSection(
    modifier: Modifier,
    selectedLocation: LatLng?,
    onCameraIdle: (LatLng, Double) -> Unit
) {
    // 카메라 상태 관리
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            selectedLocation ?: LatLng(37.5666, 126.9784),
            15.0
        )
    }

    // 카메라 이동 완료 감지 (기존 IdleEventListener 대체)
    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .collect { isMoving ->
                if (!isMoving) {
                    delay(1000) // 기존 1초 딜레이 유지
                    val target = cameraPositionState.position.target
                    val contentBounds = cameraPositionState.contentBounds
                    val distance = if (contentBounds != null) {
                        calculateDistance(
                            contentBounds.northWest,
                            contentBounds.southEast
                        ).toDouble()
                    } else {
                        DEFAULT_DISTANCE_M
                    }
                    onCameraIdle(target, distance)
                }
            }
    }

    Box(modifier = modifier) {
        // Naver Map (Compose Native)
        NaverMap(
            modifier = Modifier.fillMaxSize(),
            locationSource = rememberFusedLocationSource(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                locationTrackingMode = LocationTrackingMode.Follow,
            ),
            uiSettings = MapUiSettings(
                isLocationButtonEnabled = true,
                isZoomControlEnabled = false,
            ),
        )

        // 중앙 고정 핀 마커 (오버레이)
        Icon(
            painter = painterResource(DesignSystemR.drawable.ic_mappin_focused_on),
            contentDescription = stringResource(CommonR.string.cd_location_pin),
            modifier = Modifier.align(Alignment.Center),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun BottomAddressSheet(
    modifier: Modifier = Modifier,
    address: String,
    onFinishClick: (String) -> Unit,
    onBossDownloadClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorWhite)
            .padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Gray10, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp, horizontal = 12.dp),
            text = address,
            textAlign = TextAlign.Center,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Gray70
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Pink, shape = RoundedCornerShape(12.dp))
                .clickable(onClick = { onFinishClick(address) })
                .padding(vertical = 14.dp),
            text = stringResource(CommonR.string.add_store_report_current_location),
            textAlign = TextAlign.Center,
            fontFamily = PretendardFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp,
            color = ColorWhite
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onBossDownloadClick() }),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(CommonR.string.add_store_boss_question),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                color = Gray60
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(CommonR.string.add_store_manage_easier),
                fontFamily = PretendardFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 14.sp,
                color = Green
            )
        }
    }
}

/*
==================== 기존 코드 (블록 주석으로 보존) ====================

package com.zion830.threedollars.ui.write.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.MainActivity
import com.zion830.threedollars.R
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.FragmentNewAddressBinding
import com.zion830.threedollars.ui.dialog.NearExistDialog
import com.zion830.threedollars.ui.map.ui.StoreAddNaverMapFragment
import com.zion830.threedollars.ui.write.viewModel.AddStoreViewModel
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.NaverMapUtils.calculateDistance
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.navigateSafe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class NewAddressFragment : BaseFragment<FragmentNewAddressBinding, AddStoreViewModel>() {

    override val viewModel: AddStoreViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreAddNaverMapFragment

    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.navigation_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        initNavigationBar()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initView() {
        initMap()
        initFlows()
        initButton()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "NewAddressFragment", screenName = "write_address")
    }

    private fun initNavigationBar() {
        if (requireActivity() is MainActivity) {
            (requireActivity() as MainActivity).showBottomNavigation(false)
        }
    }

    private fun initButton() {
        binding.backButton.onSingleClick {
            findNavController().navigate(R.id.navigation_home)
        }
        binding.finishButton.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "write_address")
                putString("address", binding.addressTextView.text.toString())
            }
            EventTracker.logEvent(Constants.CLICK_SET_ADDRESS, bundle)
            viewModel.selectedLocation.value?.let { location -> viewModel.getStoreNearExists(location) }
        }
    }

    private fun initMap() {
        naverMapFragment = StoreAddNaverMapFragment()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.mapContainer, naverMapFragment)?.commit()
        naverMapFragment.setIsShowOverlay(false)
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.aroundStoreModels.collect { res ->
                        naverMapFragment.addStoreMarkers(DesignSystemR.drawable.ic_mappin_focused_off, res ?: listOf())
                    }
                }
                launch {
                    viewModel.isNearStoreExist.collect {
                        if (it) {
                            showNearExistDialog()
                        } else {
                            val bundle = Bundle().apply {
                                putString("screen", "write_address")
                            }
                            EventTracker.logEvent(Constants.CLICK_CURRENT_LOCATION, bundle)
                            moveAddStoreDetailFragment()
                        }
                    }
                }
                launch {
                    viewModel.selectedLocation.collect { latLng ->
                        if (latLng != null) {
                            binding.addressTextView.text = getCurrentLocationName(latLng) ?: getString(CommonR.string.location_no_address)
                            val northWest = naverMapFragment.naverMap?.contentBounds?.northWest
                            val southEast = naverMapFragment.naverMap?.contentBounds?.southEast
                            viewModel.requestStoreInfo(
                                latLng,
                                if (northWest != null && southEast != null) calculateDistance(northWest, southEast).toDouble() else DEFAULT_DISTANCE_M
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showNearExistDialog() {
        viewModel.selectedLocation.value?.let { location ->
            NearExistDialog.getInstance(location.latitude, location.longitude)
                .apply {
                    setDialogListener(object : NearExistDialog.DialogListener {
                        override fun accept() {
                            moveAddStoreDetailFragment()
                        }
                    })
                }.show(parentFragmentManager, NearExistDialog().tag)
        }
    }

    private fun moveAddStoreDetailFragment() {
        findNavController().navigateSafe(R.id.action_navigation_write_to_navigation_write_detail)
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNewAddressBinding =
        FragmentNewAddressBinding.inflate(inflater, container, false)
}

==================== 기존 코드 끝 ====================
*/
