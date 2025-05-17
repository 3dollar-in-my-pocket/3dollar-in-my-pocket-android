package com.zion830.threedollars.ui.storeDetail.boss.ui

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.AppearanceDayModel
import com.home.domain.data.store.BossStoreDetailModel
import com.home.domain.data.store.DayOfTheWeekType
import com.home.domain.data.store.StatusType
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.convertUpdateAt
import com.threedollar.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.loadImage
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_NAVIGATION
import com.threedollar.common.utils.Constants.CLICK_NUMBER
import com.threedollar.common.utils.Constants.CLICK_SNS
import com.threedollar.common.utils.getDistanceText
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckStoreDetailBinding
import com.zion830.threedollars.datasource.model.v2.response.BossStoreMenuMoreResponse
import com.zion830.threedollars.datasource.model.v2.response.FoodTruckMenuEmptyResponse
import com.zion830.threedollars.ui.dialog.DirectionBottomDialog
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.map.ui.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.boss.adapter.AppearanceDayRecyclerAdapter
import com.zion830.threedollars.ui.storeDetail.boss.adapter.BossMenuRecyclerAdapter
import com.zion830.threedollars.ui.storeDetail.boss.adapter.BossReviewRecyclerAdapter
import com.zion830.threedollars.ui.storeDetail.boss.viewModel.BossStoreDetailViewModel
import com.zion830.threedollars.utils.OnMapTouchListener
import com.zion830.threedollars.utils.ShareFormat
import com.zion830.threedollars.utils.isGpsAvailable
import com.zion830.threedollars.utils.isLocationAvailable
import com.zion830.threedollars.utils.navigateToMainActivityOnCloseIfNeeded
import com.zion830.threedollars.utils.shareWithKakao
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick

@AndroidEntryPoint
class BossStoreDetailActivity :
    BaseActivity<ActivityFoodTruckStoreDetailBinding, BossStoreDetailViewModel>({ ActivityFoodTruckStoreDetailBinding.inflate(it) }) {

    override val viewModel: BossStoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val foodTruckMenuAdapter: BossMenuRecyclerAdapter by lazy {
        BossMenuRecyclerAdapter {
            foodTruckMenuAdapter.submitList(viewModel.bossStoreDetailModel.value.store.menus)
        }
    }
    private val appearanceDayAdapter: AppearanceDayRecyclerAdapter by lazy {
        AppearanceDayRecyclerAdapter()
    }
    private val bossReviewRecyclerAdapter: BossReviewRecyclerAdapter by lazy {
        BossReviewRecyclerAdapter()
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
        AppearanceDayModel(DayOfTheWeekType.SUNDAY),
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        initMap()
        initViewModels()
        initButton()
        initFlows()
        initAdapter()
        initAdmob()
    }

    private fun initAdmob() {
        val adRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "BossStoreDetailActivity", screenName = "boss_store_detail")
    }

    private fun initAdapter() {
        binding.menuInfoRecyclerView.adapter = foodTruckMenuAdapter
        binding.appearanceDayRecyclerView.adapter = appearanceDayAdapter
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
                        longitude = it.longitude,
                    )
                }
            }
        }

        viewModel.getBossStoreFeedbackFull(bossStoreId = storeId)
    }

    private fun initButton() {
        binding.btnBack.onSingleClick {
            setResult(RESULT_OK)
            finish()
        }
        binding.bottomReviewTextView.onSingleClick {
            moveFoodTruckReviewActivity()
        }
        binding.feedbackReviewTextView.onSingleClick {
            moveFoodTruckReviewActivity()
        }
        binding.shareButton.onSingleClick {
            initShared()
        }

        binding.snsButton.onSingleClick {
            val bundle = Bundle().apply {
                putString("screen", "boss_store_detail")
                putString("store_id", storeId)
            }
            viewModel.bossStoreDetailModel.value.store.snsUrl?.let {
                EventTracker.logEvent(CLICK_SNS, bundle)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
            }
        }
        binding.snsTextView.onSingleClick {
            if (viewModel.bossStoreDetailModel.value.store.snsUrl.isNotNullOrEmpty()) {
                val bundle = Bundle().apply {
                    putString("screen", "boss_store_detail")
                    putString("store_id", storeId)
                }
                EventTracker.logEvent(CLICK_SNS, bundle)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.bossStoreDetailModel.value.store.snsUrl)))
            }
        }
        binding.phoneTextView.onSingleClick {
            if (viewModel.bossStoreDetailModel.value.store.contactsNumbers.isNotEmpty()) {
                Bundle().apply {
                    putString("screen", "boss_store_detail")
                    putString("store_id", storeId)
                    EventTracker.logEvent(CLICK_NUMBER, this)
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("tel://${viewModel.bossStoreDetailModel.value.store.contactsNumbers.first().number}")
                        )
                    )
                }
            }
        }
        binding.directionsButton.onSingleClick {
            showDirectionBottomDialog()
        }

        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.addressTextView.onSingleClick {
            val manager = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            manager.text = binding.addressTextView.text
            showToast("주소가 복사됐습니다.")
        }
        binding.fullScreenButton.onSingleClick {
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
            isClosed = viewModel.bossStoreDetailModel.value.openStatusModel.status == StatusType.CLOSED,
        )
        startActivity(intent)
    }

    private fun showDirectionBottomDialog() {
        val bundle = Bundle().apply {
            putString("screen", "boss_store_detail")
            putString("store_id", storeId)
        }
        EventTracker.logEvent(CLICK_NAVIGATION, bundle)
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

                        appearanceDayAdapter.submitList(
                            appearanceDayModels.map { appearanceDayModel ->
                                bossStoreDetailModel.store.appearanceDays.find {
                                    appearanceDayModel.dayOfTheWeek == it.dayOfTheWeek
                                } ?: appearanceDayModel
                            },
                        )

                        if (bossStoreDetailModel.store.menus.isEmpty()) {
                            foodTruckMenuAdapter.submitList(listOf(FoodTruckMenuEmptyResponse()))
                        } else if (bossStoreDetailModel.store.menus.size > 5) {
                            val sublist = bossStoreDetailModel.store.menus.subList(0, 5)
                            val bossStoreMenuMoreResponse = BossStoreMenuMoreResponse(
                                moreTitle = getString(R.string.store_detail_menu_more, bossStoreDetailModel.store.menus.size - 5),
                            )
                            foodTruckMenuAdapter.submitList(sublist + bossStoreMenuMoreResponse)
                        } else {
                            foodTruckMenuAdapter.submitList(bossStoreDetailModel.store.menus)
                        }

                        binding.run {
                            if (bossStoreDetailModel.store.categories.isNotEmpty()) {
                                binding.menuIconImageView.loadImage(bossStoreDetailModel.store.categories.first().imageUrl)
                            }
                            if(bossStoreDetailModel.store.representativeImages.isNotEmpty()) {
                                Glide.with(binding.storeImageView)
                                    .load(bossStoreDetailModel.store.representativeImages.first().imageUrl)
                                    .transform(FitCenter(), RoundedCorners(12))
                                    .into(binding.storeImageView)
                            }
                            tagTextView.text = bossStoreDetailModel.store.categories.joinToString(" ") { "#${it.name}" }
                            distanceTextView.text = getDistanceText(bossStoreDetailModel.distanceM)
                            storeNameTextView.text = bossStoreDetailModel.store.name
                            reviewTextView.text = getString(R.string.food_truck_review_count, bossStoreDetailModel.feedbackModels.sumOf { it.count })
                            snsTextView.text = bossStoreDetailModel.store.snsUrl
                            if(bossStoreDetailModel.store.contactsNumbers.isNotEmpty()) {
                                phoneTextView.text = bossStoreDetailModel.store.contactsNumbers.first().number
                            }
                            ownerOneWordTextView.text = bossStoreDetailModel.store.introduction
                            feedbackCountTextView.text =
                                getString(R.string.food_truck_review_count, bossStoreDetailModel.feedbackModels.sumOf { it.count })
                            storeInfoUpdateAtTextView.text =
                                bossStoreDetailModel.store.updatedAt.convertUpdateAt(context = this@BossStoreDetailActivity)
                            addressTextView.text = bossStoreDetailModel.store.address.fullAddress
                            reviewRatingBar.rating = bossStoreDetailModel.store.rating
                        }

                        initAccount(bossStoreDetailModel)
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
                        bossReviewRecyclerAdapter.submitList(foodTruckReviewModelList)
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

    private fun initAccount(bossStoreDetailModel: BossStoreDetailModel) {
        val accountNumberModel = bossStoreDetailModel.store.accountNumbers?.firstOrNull()
        if (accountNumberModel != null) {
            binding.accountCardView.isVisible = true
            binding.accountNumberTextView.text =
                "${accountNumberModel.bank.description} ${accountNumberModel.accountNumber} | ${accountNumberModel.accountHolder}"
            binding.accountCopyButton.onSingleClick {
                val manager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                manager.text = accountNumberModel.accountNumber
                showToast("계좌번호가 복사되었습니다.")
            }
        } else {
            binding.accountCardView.isVisible = false
        }
    }

    private fun initShared() {
        val bundle = Bundle().apply {
            putString("screen", "boss_store_detail")
            putString("store_id", storeId)
        }
        EventTracker.logEvent(Constants.CLICK_SHARE, bundle)
        val shareFormat = ShareFormat(
            getString(R.string.kakao_map_format),
            binding.storeNameTextView.text.toString(),
            LatLng(latitude, longitude),
        )
        shareWithKakao(
            shareFormat = shareFormat,
            title = getString(
                R.string.share_kakao_food_truck_title,
                viewModel.bossStoreDetailModel.value.store.name,
            ),
            description = getString(
                R.string.share_kakao_food_truck,
                viewModel.bossStoreDetailModel.value.store.name,
            ),
            imageUrl = viewModel.bossStoreDetailModel.value.store.representativeImages.first().imageUrl,
            storeId = storeId,
            type = getString(R.string.scheme_host_kakao_link_food_truck_type),
        )
    }

    private fun moveFoodTruckReviewActivity() {
        val bundle = Bundle().apply {
            putString("screen", "boss_store_detail")
            putString("store_id", storeId)
        }
        EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
        val intent = BossReviewWriteActivity.getIntent(this, storeId)
        startActivity(intent)
    }

    private fun clickFavoriteButton() {
        val bundle = Bundle().apply {
            putString("screen", "boss_store_detail")
            putString("store_id", storeId)
        }
        if (viewModel.favoriteModel.value.isFavorite) {
            bundle.putString("value", "off")
            viewModel.deleteFavorite(storeId)
        } else {
            bundle.putString("value", "on")
            viewModel.putFavorite(storeId)
        }
        EventTracker.logEvent(Constants.CLICK_FAVORITE, bundle)
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
            Intent(context, BossStoreDetailActivity::class.java).apply {
                storeId?.let {
                    putExtra(STORE_ID, it)
                }
                deepLinkStoreId?.let {
                    putExtra(STORE_ID, it)
                }
            }
    }
}
