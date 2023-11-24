package com.zion830.threedollars.ui.storeDetail.user.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.*
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.*
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.Constants.USER_STORE
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.ui.dialog.DirectionBottomDialog
import com.zion830.threedollars.ui.write.ui.EditStoreDetailFragment
import com.zion830.threedollars.ui.write.adapter.PhotoRecyclerAdapter
import com.zion830.threedollars.ui.write.adapter.ReviewRecyclerAdapter
import com.home.domain.data.store.StoreImage
import com.zion830.threedollars.ui.map.ui.FullScreenMapActivity
import com.zion830.threedollars.ui.dialog.AddReviewDialog
import com.zion830.threedollars.ui.dialog.DeleteStoreDialog
import com.zion830.threedollars.ui.dialog.ReportReviewDialog
import com.zion830.threedollars.ui.dialog.StorePhotoDialog
import com.zion830.threedollars.ui.storeDetail.user.adapter.UserStoreMenuAdapter
import com.zion830.threedollars.ui.storeDetail.user.adapter.VisitHistoryAdapter
import com.zion830.threedollars.ui.map.ui.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.*
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty

@AndroidEntryPoint
class StoreDetailActivity : BaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>({ ActivityStoreInfoBinding.inflate(it) }) {

    override val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val userStoreMenuAdapter: UserStoreMenuAdapter by lazy {
        UserStoreMenuAdapter {
            val menuGroup = viewModel.userStoreDetailModel.value?.store?.menus?.groupBy { it.category.name }
            userStoreMenuAdapter.submitList(listOf())
            userStoreMenuAdapter.clearCategoryNameList()
            userStoreMenuAdapter.submitList(menuGroup?.flatMap { it.value })
        }
    }

    private val storeId: Int by lazy { intent.getIntExtra(STORE_ID, 0) }

    private var startCertificationExactly: Boolean? = false

    private val photoAdapter: PhotoRecyclerAdapter by lazy {
        PhotoRecyclerAdapter(object : OnItemClickListener<StoreImage> {
            override fun onClick(item: StoreImage) {
                StorePhotoDialog.getInstance(item.index, storeId)
                    .show(supportFragmentManager, StorePhotoDialog::class.java.name)
            }
        }) {
            moveMoreImageActivity()
        }
    }

    private val reviewAdapter: ReviewRecyclerAdapter by lazy {
        ReviewRecyclerAdapter(
            object : OnItemClickListener<ReviewContentModel> {
                override fun onClick(item: ReviewContentModel) {
                    if (item.review.isOwner) {
                        AddReviewDialog.getInstance(item).show(supportFragmentManager, AddReviewDialog::class.java.name)
                    } else {
                        if (item.reviewReport.reportedByMe) {
                            showAlreadyReportDialog()
                        } else {
                            ReportReviewDialog.getInstance(item, storeId).show(supportFragmentManager, ReportReviewDialog::class.java.name)
                        }
                    }
                }
            }, reviewClickListener = {
                activityResultLauncher.launch(StoreReviewDetailActivity.getInstance(this, storeId))
            })
    }

    private val naverMapFragment: StoreDetailNaverMapFragment = StoreDetailNaverMapFragment()

    private val visitHistoryAdapter: VisitHistoryAdapter by lazy {
        VisitHistoryAdapter()
    }

    private var progressDialog: AlertDialog? = null

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                refreshStoreInfo()
            }
        }

        refreshStoreInfo()
        initMap()
        initButton()
        initAdapter()
        initFlows()

        viewModel.addReviewResult.observe(this) {
            viewModel.getUserStoreDetail(
                storeId = storeId,
                deviceLatitude = viewModel.userStoreDetailModel.value?.store?.location?.latitude,
                deviceLongitude = viewModel.userStoreDetailModel.value?.store?.location?.longitude,
                filterVisitStartDate = getMonthFirstDate()
            )
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "StoreDetailActivity")
    }

    private fun initAdapter() {
        binding.reviewRecyclerView.adapter = reviewAdapter
        binding.visitHistoryRecyclerView.adapter = visitHistoryAdapter
        binding.menuRecyclerView.adapter = userStoreMenuAdapter
        binding.photoRecyclerView.adapter = photoAdapter
    }

    private fun initMap() {
        naverMapFragment.setOnMapTouchListener(object : OnMapTouchListener {
            override fun onTouch() {
                // 지도 스크롤 이벤트 구분용
                binding.scroll.requestDisallowInterceptTouchEvent(true)
            }
        })
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()
        naverMapFragment.setIsShowOverlay(false)
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.deleteButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "store_detail")
                putString("store_id", storeId.toString())
            }
            EventTracker.logEvent(Constants.CLICK_REPORT, bundle)
            DeleteStoreDialog.getInstance().show(supportFragmentManager, DeleteStoreDialog::class.java.name)
        }
        binding.photoSummitTextView.setOnClickListener {
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
                lifecycleScope.launch {
                    val images = getImageFiles(uriData)
                    if (images != null) {
                        val bundle = Bundle().apply {
                            putString("screen", "upload_photo")
                            putString("store_id", storeId.toString())
                            putString("count", images.size.toString())
                        }
                        EventTracker.logEvent(Constants.CLICK_UPLOAD, bundle)
                        viewModel.saveImages(images, storeId)
                    }
                }
            }
        }
        binding.shareButton.setOnClickListener {
            initShared()
        }
        binding.writeReviewTextView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "store_detail")
                putString("store_id", storeId.toString())
            }
            EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
            AddReviewDialog.getInstance(storeId = storeId)
                .show(supportFragmentManager, AddReviewDialog::class.java.name)
        }

        binding.reviewButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "store_detail")
                putString("store_id", storeId.toString())
            }
            EventTracker.logEvent(Constants.CLICK_WRITE_REVIEW, bundle)
            AddReviewDialog.getInstance(storeId = storeId)
                .show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
        binding.editStoreInfoButton.setOnClickListener {
            supportFragmentManager.addNewFragment(
                R.id.container,
                EditStoreDetailFragment(),
                EditStoreDetailFragment::class.java.name,
                false
            )
        }
        binding.addCertificationButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "store_detail")
                putString("store_id", storeId.toString())
            }
            EventTracker.logEvent(Constants.CLICK_VISIT, bundle)
            startCertification()
        }
        binding.favoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.bottomFavoriteButton.onSingleClick {
            clickFavoriteButton()
        }
        binding.fullScreenButton.setOnClickListener {
            moveFullScreenMap()
        }
        binding.addressTextView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "store_detail")
                putString("store_id", storeId.toString())
            }
            EventTracker.logEvent(Constants.CLICK_COPY_ADDRESS, bundle)
            val manager = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            manager.text = binding.addressTextView.text
            showToast("주소가 복사됐습니다.")
        }
        binding.directionsButton.setOnClickListener {
            showDirectionBottomDialog()
        }
    }

    private fun initShared() {
        val bundle = Bundle().apply {
            putString("screen", "store_detail")
            putString("store_id", storeId.toString())
        }
        EventTracker.logEvent(Constants.CLICK_SHARE, bundle)
        val userStoreModel = viewModel.userStoreDetailModel.value?.store

        val shareFormat = ShareFormat(
            getString(R.string.kakao_map_format),
            binding.storeNameTextView.text.toString(),
            LatLng(userStoreModel?.location?.latitude ?: 0.0, userStoreModel?.location?.longitude ?: 0.0)
        )
        shareWithKakao(
            shareFormat = shareFormat,
            title = getString(
                R.string.share_kakao_road_food_title,
                userStoreModel?.name
            ),
            description = getString(
                R.string.share_kakao_road_food,
                userStoreModel?.name
            ),
            imageUrl = "https://storage.threedollars.co.kr/share/share-with-kakao.png",
            storeId = storeId.toString(),
            type = getString(R.string.scheme_host_kakao_link_road_food_type)
        )
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.userStoreDetailModel.collect {
                        it?.let {
                            initReviewLayout(it)
                            initPhotoLayout(it)
                            initMap(it)
                            isStartCertification()
                            initImageView(it)
                            initTextView(it)
                            initVisitHistory(it.visits)
                            initMenu(it.store.menus)
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
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }

                launch {
                    viewModel.photoDeleted.collect {
                        if (it) {
                            viewModel.getUserStoreDetail(
                                storeId = storeId,
                                deviceLatitude = viewModel.userStoreDetailModel.value?.store?.location?.latitude,
                                deviceLongitude = viewModel.userStoreDetailModel.value?.store?.location?.longitude,
                                filterVisitStartDate = getMonthFirstDate()
                            )
                        } else {
                            binding.root.showSnack(getString(R.string.delete_photo_failed))
                        }
                    }
                }

                launch {
                    viewModel.uploadImageStatus.collect {
                        if (it) {
                            if (progressDialog == null) {
                                progressDialog = AlertDialog.Builder(this@StoreDetailActivity)
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
                }
                launch {
                    viewModel.isDeleteStore.collect {
                        if (it) {
                            finish()
                        }
                    }
                }
                launch {
                    viewModel.reviewSuccessEvent.collect{
                        if(it) {
                            refreshStoreInfo()
                        }
                    }
                }
            }
        }
    }

    private fun initReviewLayout(it: UserStoreDetailModel) {
        val reviewContentModelList = it.reviews.contents

        if (reviewContentModelList.isEmpty()) {
            reviewAdapter.submitList(listOf(UserStoreDetailEmptyItem(getString(R.string.review_empty))))
        } else if (reviewContentModelList.size < 4) {
            reviewAdapter.submitList(reviewContentModelList)
        } else {
            val subList = reviewContentModelList.take(3)
            val userStoreMoreResponse = UserStoreMoreResponse(
                moreTitle = getString(R.string.store_detail_review_more, reviewContentModelList.size - 3)
            )
            reviewAdapter.submitList(subList + userStoreMoreResponse)
        }
    }

    private fun initPhotoLayout(it: UserStoreDetailModel) {
        val imageContentModelList = it.images.contents
        if (imageContentModelList.isEmpty()) {
            photoAdapter.submitList(listOf(UserStoreDetailEmptyItem(getString(R.string.photo_empty))))
        } else {
            photoAdapter.submitList(listOf())
            photoAdapter.submitList(it.images.contents.mapIndexed { index, image ->
                StoreImage(index, null, image.url)
            }.toList())
        }
    }

    private fun initMap(it: UserStoreDetailModel) {
        val latLng = LatLng(it.store.location.latitude, it.store.location.longitude)
        naverMapFragment.initMap(latLng)
    }

    private fun isStartCertification() {
        startCertificationExactly = if (startCertificationExactly != null) {
            intent.getBooleanExtra(KEY_START_CERTIFICATION, false)
        } else {
            null
        }
        if (startCertificationExactly == true) {
            startCertification()
            startCertificationExactly = null
        }
    }

    private fun initMenu(menuModel: List<UserStoreMenuModel>) {
        val menuGroup = menuModel.groupBy { it.category.name }
        if (menuGroup.size < 3) {
            userStoreMenuAdapter.submitList(listOf())
            userStoreMenuAdapter.clearCategoryNameList()
            userStoreMenuAdapter.submitList(menuGroup.flatMap { it.value })
        } else {
            val subKeys = menuGroup.keys.take(2)
            val subMenuGroup = menuGroup.getValue(subKeys[0]) + menuGroup.getValue(subKeys[1])
            val userStoreMoreResponse = UserStoreMoreResponse(
                moreTitle = getString(R.string.store_detail_menu_more, menuGroup.keys.size - 2)
            )
            lifecycleScope.launch {
                userStoreMenuAdapter.submitList(listOf())
                userStoreMenuAdapter.clearCategoryNameList()
                delay(5L)
                userStoreMenuAdapter.submitList(subMenuGroup + userStoreMoreResponse)
            }
        }
    }

    private fun initVisitHistory(visits: VisitsModel?) {
        if (visits == null) {
            binding.visitHistoryTitleTextView.text = getString(R.string.visit_history_empty_title)
            binding.smileTextView.apply {
                text = getString(R.string.visit_history_success, 0)
                textPartTypeface("0명", Typeface.BOLD)
            }
            binding.sadTextView.apply {
                text = getString(R.string.visit_history_fail, 0)
                textPartTypeface("0명", Typeface.BOLD)
            }
        } else {
            binding.visitHistoryTitleTextView.text =
                if (visits.counts.isCertified) getString(R.string.visit_history_title) else getString(R.string.visit_history_empty_title)
            binding.smileTextView.apply {
                text = getString(R.string.visit_history_success, visits.counts.existsCounts)
                textPartTypeface("${visits.counts.existsCounts}명", Typeface.BOLD)
            }
            binding.sadTextView.apply {
                text = getString(R.string.visit_history_fail, visits.counts.notExistsCounts)
                textPartTypeface("${visits.counts.notExistsCounts}명", Typeface.BOLD)
            }
            val historiesContentModelList = visits.histories.contents
            if (historiesContentModelList.isNotNullOrEmpty()) {
                if (historiesContentModelList.size > 5) {
                    visitHistoryAdapter.submitList(historiesContentModelList.subList(0, 5))
                    binding.visitExtraTextView.apply {
                        isVisible = true
                        text = getString(R.string.visit_extra, visits.counts.existsCounts + visits.counts.notExistsCounts - 5)
                    }
                } else {
                    visitHistoryAdapter.submitList(historiesContentModelList)
                }
                binding.visitHistoryCardView.isVisible = true
            }
        }
    }

    private fun initImageView(userStoreDetailModel: UserStoreDetailModel) {
        if (userStoreDetailModel.store.categories.isNotNullOrEmpty()) {
            binding.menuIconImageView.loadImage(userStoreDetailModel.store.categories.first().imageUrl)
        }

        binding.newImageView.isVisible = userStoreDetailModel.tags.isNew
    }

    private fun initTextView(userStoreDetailModel: UserStoreDetailModel) {
        binding.storeNameTextView.text = userStoreDetailModel.store.name
        binding.creatorTextView.text = getString(R.string.creator, userStoreDetailModel.creator.name)
        binding.distanceTextView.text =
            if (userStoreDetailModel.distanceM < 1000) "${userStoreDetailModel.distanceM}m" else StringUtils.getString(R.string.more_1km)
        binding.reviewTextView.text = getString(R.string.food_truck_review_count, userStoreDetailModel.reviews.contents.size)
        binding.favoriteButton.text = userStoreDetailModel.favorite.totalSubscribersCount.toString()
        binding.visitTextView.text = getString(R.string.last_visit, userStoreDetailModel.visits.counts.existsCounts)
        binding.visitTextView.textPartTypeface(userStoreDetailModel.visits.counts.existsCounts.toString(), Typeface.BOLD)
        binding.addressTextView.text = userStoreDetailModel.store.address.fullAddress
        binding.storeInfoUpdatedAtTextView.text = userStoreDetailModel.store.updatedAt.convertUpdateAt(this)
        binding.storeTypeTextView.text = userStoreDetailModel.store.salesType.title
        binding.reviewTitleTextView.text = getString(R.string.review_count, userStoreDetailModel.reviews.contents.size)
        binding.reviewTitleTextView.textPartTypeface("${userStoreDetailModel.reviews.contents.size}개", Typeface.NORMAL)
        binding.reviewRatingAvgTextView.text = getString(R.string.score, userStoreDetailModel.store.rating)
        binding.reviewRatingBar.rating = userStoreDetailModel.store.rating.toFloat()
        initPayments(userStoreDetailModel.store.paymentMethods)
        initAppearanceDays(userStoreDetailModel.store.appearanceDays)
    }

    private fun initPayments(paymentTypeList: List<PaymentType>) {
        paymentTypeList.forEach {
            when (it) {
                PaymentType.CARD -> {
                    initPayment(binding.cardTextView)
                }

                PaymentType.CASH -> {
                    initPayment(binding.cashTextView)
                }

                PaymentType.ACCOUNT_TRANSFER -> {
                    initPayment(binding.bankingTextView)
                }
            }
        }
    }

    private fun initPayment(textView: TextView) {
        textView.apply {
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this@StoreDetailActivity, R.drawable.circle_gray70_4dp), null, null, null
            )
            setTextColor(getColor(R.color.gray70))
        }
    }

    private fun initAppearanceDays(dayOfTheWeekTypeList: List<DayOfTheWeekType>) {
        clearAppearanceDay()
        dayOfTheWeekTypeList.forEach {
            when (it) {
                DayOfTheWeekType.MONDAY -> {
                    initAppearanceDay(binding.mondayTextView)
                }

                DayOfTheWeekType.TUESDAY -> {
                    initAppearanceDay(binding.tuesdayTextView)
                }

                DayOfTheWeekType.WEDNESDAY -> {
                    initAppearanceDay(binding.wednesdayTextView)
                }

                DayOfTheWeekType.THURSDAY -> {
                    initAppearanceDay(binding.thursdayTextView)
                }

                DayOfTheWeekType.FRIDAY -> {
                    initAppearanceDay(binding.fridayTextView)
                }

                DayOfTheWeekType.SATURDAY -> {
                    initAppearanceDay(binding.saturdayTextView)
                }

                DayOfTheWeekType.SUNDAY -> {
                    initAppearanceDay(binding.sundayTextView)
                }
            }
        }
    }

    private fun clearAppearanceDay() {
        binding.run {
            listOf(mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView, fridayTextView, saturdayTextView, sundayTextView).forEach {
                it.apply {
                    setBackgroundResource(R.drawable.circle_gray10_24dp)
                    setTextColor(getColor(R.color.gray40))
                }
            }
        }
    }

    private fun initAppearanceDay(textView: TextView) {
        textView.apply {
            setBackgroundResource(R.drawable.circle_gray70_24dp)
            setTextColor(getColor(R.color.white))
        }
    }

    private fun clickFavoriteButton() {
        val bundle = Bundle().apply {
            putString("screen", "store_detail")
            putString("store_id", storeId.toString())
            putString("value", if (viewModel.favoriteModel.value.isFavorite) "on" else "false")
        }
        EventTracker.logEvent(Constants.CLICK_FAVORITE, bundle)
        if (viewModel.favoriteModel.value.isFavorite) {
            viewModel.deleteFavorite(USER_STORE, storeId.toString())
        } else {
            viewModel.putFavorite(USER_STORE, storeId.toString())
        }
    }

    private fun setFavoriteIcon(isFavorite: Boolean) {
        val favoriteIcon = if (isFavorite) R.drawable.ic_food_truck_favorite_on else R.drawable.ic_food_truck_favorite_off

        binding.favoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, favoriteIcon, 0, 0)
        binding.bottomFavoriteButton.setCompoundDrawablesRelativeWithIntrinsicBounds(favoriteIcon, 0, 0, 0)
    }

    private fun startCertification() {
        val userStoreModel = viewModel.userStoreDetailModel.value?.store

        val distance = NaverMapUtils.calculateDistance(
            naverMapFragment.currentPosition,
            LatLng(userStoreModel?.location?.latitude ?: 0.0, userStoreModel?.location?.longitude ?: 0.0)
        )
        supportFragmentManager.addNewFragment(
            R.id.container,
            if (distance > StoreCertificationAvailableFragment.MIN_DISTANCE) StoreCertificationFragment.getInstance(userStoreModel) else StoreCertificationAvailableFragment.getInstance(
                userStoreModel
            ),
            StoreCertificationAvailableFragment::class.java.name,
            false
        )
    }

    private fun showDirectionBottomDialog() {
        val bundle = Bundle().apply {
            putString("screen", "store_detail")
            putString("store_id", storeId.toString())
        }
        EventTracker.logEvent(Constants.CLICK_NAVIGATION, bundle)
        val store = viewModel.userStoreDetailModel.value?.store
        DirectionBottomDialog.getInstance(store?.location?.latitude, store?.location?.longitude, store?.name).show(supportFragmentManager, "")
    }

    private fun moveFullScreenMap() {
        val bundle = Bundle().apply {
            putString("screen", "store_detail")
            putString("store_id", storeId.toString())
        }
        EventTracker.logEvent(Constants.CLICK_ZOOM_MAP, bundle)
        val store = viewModel.userStoreDetailModel.value?.store
        val intent = FullScreenMapActivity.getIntent(
            context = this,
            latitude = store?.location?.latitude,
            longitude = store?.location?.longitude,
            name = store?.name,
        )
        startActivity(intent)
    }

    private fun moveMoreImageActivity() {
        val intent = MoreImageActivity.getIntent(this, storeId)
        activityResultLauncher.launch(intent)
    }

    private fun refreshStoreInfo() {
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

    private fun showAlreadyReportDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("신고")
        builder.setMessage("이미 신고한 댓글입니다!")

        builder.setPositiveButton("확인") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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