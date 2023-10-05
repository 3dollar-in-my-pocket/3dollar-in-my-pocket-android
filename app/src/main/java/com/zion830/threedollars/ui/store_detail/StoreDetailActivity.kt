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
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.USER_STORE
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.datasource.model.v2.response.my.Review
import com.zion830.threedollars.datasource.model.v2.response.store.StoreDetail
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
import zion830.com.common.base.LegacyBaseActivity
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.showSnack
import com.threedollar.common.ext.toFormattedNumber

@AndroidEntryPoint
class StoreDetailActivity :
    LegacyBaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>(R.layout.activity_store_info) {

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

        EventTracker.logEvent(Constants.STORE_DELETE_BTN_CLICKED)
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getIntExtra(STORE_ID, 0)
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
        binding.btnShare.setOnClickListener {
            EventTracker.logEvent(Constants.SHARE_BTN_CLICKED)
            val shareFormat = ShareFormat(
                getString(R.string.kakao_map_format),
                binding.tvStoreName.text.toString(),
                viewModel.selectedLocation.value ?: viewModel.storeLocation.value
            )
            shareWithKakao(
                shareFormat = shareFormat,
                title = getString(
                    R.string.share_kakao_road_food_title,
                    viewModel.storeInfo.value?.storeName
                ),
                description = getString(
                    R.string.share_kakao_road_food,
                    viewModel.storeInfo.value?.storeName
                ),
                imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
                storeId = storeId.toString(),
                type = getString(R.string.scheme_host_kakao_link_road_food_type)
            )
        }
        binding.rvPhoto.adapter = photoAdapter
        binding.rvCategory.adapter = categoryAdapter
        binding.rvReview.adapter = reviewAdapter
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
        binding.btnCertification.setOnClickListener {
            EventTracker.logEvent(Constants.STORE_CERTIFICATION_BTN_CLICKED)
            startCertification()
        }
        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        viewModel.addReviewResult.observe(this) {
            EventTracker.logEvent(Constants.REVIEW_WRITE_BTN_CLICKED)
            viewModel.requestStoreInfo(
                storeId,
                viewModel.storeInfo.value?.latitude,
                viewModel.storeInfo.value?.longitude
            )
        }
        viewModel.closeActivity.observe(this) {
            if (it) {
                finish()
            }
        }
        viewModel.photoDeleted.observe(this) {
            if (it) {
                viewModel.requestStoreInfo(
                    storeId,
                    viewModel.storeLocation.value?.latitude,
                    viewModel.storeLocation.value?.latitude
                )
            } else {
                binding.layoutTitle.showSnack(getString(R.string.delete_photo_failed))
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
        viewModel.storeInfo.observe(this) {
            initStoreInfo(it)
            updateVisitHistory(it)

            reviewAdapter.submitList(it?.reviews)
            photoAdapter.submitList(it?.images?.mapIndexed { index, image ->
                StoreImage(index, null, image.url)
            }?.toMutableList())
            initWeekdays(it)

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
        }
        viewModel.categoryInfo.observe(this) {
            categoryAdapter.submitList(it)
        }
        viewModel.isFavorite.observe(this) {
            setFavoriteIcon(it)
        }
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
        naverMapFragment.updateCurrentLocation {
            if (it == null) {
                return@updateCurrentLocation
            }
            val distance = NaverMapUtils.calculateDistance(
                naverMapFragment.currentPosition,
                viewModel.storeLocation.value
            )
            supportFragmentManager.addNewFragment(
                R.id.container,
                if (distance > StoreCertificationAvailableFragment.MIN_DISTANCE) StoreCertificationFragment() else StoreCertificationAvailableFragment(),
                StoreCertificationAvailableFragment::class.java.name,
                false
            )
        }
    }

    private fun initWeekdays(it: StoreDetail?) {
        binding.layoutBtnDayOfWeek.tbMon.isChecked =
            it?.appearanceDays?.contains("MONDAY") == true
        binding.layoutBtnDayOfWeek.tbTue.isChecked =
            it?.appearanceDays?.contains("TUESDAY") == true
        binding.layoutBtnDayOfWeek.tbWen.isChecked =
            it?.appearanceDays?.contains("WEDNESDAY") == true
        binding.layoutBtnDayOfWeek.tbThur.isChecked =
            it?.appearanceDays?.contains("THURSDAY") == true
        binding.layoutBtnDayOfWeek.tbFri.isChecked =
            it?.appearanceDays?.contains("FRIDAY") == true
        binding.layoutBtnDayOfWeek.tbSat.isChecked =
            it?.appearanceDays?.contains("SATURDAY") == true
        binding.layoutBtnDayOfWeek.tbSun.isChecked =
            it?.appearanceDays?.contains("SUNDAY") == true
    }

    private fun updateVisitHistory(it: StoreDetail?) {
        val isExist = it?.visitHistories?.count { history -> history.isExist() } ?: 0
        val isNotExist = it?.visitHistories?.size?.minus(isExist) ?: 0
        val hasCertification = isExist + isNotExist > 0

        binding.tvVisitHistory.text = buildSpannedString {
            append("이번 달 ")
            bold {
                if (hasCertification) {
                    append((isExist + isNotExist).toString())
                    append("명")
                } else {
                    append("방문 인증")
                }
            }
            append(if (hasCertification) "이 다녀간 가게에요!" else " 내역이 없어요 :(")
        }
        binding.tvGood.text = "${isExist}명"
        binding.tvGood.setTextColor(
            ContextCompat.getColor(
                this,
                if (isExist > 0) R.color.green else R.color.gray30
            )
        )
        binding.tvGood.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(
                this,
                if (isExist > 0) R.drawable.ic_good else R.drawable.ic_good_off
            ),
            null,
            null,
            null
        )
        binding.tvBad.text = "${isNotExist}명"
        binding.tvBad.setTextColor(
            ContextCompat.getColor(
                this,
                if (isNotExist > 0) R.color.color_main_red else R.color.gray30
            )
        )
        binding.tvBad.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(
                this,
                if (isNotExist > 0) R.drawable.ic_bad else R.drawable.ic_bad_off
            ),
            null,
            null,
            null
        )
        visitHistoryAdapter.submitList(it?.visitHistories)

        binding.ibPlus.setOnClickListener {
            if (binding.rvVisitHistory.isVisible) {
                binding.rvVisitHistory.isVisible = false
                return@setOnClickListener
            }

            if (visitHistoryAdapter.itemCount > 0) {
                binding.rvVisitHistory.isVisible = true
            } else {
                binding.root.showSnack("아직 인증 내역이 없어요!")
            }
        }
    }

    private fun initStoreInfo(it: StoreDetail?) {
        if (it != null) {
            val distance = it.distance
            val updatedAt = "${
                StringUtils.getTimeString(
                    it.updatedAt,
                    "yy.MM.dd"
                )
            } ${getString(R.string.updated_at)}"

            binding.tvDistance.text = "${distance.toString().toFormattedNumber()}m"
            binding.tvStoreType.isVisible = true
            binding.tvEmptyStoreType.isVisible = false
            binding.tvUpdatedAt.isVisible = !it.updatedAt.isNullOrBlank()
            binding.tvUpdatedAt.text = updatedAt
        }
    }

    fun refreshStoreInfo() {
        val storeId = intent.getIntExtra(STORE_ID, 0)

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.requestStoreInfo(storeId, it.latitude, it.longitude)
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
                binding.tvStoreName.showSnack(R.string.error_file_size)
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
            deepLinkStoreId: String? = null
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