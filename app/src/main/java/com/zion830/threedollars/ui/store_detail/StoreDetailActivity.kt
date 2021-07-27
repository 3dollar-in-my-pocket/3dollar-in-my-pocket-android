package com.zion830.threedollars.ui.store_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityStoreInfoBinding
import com.zion830.threedollars.repository.model.response.Review
import com.zion830.threedollars.ui.addstore.EditStoreDetailFragment
import com.zion830.threedollars.ui.addstore.adapter.PhotoRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.ReviewRecyclerAdapter
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import com.zion830.threedollars.ui.report_store.AddReviewDialog
import com.zion830.threedollars.ui.report_store.DeleteStoreDialog
import com.zion830.threedollars.ui.report_store.StorePhotoDialog
import com.zion830.threedollars.ui.store_detail.adapter.CategoryInfoRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.map.StoreDetailNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import com.zion830.threedollars.utils.*
import gun0912.tedimagepicker.builder.TedImagePicker
import io.hackle.android.HackleApp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import zion830.com.common.base.BaseActivity
import zion830.com.common.ext.addNewFragment
import zion830.com.common.listener.OnItemClickListener


class StoreDetailActivity :
    BaseActivity<ActivityStoreInfoBinding, StoreDetailViewModel>(R.layout.activity_store_info),
    OnMapTouchListener {

    override val viewModel: StoreDetailViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val categoryAdapter = CategoryInfoRecyclerAdapter()

    private var currentPosition: LatLng = NaverMapUtils.DEFAULT_LOCATION

    private var storeId = 0

    private lateinit var photoAdapter: PhotoRecyclerAdapter

    private lateinit var reviewAdapter: ReviewRecyclerAdapter

    override fun onTouch() {
        // 지도 스크롤 이벤트 구분용
        binding.scroll.requestDisallowInterceptTouchEvent(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        val hackleApp = HackleApp.getInstance()
        val adRequest: AdRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        storeId = intent.getIntExtra(KEY_STORE_ID, 0)
        reviewAdapter = ReviewRecyclerAdapter(
            object : OnItemClickListener<Review> {
                override fun onClick(item: Review) {
                    AddReviewDialog.getInstance(item)
                        .show(supportFragmentManager, AddReviewDialog::class.java.name)
                }
            },
            object : OnItemClickListener<Review> {
                override fun onClick(item: Review) {
                    viewModel.deleteReview(item.id)
                }
            },
        )
        photoAdapter = PhotoRecyclerAdapter(object : OnItemClickListener<StoreImage> {
            override fun onClick(item: StoreImage) {
                StorePhotoDialog().show(supportFragmentManager, StorePhotoDialog::class.java.name)
            }
        })

        val naverMapFragment = StoreDetailNaverMapFragment()
        supportFragmentManager.beginTransaction().replace(R.id.map, naverMapFragment).commit()

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnDelete.setOnClickListener {
            binding.btnDelete.setOnClickListener {
                DeleteStoreDialog.getInstance()
                    .show(supportFragmentManager, DeleteStoreDialog::class.java.name)
            }
        }
        binding.btnAddPhoto.setOnClickListener {
            TedImagePicker.with(this).zoomIndicator(false).startMultiImage {

            }
        }
        binding.btnSendMoney.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.toss_scheme)))
            startActivity(browserIntent)
            hackleApp.track(Constants.TOSS_BTN_CLICKED)
        }
        binding.btnShare.setOnClickListener {
            val shareFormat = ShareFormat(
                getString(R.string.kakao_map_format),
                binding.tvStoreName.text.toString(),
                viewModel.selectedLocation.value ?: viewModel.storeLocation.value
            )
            shareWithKakao(shareFormat)
        }
        binding.rvPhoto.adapter = photoAdapter
        binding.rvCategory.adapter = categoryAdapter
        binding.rvReview.adapter = reviewAdapter
        binding.btnAddReview.setOnClickListener {
            AddReviewDialog.getInstance()
                .show(supportFragmentManager, AddReviewDialog::class.java.name)
        }
        binding.btnAddStoreInfo.setOnClickListener {
            supportFragmentManager.addNewFragment(
                R.id.container,
                EditStoreDetailFragment(),
                EditStoreDetailFragment::class.java.name,
                false
            )
        }
        viewModel.addReviewResult.observe(this) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
        }
        viewModel.needRefresh.observe(this) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
        }
        viewModel.storeInfo.observe(this) {
            binding.tvStoreType.isVisible = it?.storeType == null
            binding.tvEmptyStoreType.isVisible = it?.storeType == null
            reviewAdapter.submitList(it?.review)
            photoAdapter.submitList(it?.image?.mapIndexed { index, image ->
                StoreImage(
                    index,
                    null,
                    image.url
                )
            }?.toMutableList())

            // TODO : 이게 최선이야??
            when {
                it?.appearanceDays?.contains("MONDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbMon.toggle()
                }
                it?.appearanceDays?.contains("TUESDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbTue.toggle()
                }
                it?.appearanceDays?.contains("WEDNESDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbWen.toggle()
                }
                it?.appearanceDays?.contains("THURSDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbThur.toggle()
                }
                it?.appearanceDays?.contains("FRIDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbFri.toggle()
                }
                it?.appearanceDays?.contains("SATURDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbSat.toggle()
                }
                it?.appearanceDays?.contains("SUNDAY") == true -> {
                    binding.layoutBtnDayOfWeek.tbSun.toggle()
                }
            }
        }
        viewModel.categoryInfo.observe(this) {
            categoryAdapter.submitList(it)
        }
        viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
    }

    private fun initMap() {
        val storeId = intent.getIntExtra(KEY_STORE_ID, 0)

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        viewModel.requestStoreInfo(storeId, it.latitude, it.longitude)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STORE_INFO && resultCode == Activity.RESULT_OK) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
            initMap()
        } else if (requestCode == PICK_IMAGE_MULTIPLE) {
            if (data?.data == null) {
                showToast(R.string.error_pick_image)
                return
            }

            val clipData = data.clipData
            val uriData = arrayListOf<Uri?>()
            for (i in 0 until (clipData?.itemCount ?: 0)) {
                if (!FileUtils.isAvailable(clipData?.getItemAt(i)?.uri)) {
                    showToast(R.string.error_file_size)
                    return
                } else {
                    uriData.add(clipData?.getItemAt(i)?.uri)
                }
            }

            viewModel.saveImages(viewModel.storeInfo.value?.id, getImageFiles(uriData))
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

    private fun getImageFiles(data: List<Uri?>): List<MultipartBody.Part> {
        val imageList = ArrayList<MultipartBody.Part>()
        data.forEach {
            FileUtils.uriToFile(it)?.run {
                val requestFile = asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageList.add(MultipartBody.Part.createFormData("image", name, requestFile))
            }
        }
        return imageList.toList()
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        private const val EDIT_STORE_INFO = 234
        private const val PICK_IMAGE_MULTIPLE = 235

        fun getIntent(context: Context, storeId: Int) =
            Intent(context, StoreDetailActivity::class.java).apply {
                putExtra(KEY_STORE_ID, storeId)
            }
    }
}