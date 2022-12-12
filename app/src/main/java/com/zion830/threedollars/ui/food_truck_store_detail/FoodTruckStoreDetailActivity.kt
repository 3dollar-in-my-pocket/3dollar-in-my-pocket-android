package com.zion830.threedollars.ui.food_truck_store_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckStoreDetailBinding
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.store.AppearanceDayModel
import com.zion830.threedollars.ui.store_detail.map.FoodTruckStoreDetailNaverMapFragment
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.loadRoundUrlImg
import zion830.com.common.base.loadUrlImg
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class FoodTruckStoreDetailActivity :
    BaseActivity<ActivityFoodTruckStoreDetailBinding, FoodTruckStoreDetailViewModel>(R.layout.activity_food_truck_store_detail) {

    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var foodTruckCategoriesAdapter: FoodTruckCategoriesRecyclerAdapter
    private lateinit var foodTruckMenuAdapter: FoodTruckMenuRecyclerAdapter
    private lateinit var foodTruckReviewRecyclerAdapter: FoodTruckReviewRecyclerAdapter
    private lateinit var appearanceDayAdapter: AppearanceDayRecyclerAdapter


    private var storeId = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private val naverMapFragment: FoodTruckStoreDetailNaverMapFragment =
        FoodTruckStoreDetailNaverMapFragment()


    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        EventTracker.logEvent(Constants.STORE_DELETE_BTN_CLICKED)
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()

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

        foodTruckCategoriesAdapter = FoodTruckCategoriesRecyclerAdapter()
        binding.foodTruckCategoryRecyclerView.adapter = foodTruckCategoriesAdapter

        foodTruckMenuAdapter = FoodTruckMenuRecyclerAdapter {
            foodTruckMenuAdapter.submitList(viewModel.bossStoreDetailModel.value?.menus)
        }
        binding.menuInfoRecyclerView.adapter = foodTruckMenuAdapter

        foodTruckReviewRecyclerAdapter = FoodTruckReviewRecyclerAdapter()
        binding.foodTruckReviewRecyclerView.adapter = foodTruckReviewRecyclerAdapter

        appearanceDayAdapter = AppearanceDayRecyclerAdapter()
        binding.appearanceDayRecyclerView.adapter = appearanceDayAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.topReviewTextView.setOnClickListener {
            moveFoodTruckReviewActivity()
        }
        binding.bottomReviewTextView.setOnClickListener {
            moveFoodTruckReviewActivity()
        }
        binding.feedbackReviewTextView.setOnClickListener {
            moveFoodTruckReviewActivity()
        }
        binding.btnShare.setOnClickListener {
            EventTracker.logEvent(Constants.SHARE_BTN_CLICKED)
            val shareFormat = ShareFormat(
                getString(R.string.kakao_map_format),
                binding.tvStoreName.text.toString(),
                LatLng(latitude, longitude)
            )
            shareWithKakao(
                shareFormat = shareFormat,
                title = getString(
                    R.string.share_kakao_food_truck_title,
                    viewModel.bossStoreDetailModel.value?.name
                ),
                description = getString(
                    R.string.share_kakao_food_truck,
                    viewModel.bossStoreDetailModel.value?.name
                ),
                imageUrl = viewModel.bossStoreDetailModel.value?.imageUrl,
                storeId = storeId,
                type = getString(R.string.scheme_host_kakao_link_food_truck_type)
            )
        }

        binding.snsTextView.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(viewModel.bossStoreDetailModel.value?.snsUrl)
                )
            )
        }
        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        viewModel.bossStoreDetailModel.observe(this@FoodTruckStoreDetailActivity) { bossStoreDetailModel ->
            foodTruckCategoriesAdapter.submitList(bossStoreDetailModel.categories)
            val isClosed = bossStoreDetailModel.openStatus?.status == "CLOSED"

            if (isClosed) showCustomBlackToast(getString(R.string.getting_ready_now))

            setFavoriteIcon(bossStoreDetailModel?.favorite?.isFavorite)

            val appearanceDayDefaultModelList = resources.getStringArray(R.array.day_name)
                .map { AppearanceDayModel(dayOfTheWeek = it) }
            val appearanceDayModelList = bossStoreDetailModel.appearanceDays?.map { it.toModel() }

            appearanceDayAdapter.submitList(appearanceDayDefaultModelList.map {
                appearanceDayModelList?.find { item -> it.dayOfTheWeek == item.dayOfTheWeek } ?: it
            })
            if (bossStoreDetailModel.menus.isNullOrEmpty()) {
                foodTruckMenuAdapter.submitList(listOf(FoodTruckMenuEmptyResponse()))
            } else if (bossStoreDetailModel.menus.size > 5) {
                val sublist = bossStoreDetailModel.menus.subList(0, 5)
                val foodTruckMenuMoreResponse = FoodTruckMenuMoreResponse(
                    moreTitle = getString(
                        R.string.food_truck_menu_more,
                        bossStoreDetailModel.menus.size - 5
                    ), moreImage = bossStoreDetailModel.menus[5].imageUrl
                )
                foodTruckMenuAdapter.submitList(sublist + foodTruckMenuMoreResponse)
            } else {
                foodTruckMenuAdapter.submitList(bossStoreDetailModel.menus)
            }

            binding.run {
                ivStoreType.loadUrlImg(bossStoreDetailModel.categories?.firstOrNull()?.imageUrl)
                tvStoreName.text = bossStoreDetailModel.name
                tvDistance.text = "${bossStoreDetailModel.distance}m"
                ownerOneWordTextView.text = bossStoreDetailModel.introduction
                storeImageView.loadRoundUrlImg(bossStoreDetailModel.imageUrl)
            }
        }

        viewModel.bossStoreFeedbackFullModelList.observe(this) { bossStoreFeedbackFullModeList ->
            binding.reviewCountTextView.text =
                "${bossStoreFeedbackFullModeList.sumOf { it.count }}개"
            foodTruckReviewRecyclerAdapter.submitList(bossStoreFeedbackFullModeList.map { it.feedbackFullModelToReviewModel() })
        }
        viewModel.isFavorite.observe(this) {
            val toastText = if (it) getString(R.string.toast_favorite_add) else getString(R.string.toast_favorite_delete)
            setFavoriteIcon(it)
            showCustomBlackToast(toastText)
        }
    }

    private fun moveFoodTruckReviewActivity() {
        val intent = FoodTruckReviewActivity.getIntent(this, storeId)
        startActivity(intent)
        finish()
    }

    private fun clickFavoriteButton() {
        if (viewModel.isFavorite.value == true) {
            viewModel.deleteFavorite(Constants.BOSS_STORE, storeId)
        } else {
            viewModel.putFavorite(Constants.BOSS_STORE, storeId)
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean?) {
        if (isFavorite == null) {
            showToast(R.string.connection_failed)
            return
        }

        val favoriteIcon = if (isFavorite) R.drawable.ic_food_truck_favorite_on else R.drawable.ic_food_truck_favorite_off

        binding.favoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
        binding.bottomFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
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