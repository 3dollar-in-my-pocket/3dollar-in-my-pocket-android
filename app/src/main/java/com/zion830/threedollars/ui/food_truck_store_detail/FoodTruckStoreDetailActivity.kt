package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.AppearanceDayModel
import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.StatusType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.convertUpdateAt
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckStoreDetailBinding
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.BossStoreMenuMoreResponse
import com.zion830.threedollars.ui.DirectionBottomDialog
import com.zion830.threedollars.ui.map.FullScreenMapActivity
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import java.util.*


@AndroidEntryPoint
class FoodTruckStoreDetailActivity :
    BaseActivity<ActivityFoodTruckStoreDetailBinding, FoodTruckStoreDetailViewModel>({ ActivityFoodTruckStoreDetailBinding.inflate(it) }) {

    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val foodTruckMenuAdapter: FoodTruckMenuRecyclerAdapter by lazy {
        FoodTruckMenuRecyclerAdapter {
            foodTruckMenuAdapter.submitList(viewModel.bossStoreDetailModel.value.store.menuModels)
        }
    }
    private val appearanceDayAdapter: AppearanceDayRecyclerAdapter by lazy {
        AppearanceDayRecyclerAdapter()
    }
    private val foodTruckReviewRecyclerAdapter: FoodTruckReviewRecyclerAdapter by lazy {
        FoodTruckReviewRecyclerAdapter()
    }

    private var storeId = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private val naverMapFragment: StoreDetailNaverMapFragment by lazy {
        StoreDetailNaverMapFragment()
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }
    private val appearanceDayModels = listOf(
        AppearanceDayModel(DayOfTheWeekType.MONDAY),
        AppearanceDayModel(DayOfTheWeekType.TUESDAY),
        AppearanceDayModel(DayOfTheWeekType.WEDNESDAY),
        AppearanceDayModel(DayOfTheWeekType.THURSDAY),
        AppearanceDayModel(DayOfTheWeekType.FRIDAY),
        AppearanceDayModel(DayOfTheWeekType.SATURDAY),
        AppearanceDayModel(DayOfTheWeekType.SUNDAY)
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        initMap()
        initViewModels()
        initButton()
        initFlows()
        initAdapter()
    }

    private fun initAdapter() {
        binding.menuInfoRecyclerView.adapter = foodTruckMenuAdapter
        binding.appearanceDayRecyclerView.adapter = appearanceDayAdapter
        binding.foodTruckReviewRecyclerView.adapter = foodTruckReviewRecyclerAdapter
    }

    private fun initMap() {
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
    }

    private fun initViewModels() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getStringExtra(STORE_ID).toString()
        if (isLocationAvailable() && isGpsAvailable()) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener {
                if (it != null) {
                    viewModel.getFoodTruckStoreDetail(
                        bossStoreId = storeId,
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            }
        }

        viewModel.getBossStoreFeedbackFull(bossStoreId = storeId)
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.bottomReviewTextView.setOnClickListener {
            moveFoodTruckReviewActivity()
        }
        binding.feedbackReviewTextView.setOnClickListener {
            moveFoodTruckReviewActivity()
        }
        binding.shareButton.setOnClickListener {
            initShared()
        }

        binding.snsButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.bossStoreDetailModel.value.store.snsUrl)))
        }
        binding.snsTextView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.bossStoreDetailModel.value.store.snsUrl)))
        }
        binding.directionsButton.setOnClickListener {
            showDirectionBottomDialog()
        }

        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.addressTextView.setOnClickListener {
            val manager = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            manager.text = binding.addressTextView.text
            showToast("주소가 복사됐습니다.")
        }
        binding.fullScreenButton.setOnClickListener {
            moveFullScreenMap()
        }
    }

    private fun moveFullScreenMap() {
        val store = viewModel.bossStoreDetailModel.value.store
        val intent = FullScreenMapActivity.getIntent(
            context = this,
            latitude = store.location?.latitude,
            longitude = store.location?.longitude,
            name = store.name,
            isClosed = viewModel.bossStoreDetailModel.value.openStatusModel.status == StatusType.CLOSED
        )
        startActivity(intent)
    }

    private fun showDirectionBottomDialog() {
        val store = viewModel.bossStoreDetailModel.value.store
        DirectionBottomDialog.getInstance(store.location?.latitude, store.location?.longitude, store.name).show(supportFragmentManager, "")
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.bossStoreDetailModel.collect { bossStoreDetailModel ->

                        val isClosed = bossStoreDetailModel.openStatusModel.status == StatusType.CLOSED

                        if (isClosed) showCustomBlackToast(getString(R.string.getting_ready_now))

                        if (bossStoreDetailModel.store.location != null) {
                            val latLng = LatLng(bossStoreDetailModel.store.location!!.latitude, bossStoreDetailModel.store.location!!.longitude)
                            naverMapFragment.initMap(latLng, isClosed)
                        }

                        appearanceDayAdapter.submitList(appearanceDayModels.map { appearanceDayModel ->
                            bossStoreDetailModel.store.appearanceDayModels.find {
                                appearanceDayModel.dayOfTheWeek == it.dayOfTheWeek
                            } ?: appearanceDayModel
                        })

                        if (bossStoreDetailModel.store.menuModels.isEmpty()) {
                            foodTruckMenuAdapter.submitList(listOf(FoodTruckMenuEmptyResponse()))
                        } else if (bossStoreDetailModel.store.menuModels.size > 5) {
                            val sublist = bossStoreDetailModel.store.menuModels.subList(0, 5)
                            val bossStoreMenuMoreResponse = BossStoreMenuMoreResponse(
                                moreTitle = getString(R.string.store_detail_menu_more, bossStoreDetailModel.store.menuModels.size - 5)
                            )
                            foodTruckMenuAdapter.submitList(sublist + bossStoreMenuMoreResponse)
                        } else {
                            foodTruckMenuAdapter.submitList(bossStoreDetailModel.store.menuModels)
                        }

                        binding.run {
                            if (bossStoreDetailModel.store.categories.isNotEmpty()) {
                                Glide.with(binding.menuIconImageView)
                                    .load(bossStoreDetailModel.store.categories.first().imageUrl)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(binding.menuIconImageView)
                            }
                            Glide.with(binding.storeImageView)
                                .load(bossStoreDetailModel.store.imageUrl)
                                .transform(FitCenter(), RoundedCorners(12))
                                .into(binding.storeImageView)

                            tagTextView.text = bossStoreDetailModel.store.categories.joinToString(" ") { "#${it.name}" }
                            distanceTextView.text =
                                if (bossStoreDetailModel.distanceM < 1000) "${bossStoreDetailModel.distanceM}m" else StringUtils.getString(R.string.more_1km)
                            storeNameTextView.text = bossStoreDetailModel.store.name
                            reviewTextView.text = getString(R.string.food_truck_review_count, bossStoreDetailModel.feedbackModels.size)
                            snsTextView.text = bossStoreDetailModel.store.snsUrl
                            ownerOneWordTextView.text = bossStoreDetailModel.store.introduction
                            feedbackCountTextView.text = getString(R.string.food_truck_review_count, bossStoreDetailModel.feedbackModels.size)
                            storeInfoUpdateAtTextView.text =
                                bossStoreDetailModel.store.updatedAt.convertUpdateAt(context = this@FoodTruckStoreDetailActivity)
                            addressTextView.text = bossStoreDetailModel.store.address.fullAddress
                        }
                    }
                }
                launch {
                    viewModel.favoriteModel.collect {
                        setFavoriteIcon(it.isFavorite)
                        binding.favoriteButton.text = it.totalSubscribersCount.toString()
                    }
                }
                launch {
                    viewModel.foodTruckReviewModelList.collect { foodTruckReviewModelList ->
                        foodTruckReviewRecyclerAdapter.submitList(foodTruckReviewModelList)
                    }
                }
            }
        }
    }


    private fun initShared() {
        EventTracker.logEvent(Constants.SHARE_BTN_CLICKED)
        val shareFormat = ShareFormat(
            getString(R.string.kakao_map_format),
            binding.storeNameTextView.text.toString(),
            LatLng(latitude, longitude)
        )
        shareWithKakao(
            shareFormat = shareFormat,
            title = getString(
                R.string.share_kakao_food_truck_title,
                viewModel.bossStoreDetailModel.value.store.name
            ),
            description = getString(
                R.string.share_kakao_food_truck,
                viewModel.bossStoreDetailModel.value.store.name
            ),
            imageUrl = viewModel.bossStoreDetailModel.value.store.imageUrl,
            storeId = storeId,
            type = getString(R.string.scheme_host_kakao_link_food_truck_type)
        )
    }

    private fun moveFoodTruckReviewActivity() {
        val intent = FoodTruckReviewActivity.getIntent(this, storeId)
        startActivity(intent)
    }

    private fun clickFavoriteButton() {
        if (viewModel.favoriteModel.value.isFavorite) {
            viewModel.deleteFavorite(Constants.BOSS_STORE, storeId)
        } else {
            viewModel.putFavorite(Constants.BOSS_STORE, storeId)
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean) {
        val favoriteIcon = if (isFavorite) R.drawable.ic_food_truck_favorite_on else R.drawable.ic_food_truck_favorite_off

        binding.favoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, favoriteIcon, 0, 0)
        binding.bottomFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
    }

    override fun finish() {
        navigateToMainActivityOnCloseIfNeeded()
        super.finish()
    }

    companion object {
        const val STORE_ID = "storeId"
        fun getIntent(context: Context, storeId: String? = null, deepLinkStoreId: String? = null) =
            Intent(context, FoodTruckStoreDetailActivity::class.java).apply {
                storeId?.let {
                    putExtra(STORE_ID, it)
                }
                deepLinkStoreId?.let {
                    putExtra(STORE_ID, it)
                }
            }
    }
}