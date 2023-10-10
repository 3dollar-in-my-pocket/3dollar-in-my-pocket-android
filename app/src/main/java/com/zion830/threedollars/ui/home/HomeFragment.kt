package com.zion830.threedollars.ui.home

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessaging
import com.home.domain.data.advertisement.AdvertisementModel
import com.home.domain.data.store.ContentModel
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.BOSS_STORE
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.datasource.model.v2.response.store.BossNearStoreResponse
import com.zion830.threedollars.ui.MarketingDialog
import com.zion830.threedollars.ui.addstore.view.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.food_truck_store_detail.FoodTruckStoreDetailActivity
import com.zion830.threedollars.ui.home.adapter.AroundStoreMapViewRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import com.zion830.threedollars.utils.getCurrentLocationName
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

    private val searchViewModel: SearchAddressViewModel by activityViewModels()

    private lateinit var adapter: AroundStoreMapViewRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    private var homeStoreType = HomeStoreType.ALL
    private var homeSortType = HomeSortType.DISTANCE_ASC

    override fun initView() {
        initMap()
        initAdapter()
        initViewModel()
        initFlow()
        initButton()
        initScroll()

        viewModel.addressText.observe(viewLifecycleOwner) {
            binding.tvAddress.text = it ?: getString(R.string.location_no_address)
        }
        searchViewModel.searchResultLocation.observe(viewLifecycleOwner) {
            naverMapFragment.moveCamera(it)
            binding.tvAddress.text =
                getCurrentLocationName(it) ?: getString(R.string.location_no_address)
        }
    }

    private fun initScroll() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.aroundStoreRecyclerView)
        binding.aroundStoreRecyclerView.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (adapter.getItemLocation(position) != null) {
                            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
                            adapter.focusedIndex = position
                            naverMapFragment.updateMarkerIcon(R.drawable.ic_mappin_focused_on, adapter.focusedIndex)

                            adapter.getItemLocation(position)?.let {
                                naverMapFragment.moveCameraWithAnim(it)
                            }
                        }
                    }
                })
        )
    }

    private fun initViewModel() {
        viewModel.getUserInfo()
        viewModel.requestHomeItem(naverMapFragment.getMapCenterLatLng())
    }

    private fun initAdapter() {
        adapter = AroundStoreMapViewRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
                EventTracker.logEvent(Constants.STORE_CARD_BTN_CLICKED)
                if (item.storeModel.storeType == BOSS_STORE) {
                    val intent =
                        FoodTruckStoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                } else {
                    val intent =
                        StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), false)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                }

            }
        }, object : OnItemClickListener<AdvertisementModel> {
            override fun onClick(item: AdvertisementModel) {
                EventTracker.logEvent(Constants.HOME_AD_BANNER_CLICKED)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.linkUrl)))
            }
        }) { item ->
            val intent = StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), true)
            startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
        }
        binding.aroundStoreRecyclerView.adapter = adapter
    }

    private fun initMap() {
        naverMapFragment = NearStoreNaverMapFragment {
            binding.tvRetrySearch.isVisible = true
        }
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()
        naverMapFragment.moveToCurrentLocation(false)
    }

    private fun initButton() {
        binding.layoutAddress.setOnClickListener {
            EventTracker.logEvent(Constants.SEARCH_BTN_CLICKED)
            requireActivity().supportFragmentManager.addNewFragment(
                R.id.layout_container,
                SearchAddressFragment.newInstance(),
                SearchAddressFragment::class.java.name
            )
        }

        binding.allMenuTextView.setOnClickListener {
            showSelectCategoryDialog()
        }

        binding.filterTextView.setOnClickListener {
            homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) {
                HomeSortType.LATEST
            } else {
                HomeSortType.DISTANCE_ASC
            }
            viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
        }

        binding.bossFilterTextView.setOnClickListener {
            homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
            viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
        }

        binding.listViewTextView.setOnClickListener {
            it.findNavController().navigate(R.id.action_home_to_home_list_view)
        }
        binding.tvRetrySearch.setOnClickListener {
            viewModel.requestHomeItem(naverMapFragment.getMapCenterLatLng())
            binding.tvRetrySearch.isVisible = false
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectCategory.collect {
                        val text = if (it.categoryId.isEmpty()) getString(R.string.fragment_home_all_menu) else it.name
                        val textColor = if (it.categoryId.isEmpty()) R.color.gray70 else R.color.pink
                        val background =
                            if (it.categoryId.isEmpty()) R.drawable.rect_white_radius10_stroke_gray30 else R.drawable.rect_white_radius10_stroke_black_fill_black

                        binding.run {
                            allMenuTextView.text = text
                            allMenuTextView.setTextColor(resources.getColor(textColor, null))
                            allMenuTextView.setBackgroundResource(background)
                            if (it.imageUrl.isEmpty()) {
                                allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_category), null, null, null
                                )
                            } else {
                                loadImageUriIntoDrawable(it.imageUrl.toUri()) { drawable ->
                                    allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                                }
                            }
                        }
                        viewModel.requestHomeItem(naverMapFragment.getMapCenterLatLng())
                    }
                }
                launch {
                    viewModel.userInfo.collect {
                        if (it.marketingConsent == "UNVERIFIED") {
                            showMarketingDialog()
                        }
                    }
                }

                launch {
                    viewModel.aroundStoreModels.collect { adAndStoreItems ->
                        adapter.submitList(adAndStoreItems)
                        val list = adAndStoreItems.filterIsInstance<ContentModel>()
                        naverMapFragment.addStoreMarkers(R.drawable.ic_store_off, list) {
                            onStoreClicked(it)
                        }
                        delay(200L)
                        binding.aroundStoreRecyclerView.scrollToPosition(0)
                    }
                }
                launch {
                    viewModel.homeFilterEvent.collect {
                        viewModel.requestHomeItem(naverMapFragment.getMapCenterLatLng())

                        val textColor = resources.getColor(if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.color.gray70 else R.color.gray40, null)
                        val drawableStart = ContextCompat.getDrawable(
                            requireContext(),
                            if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.drawable.ic_check_gray_16 else R.drawable.ic_uncheck
                        )
                        binding.run {
                            bossFilterTextView.setTextColor(textColor)
                            bossFilterTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                            filterTextView.text = if (it.homeSortType == HomeSortType.DISTANCE_ASC) {
                                getString(R.string.fragment_home_filter_latest)
                            } else {
                                getString(R.string.fragment_home_filter_distance)
                            }
                        }
                    }
                }

                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun loadImageUriIntoDrawable(imageUri: Uri, callback: (Drawable?) -> Unit) {
        Glide.with(requireContext())
            .load(imageUri)
            .override(64)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(null)
                }
            })
    }

    private fun showSelectCategoryDialog() {
        val dialog = SelectCategoryDialogFragment()
        dialog.show(parentFragmentManager, "")
    }

    private fun onStoreClicked(adAndStoreItem: AdAndStoreItem) {
        val position = adapter.getItemPosition(adAndStoreItem)
        if (position >= 0) {
            naverMapFragment.updateMarkerIcon(R.drawable.ic_store_off, adapter.focusedIndex)
            adapter.focusedIndex = position
            naverMapFragment.updateMarkerIcon(R.drawable.ic_marker, adapter.focusedIndex)
            naverMapFragment.moveCameraWithAnim(
                if (adAndStoreItem is ContentModel) {
                    LatLng(adAndStoreItem.storeModel.locationModel.latitude, adAndStoreItem.storeModel.locationModel.longitude)
                } else {
                    val location =
                        (adAndStoreItem as BossNearStoreResponse.BossNearStoreModel).location
                    LatLng(location.latitude, location.longitude)
                }
            )

            adapter.notifyDataSetChanged()
            binding.aroundStoreRecyclerView.scrollToPosition(position)
        }
    }

    private fun showMarketingDialog() {
        val dialog = MarketingDialog()
        dialog.setDialogListener(object : MarketingDialog.DialogListener {
            override fun accept(isMarketing: Boolean) {
                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (it.isSuccessful) {
                        viewModel.postPushInformation(pushToken = it.result, isMarketing = isMarketing)
                    }
                }
            }
        })
        dialog.show(parentFragmentManager, dialog.tag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.GET_LOCATION_PERMISSION) {
            naverMapFragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater, container, false)
}