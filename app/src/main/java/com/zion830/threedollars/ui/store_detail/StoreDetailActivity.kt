package com.zion830.threedollars.ui.store_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.UserStoreDetailModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.ext.changeTextPartBold
import com.threedollar.common.ext.getMonthFirstDate
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.USER_STORE
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.datasource.model.v2.response.my.Review
import com.zion830.threedollars.ui.addstore.EditStoreDetailFragment
import com.zion830.threedollars.ui.addstore.adapter.PhotoRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.ReviewRecyclerAdapter
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.report_store.AddReviewDialog
import com.zion830.threedollars.ui.report_store.DeleteStoreDialog
import com.zion830.threedollars.ui.report_store.StorePhotoDialog
import com.zion830.threedollars.ui.store_detail.adapter.CategoryInfoRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.adapter.VisitHistoryAdapter
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty
import com.threedollar.common.ext.showSnack

@AndroidEntryPoint
class StoreDetailActivity : BaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>({ ActivityStoreInfoBinding.inflate(it) }) {

    override val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val categoryAdapter = CategoryInfoRecyclerAdapter()

    private var storeId = 0

    private var startCertificationExactly: Boolean? = false

    private lateinit var photoAdapter: PhotoRecyclerAdapter

    private lateinit var reviewAdapter: ReviewRecyclerAdapter

    private val naverMapFragment: StoreDetailNaverMapFragment = StoreDetailNaverMapFragment()

    private val visitHistoryAdapter = VisitHistoryAdapter()

    private var progressDialog: AlertDialog? = null

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        storeId = intent.getIntExtra(STORE_ID, 0)

        initMap()
        initAd()
        initButton()
        initAdapter()
        initFlows()

        viewModel.addReviewResult.observe(this) {
            EventTracker.logEvent(Constants.REVIEW_WRITE_BTN_CLICKED)
            viewModel.getUserStoreDetail(
                storeId = storeId,
                deviceLatitude = viewModel.userStoreDetailModel.value.store?.location?.latitude,
                deviceLongitude = viewModel.userStoreDetailModel.value.store?.location?.longitude,
                filterVisitStartDate = getMonthFirstDate()
            )
        }
        viewModel.closeActivity.observe(this) {
            if (it) {
                finish()
            }
        }
        viewModel.photoDeleted.observe(this) {
            if (it) {
                viewModel.getUserStoreDetail(
                    storeId = storeId,
                    deviceLatitude = viewModel.userStoreDetailModel.value.store?.location?.latitude,
                    deviceLongitude = viewModel.userStoreDetailModel.value.store?.location?.longitude,
                    filterVisitStartDate = getMonthFirstDate()
                )
            } else {
                binding.root.showSnack(getString(R.string.delete_photo_failed))
            }
        }
        viewModel.uploadImageStatus.observe(this) {
            if (it) {
                if (progressDialog == null) {
                    progressDialog = AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setView(R.layout.layout_image_upload_progress)
                        .create()
                }
                progressDialog?.show()
            } else {
                progressDialog?.dismiss()
                refreshStoreInfo()
            }
        }
        viewModel.isExistStoreInfo.observe(this) { isExistStore ->
            val isExist = isExistStore.second
            if (!isExist) {
                showToast(R.string.exist_store_error)
                finish()
            }
        }

        viewModel.isFavorite.observe(this) {
            setFavoriteIcon(it)
        }
    }

    private fun initAd() {
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initAdapter() {
        reviewAdapter = ReviewRecyclerAdapter(
            object : OnItemClickListener<Review> {
                override fun onClick(item: Review) {
                    AddReviewDialog.getInstance(item)
                        .show(supportFragmentManager, AddReviewDialog::class.java.name)
                }
            },
            object : OnItemClickListener<Review> {
                override fun onClick(item: Review) {
                    viewModel.deleteReview(item.reviewId)
                }
            },
        )
        photoAdapter = PhotoRecyclerAdapter(object : OnItemClickListener<StoreImage> {
            override fun onClick(item: StoreImage) {
                StorePhotoDialog.getInstance(item.index)
                    .show(supportFragmentManager, StorePhotoDialog::class.java.name)
            }

        })
        binding.rvVisitHistory.adapter = visitHistoryAdapter

        binding.rvPhoto.adapter = photoAdapter
        binding.rvCategory.adapter = categoryAdapter
        binding.rvReview.adapter = reviewAdapter
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

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.deleteTextView.setOnClickListener {
            DeleteStoreDialog.getInstance(storeId)
                .show(supportFragmentManager, DeleteStoreDialog::class.java.name)
        }
        binding.btnAddPhoto.setOnClickListener {
            TedImagePicker.with(this).zoomIndicator(false).errorListener {
                if (it.message?.startsWith("permission") == true) {
                    AlertDialog.Builder(this)
                        .setPositiveButton(R.string.request_permission_ok) { _, _ ->
                            goToPermissionSetting()
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .setTitle(getString(R.string.request_permission))
                        .setMessage(getString(R.string.request_permission_msg))
                        .create()
                        .show()
                }
            }.startMultiImage { uriData ->
                EventTracker.logEvent(Constants.IMAGE_ATTACH_BTN_CLICKED)
                lifecycleScope.launch {
                    val images = getImageFiles(uriData)
                    if (images != null) {
                        viewModel.saveImages(images)
                    }
                }
            }
        }
        binding.shareButton.setOnClickListener {
            EventTracker.logEvent(Constants.SHARE_BTN_CLICKED)
            val userStoreModel = viewModel.userStoreDetailModel.value.store

            userStoreModel?.let {
                val shareFormat = ShareFormat(
                    getString(R.string.kakao_map_format),
                    binding.storeNameTextView.text.toString(),
                    LatLng(it.location.latitude, it.location.longitude)
                )
                shareWithKakao(
                    shareFormat = shareFormat,
                    title = getString(
                        R.string.share_kakao_road_food_title,
                        userStoreModel.name
                    ),
                    description = getString(
                        R.string.share_kakao_road_food,
                        userStoreModel.name
                    ),
                    imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
                    storeId = storeId.toString(),
                    type = getString(R.string.scheme_host_kakao_link_road_food_type)
                )
            }

        }
        binding.btnAddReview.setOnClickListener {
            AddReviewDialog.getInstance()
                .show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
        binding.btnAddStoreInfo.setOnClickListener {
            EventTracker.logEvent(Constants.STORE_MODIFY_BTN_CLICKED)
            supportFragmentManager.addNewFragment(
                R.id.container,
                EditStoreDetailFragment(),
                EditStoreDetailFragment::class.java.name,
                false
            )
        }
        binding.addCertificationButton.setOnClickListener {
            EventTracker.logEvent(Constants.STORE_CERTIFICATION_BTN_CLICKED)
            startCertification()
        }
        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.userStoreDetailModel.collect {
//                        updateVisitHistory(it)
//                        reviewAdapter.submitList(it?.reviews)
//                        photoAdapter.submitList(it?.images?.mapIndexed { index, image ->
//                            StoreImage(index, null, image.url)
//                        }?.toMutableList())
//                        initWeekdays(it)

                        startCertificationExactly = if (startCertificationExactly != null) {
                            intent.getBooleanExtra(KEY_START_CERTIFICATION, false)
                        } else {
                            null
                        }

                        if (startCertificationExactly == true) {
                            lifecycleScope.launch {
                                startCertification()
                                startCertificationExactly = null
                            }
                        }
                        initImageView(it)
                        initTextView(it)
                    }
                }
            }
        }
    }

    private fun initImageView(userStoreDetailModel: UserStoreDetailModel) {
        if (userStoreDetailModel.store?.categories?.isNotNullOrEmpty() == true) {
            Glide.with(binding.menuIconImageView)
                .load(userStoreDetailModel.store!!.categories.first().imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.menuIconImageView)
        }

        binding.newImageView.isVisible = userStoreDetailModel.tags?.isNew ?: false
    }

    private fun initTextView(userStoreDetailModel: UserStoreDetailModel) {
        binding.storeNameTextView.text = userStoreDetailModel.store?.name
        binding.creatorTextView.text = getString(R.string.creator, userStoreDetailModel.creator?.name)
        binding.distanceTextView.text =
            if (userStoreDetailModel.distanceM!! < 1000) "${userStoreDetailModel.distanceM}m" else StringUtils.getString(R.string.more_1km)
        binding.reviewTextView.text = getString(R.string.food_truck_review_count, userStoreDetailModel.reviews?.contents?.size)
        binding.favoriteButton.text = userStoreDetailModel.favorite?.totalSubscribersCount.toString()
        binding.bossTextView.text = getString(R.string.last_visit, userStoreDetailModel.visits?.counts?.existsCounts)
        binding.bossTextView.changeTextPartBold(userStoreDetailModel.visits?.counts?.existsCounts.toString())
    }

    private fun clickFavoriteButton() {
        if (viewModel.isFavorite.value == true) {
            viewModel.deleteFavorite(USER_STORE, storeId.toString())
        } else {
            viewModel.putFavorite(USER_STORE, storeId.toString())
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean?) {
        if (isFavorite == null) {
            showToast(R.string.connection_failed)
            return
        }

        val favoriteIcon = if (isFavorite) R.drawable.ic_road_food_favorite_on else R.drawable.ic_road_food_favorite_off

        binding.favoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
        binding.bottomFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
    }

    private fun startCertification() {
        val userStoreModel = viewModel.userStoreDetailModel.value.store

        userStoreModel?.let {
            val distance = NaverMapUtils.calculateDistance(
                naverMapFragment.currentPosition,
                LatLng(userStoreModel.location.latitude, userStoreModel.location.longitude)
            )
            supportFragmentManager.addNewFragment(
                R.id.container,
                if (distance > StoreCertificationAvailableFragment.MIN_DISTANCE) StoreCertificationFragment() else StoreCertificationAvailableFragment(),
                StoreCertificationAvailableFragment::class.java.name,
                false
            )
        }
    }

//    private fun initWeekdays(it: StoreDetail?) {
//        binding.layoutBtnDayOfWeek.tbMon.isChecked =
//            it?.appearanceDays?.contains("MONDAY") == true
//        binding.layoutBtnDayOfWeek.tbTue.isChecked =
//            it?.appearanceDays?.contains("TUESDAY") == true
//        binding.layoutBtnDayOfWeek.tbWen.isChecked =
//            it?.appearanceDays?.contains("WEDNESDAY") == true
//        binding.layoutBtnDayOfWeek.tbThur.isChecked =
//            it?.appearanceDays?.contains("THURSDAY") == true
//        binding.layoutBtnDayOfWeek.tbFri.isChecked =
//            it?.appearanceDays?.contains("FRIDAY") == true
//        binding.layoutBtnDayOfWeek.tbSat.isChecked =
//            it?.appearanceDays?.contains("SATURDAY") == true
//        binding.layoutBtnDayOfWeek.tbSun.isChecked =
//            it?.appearanceDays?.contains("SUNDAY") == true
//    }

//    private fun updateVisitHistory(it: StoreDetail?) {
//        val isExist = it?.visitHistories?.count { history -> history.isExist() } ?: 0
//        val isNotExist = it?.visitHistories?.size?.minus(isExist) ?: 0
//        val hasCertification = isExist + isNotExist > 0
//
//        binding.tvVisitHistory.text = buildSpannedString {
//            append("이번 달 ")
//            bold {
//                if (hasCertification) {
//                    append((isExist + isNotExist).toString())
//                    append("명")
//                } else {
//                    append("방문 인증")
//                }
//            }
//            append(if (hasCertification) "이 다녀간 가게에요!" else " 내역이 없어요 :(")
//        }
//        binding.tvGood.text = "${isExist}명"
//        binding.tvGood.setTextColor(
//            ContextCompat.getColor(
//                this,
//                if (isExist > 0) R.color.green else R.color.gray30
//            )
//        )
//        binding.tvGood.setCompoundDrawablesWithIntrinsicBounds(
//            ContextCompat.getDrawable(
//                this,
//                if (isExist > 0) R.drawable.ic_good else R.drawable.ic_good_off
//            ),
//            null,
//            null,
//            null
//        )
//        binding.tvBad.text = "${isNotExist}명"
//        binding.tvBad.setTextColor(
//            ContextCompat.getColor(
//                this,
//                if (isNotExist > 0) R.color.color_main_red else R.color.gray30
//            )
//        )
//        binding.tvBad.setCompoundDrawablesWithIntrinsicBounds(
//            ContextCompat.getDrawable(
//                this,
//                if (isNotExist > 0) R.drawable.ic_bad else R.drawable.ic_bad_off
//            ),
//            null,
//            null,
//            null
//        )
//        visitHistoryAdapter.submitList(it?.visitHistories)
//
//        binding.ibPlus.setOnClickListener {
//            if (binding.rvVisitHistory.isVisible) {
//                binding.rvVisitHistory.isVisible = false
//                return@setOnClickListener
//            }
//
//            if (visitHistoryAdapter.itemCount > 0) {
//                binding.rvVisitHistory.isVisible = true
//            } else {
//                binding.root.showSnack("아직 인증 내역이 없어요!")
//            }
//        }
//    }

    fun refreshStoreInfo() {
        val storeId = intent.getIntExtra(STORE_ID, 0)

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.getUserStoreDetail(
                            storeId = storeId,
                            deviceLatitude = it.latitude,
                            deviceLongitude = it.longitude,
                            filterVisitStartDate = getMonthFirstDate()
                        )
                    } else {
                        showToast(getString(R.string.exist_location_error))
                        finish()
                    }
                }
            } else {
                showToast(getString(R.string.exist_location_error))
                finish()
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
            showToast(getString(R.string.exist_location_error))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STORE_INFO && resultCode == Activity.RESULT_OK) {
            refreshStoreInfo()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.review_menu, menu)
        val editReviewItem = menu?.getItem(0)
        if (editReviewItem?.itemId == R.id.menu_edit_review) {
            val spannableString = SpannableString(getString(R.string.edit_review))
            spannableString.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.color_main_red
                    )
                ),
                0,
                spannableString.length,
                0
            )
            editReviewItem.title = spannableString
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun getImageFiles(data: List<Uri?>): List<MultipartBody.Part>? {
        val imageList = ArrayList<MultipartBody.Part>()
        data.forEach {
            if (!FileUtils.isAvailable(it)) {
                binding.root.showSnack(R.string.error_file_size)
                return null
            }

            FileUtils.uriToFile(it)?.run {
                val requestFile = asRequestBody("image/*".toMediaType())
                imageList.add(MultipartBody.Part.createFormData("images", name, requestFile))
            }
        }
        return imageList.toList()
    }

    override fun finish() {
        navigateToMainActivityOnCloseIfNeeded()
        super.finish()
    }

    companion object {
        private const val STORE_ID = "storeId"
        private const val KEY_START_CERTIFICATION = "KEY_START_CERTIFICATION"
        private const val EDIT_STORE_INFO = 234

        fun getIntent(
            context: Context,
            storeId: Int? = null,
            startCertification: Boolean = false,
            deepLinkStoreId: String? = null,
        ) =
            Intent(context, StoreDetailActivity::class.java).apply {
                storeId?.let {
                    putExtra(STORE_ID, it)
                }
                deepLinkStoreId?.let {
                    putExtra(STORE_ID, it.toInt())
                }
                putExtra(KEY_START_CERTIFICATION, startCertification)
            }
    }
}