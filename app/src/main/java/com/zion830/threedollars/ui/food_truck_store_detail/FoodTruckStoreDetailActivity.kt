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
import com.zion830.threedollars.repository.model.v2.response.store.AppearanceDayModel
import com.zion830.threedollars.ui.store_detail.map.FoodTruckStoreDetailNaverMapFragment
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.ShareFormat
import com.zion830.threedollars.utils.shareWithKakao
import zion830.com.common.base.BaseActivity
import zion830.com.common.base.loadRoundUrlImg
import zion830.com.common.base.loadUrlImg

class FoodTruckStoreDetailActivity :
    BaseActivity<ActivityFoodTruckStoreDetailBinding, FoodTruckStoreDetailViewModel>(R.layout.activity_food_truck_store_detail),
    OnMapTouchListener {

    override val viewModel: FoodTruckStoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var foodTruckCategoriesAdapter: FoodTruckCategoriesRecyclerAdapter
    private lateinit var foodTruckReviewRecyclerAdapter: FoodTruckReviewRecyclerAdapter
    private lateinit var appearanceDayAdapter: AppearanceDayRecyclerAdapter


    private var storeId = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private val naverMapFragment: FoodTruckStoreDetailNaverMapFragment =
        FoodTruckStoreDetailNaverMapFragment()

    override fun onTouch() {
        binding.scroll.requestDisallowInterceptTouchEvent(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        EventTracker.logEvent(Constants.STORE_DELETE_BTN_CLICKED)

        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getStringExtra(KEY_STORE_ID).toString()
        latitude = intent.getDoubleExtra(LATITUDE, 0.0)
        longitude = intent.getDoubleExtra(LONGITUDE, 0.0)

        viewModel.getFoodTruckStoreDetail(
            bossStoreId = storeId,
            latitude = latitude,
            longitude = longitude
        )
        viewModel.getBossStoreFeedbackFull(bossStoreId = storeId)

        foodTruckCategoriesAdapter = FoodTruckCategoriesRecyclerAdapter()
        binding.foodTruckCategoryRecyclerView.adapter = foodTruckCategoriesAdapter

        foodTruckReviewRecyclerAdapter = FoodTruckReviewRecyclerAdapter()
        binding.foodTruckReviewRecyclerView.adapter = foodTruckReviewRecyclerAdapter

        appearanceDayAdapter = AppearanceDayRecyclerAdapter()
        binding.appearanceDayRecyclerView.adapter = appearanceDayAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.topReviewTextView.setOnClickListener {
            val intent = Intent(this, FoodTruckReviewActivity::class.java)
            intent.putExtra(KEY_STORE_ID, storeId)

            startActivity(intent)
        }
        binding.bottomReviewTextView.setOnClickListener {
            val intent = Intent(this, FoodTruckReviewActivity::class.java)
            intent.putExtra(KEY_STORE_ID, storeId)

            startActivity(intent)
        }
        binding.btnShare.setOnClickListener {
            EventTracker.logEvent(Constants.SHARE_BTN_CLICKED)
            val shareFormat = ShareFormat(
                getString(R.string.kakao_map_format),
                binding.tvStoreName.text.toString(),
                LatLng(latitude, longitude)
            )
            shareWithKakao(shareFormat)
        }

        binding.snsTextView.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(viewModel.bossStoreDetailModel.value?.snsUrl)
                )
            )
        }

        viewModel.bossStoreDetailModel.observe(this@FoodTruckStoreDetailActivity) { bossStoreDetailModel ->
            foodTruckCategoriesAdapter.submitList(bossStoreDetailModel.categories)

            val appearanceDayDefaultModelList = resources.getStringArray(R.array.day_name)
                .map { AppearanceDayModel(dayOfTheWeek = it) }
            val appearanceDayModelList = bossStoreDetailModel.appearanceDays?.map { it.toModel() }

            appearanceDayAdapter.submitList(appearanceDayDefaultModelList.map {
                appearanceDayModelList?.find { item -> it.dayOfTheWeek == item.dayOfTheWeek } ?: it
            })

            binding.run {
                ivStoreType.loadUrlImg(bossStoreDetailModel.categories?.firstOrNull()?.imageUrl)
                tvStoreName.text = bossStoreDetailModel.name
                tvDistance.text = "${bossStoreDetailModel.distance}m"
                phoneNumberTextView.text = bossStoreDetailModel.contactsNumber
                ownerOneWordTextView.text = bossStoreDetailModel.introduction
                storeImageView.loadRoundUrlImg(bossStoreDetailModel.imageUrl)
            }
        }

        viewModel.bossStoreFeedbackFullModelList.observe(this) { bossStoreFeedbackFullModeList ->
            binding.reviewCountTextView.text =
                "${bossStoreFeedbackFullModeList.sumOf { it.count }}개"
            foodTruckReviewRecyclerAdapter.submitList(bossStoreFeedbackFullModeList.map { it.feedbackFullModelToReviewModel() })
        }


    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        private const val LATITUDE = "LATITUDE"
        private const val LONGITUDE = "LONGITUDE"

        fun getIntent(context: Context, storeId: String, latitude: Double, longitude: Double) =
            Intent(context, FoodTruckStoreDetailActivity::class.java).apply {
                putExtra(KEY_STORE_ID, storeId)
                putExtra(LATITUDE, latitude)
                putExtra(LONGITUDE, longitude)
            }
    }
}