package com.zion830.threedollars.ui.storeDetail.boss.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.network.data.feedback.FeedbackTypeResponse
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFoodTruckReviewBinding
import com.zion830.threedollars.ui.storeDetail.boss.adapter.BossReviewSummitRecyclerAdapter
import com.zion830.threedollars.ui.storeDetail.boss.adapter.PhotoAdapter
import com.zion830.threedollars.ui.storeDetail.boss.adapter.PhotoItem
import com.zion830.threedollars.ui.storeDetail.boss.viewModel.BossStoreDetailViewModel
import com.zion830.threedollars.ui.dialog.LoadingProgressDialog
import com.zion830.threedollars.utils.GridItemDecoration
import com.zion830.threedollars.utils.ImageUtils
import com.zion830.threedollars.utils.goToPermissionSetting
import com.zion830.threedollars.utils.showCustomBlackToast
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import javax.inject.Inject
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class BossReviewWriteActivity :
    BaseActivity<ActivityFoodTruckReviewBinding, BossStoreDetailViewModel>({ ActivityFoodTruckReviewBinding.inflate(it) }) {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: BossStoreDetailViewModel by viewModels()

    private val bossReviewSummitRecyclerAdapter: BossReviewSummitRecyclerAdapter by lazy {
        BossReviewSummitRecyclerAdapter(
            onClickAction = {
                if (selectReviewSet.contains(it.feedbackType)) {
                    selectReviewSet.remove(it.feedbackType)
                } else {
                    selectReviewSet.add(it.feedbackType)
                }
                updateSubmitButtonState()
            },
            selectedItems = selectReviewSet
        )
    }

    private val photoAdapter: PhotoAdapter by lazy {
        PhotoAdapter(
            onAddPhotoClick = { selectPhotos() },
            onDeleteClick = { position -> removePhoto(position) }
        )
    }

    private var selectReviewSet = mutableSetOf<String>()
    private var selectedImageUris = mutableListOf<Uri>()
    private var currentRating = 0
    private var storeId = ""
    private var loadingDialog: LoadingProgressDialog? = null

    override fun initView() {
        setLightSystemBars()

        storeId = intent.getStringExtra(KEY_STORE_ID).toString()

        binding.btnBack.onSingleClick {
            val intent = BossStoreDetailActivity.getIntent(this, storeId)
            startActivity(intent)
            finish()
        }

        binding.btnSubmit.onSingleClick {
            if (selectReviewSet.isEmpty()) {
                showToast(getString(CommonR.string.boss_review_feedback_required))
            } else if (currentRating == 0) {
                showToast(getString(CommonR.string.boss_review_rating_required))
            } else if (binding.etReview.text.toString().trim().isEmpty()) {
                showToast(getString(CommonR.string.boss_review_content_required))
            } else {
                viewModel.sendClickWriteReviewSubmit()
                submitReview()
            }
        }
        
        binding.ratingBar.setOnRatingChangeListener { _, rating, _ ->
            currentRating = rating.toInt()
            updateSubmitButtonState()
        }

        binding.etReview.addTextChangedListener {
            updateSubmitButtonState()
        }
        bossReviewSummitRecyclerAdapter.submitList(sharedPrefUtils.getList<FeedbackTypeResponse>(SharedPrefUtils.BOSS_FEED_BACK_LIST))
        binding.rvFeedbackOptions.adapter = bossReviewSummitRecyclerAdapter
        
        val spacingVertical = (8 * resources.displayMetrics.density).toInt()
        val spacingHorizontal = (7 * resources.displayMetrics.density).toInt()
        binding.rvFeedbackOptions.addItemDecoration(
            GridItemDecoration(spacingVertical, spacingHorizontal, includeEdge = false)
        )

        setupPhotoRecyclerView()
        iniFlows()
        updateSubmitButtonState()
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.BOSS_STORE_REVIEW_WRITE, this::class.java.simpleName)
    }

    private fun iniFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.postStoreReview.collect {
                        hideLoadingDialog()
                        if (it?.ok == true) {
                            showCustomBlackToast(getString(CommonR.string.review_toast))
                            finish()
                        }
                    }
                }
                launch {
                    viewModel.uploadImageStatus.collect { isUploading ->
                        updateSubmitButtonState()
                    }
                }
                launch {
                    viewModel.uploadProgress.collect { progress ->
                        progress?.let { (current, total) ->
                            showImageUploadProgress(current, total)
                        }
                    }
                }
                launch {
                    viewModel.uploadedImages.collect { uploadedImages ->
                        uploadedImages?.let {
                            showReviewSubmitProgress()
                            viewModel.postStoreReview(
                                storeId = storeId,
                                contents = binding.etReview.text.toString().trim(),
                                rating = currentRating,
                                images = it,
                                feedbacks = selectReviewSet.toList()
                            )
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        hideLoadingDialog()
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    private fun hasRequiredData(): Boolean {
        return selectReviewSet.isNotEmpty() &&
            currentRating > 0 &&
            binding.etReview.text.toString().trim().isNotEmpty()
    }
    
    private fun updateSubmitButtonState() {
        val isUploading = viewModel.uploadImageStatus.value
        val hasRequired = hasRequiredData()
        
        binding.btnSubmit.isEnabled = hasRequired && !isUploading
        binding.btnSubmit.alpha = if (binding.btnSubmit.isEnabled) 1.0f else 0.5f
    }

    private fun setupPhotoRecyclerView() {
        binding.rvPhotos.apply {
            layoutManager = LinearLayoutManager(this@BossReviewWriteActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }
        updatePhotoList()
    }

    private fun selectPhotos() {
        val remainingCount = MAX_IMAGE_COUNT - selectedImageUris.size
        
        if (remainingCount <= 0) {
            showToast(getString(CommonR.string.boss_review_max_images_error, MAX_IMAGE_COUNT))
            return
        }

        TedImagePicker.with(this)
            .max(remainingCount, "최대 ${remainingCount}장까지 선택할 수 있습니다.")
            .zoomIndicator(false)
            .errorListener { error ->
                if (error.message?.startsWith("permission") == true) {
                    showPermissionDialog()
                } else {
                    showToast(getString(CommonR.string.boss_review_image_selection_error))
                }
            }
            .startMultiImage { uriList ->
                addPhotos(uriList)
            }
    }

    private fun addPhotos(uriList: List<Uri>) {
        val invalidUris = uriList.filter { !ImageUtils.isFileSizeValid(this, it) }
        if (invalidUris.isNotEmpty()) {
            showToast(getString(CommonR.string.boss_review_image_size_error))
            return
        }

        selectedImageUris.addAll(uriList)
        updatePhotoList()
    }

    private fun removePhoto(position: Int) {
        if (position < selectedImageUris.size) {
            selectedImageUris.removeAt(position)
            updatePhotoList()
        }
    }

    private fun updatePhotoList() {
        val photoItems = mutableListOf<PhotoItem>()
        
        // 사진 추가 버튼은 항상 표시하되, 최대 개수 도달 시 비활성화
        val isAddButtonEnabled = selectedImageUris.size < MAX_IMAGE_COUNT
        photoItems.add(
            PhotoItem.AddPhoto(
                isEnabled = isAddButtonEnabled,
                currentCount = selectedImageUris.size,
                maxCount = MAX_IMAGE_COUNT
            )
        )
        
        selectedImageUris.forEachIndexed { index, uri ->
            photoItems.add(PhotoItem.SelectedPhoto(uri, index))
        }
        
        photoAdapter.submitList(photoItems)
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(CommonR.string.request_permission))
            .setMessage(getString(CommonR.string.request_permission_msg))
            .setPositiveButton(getString(CommonR.string.request_permission_ok)) { _, _ ->
                goToPermissionSetting()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    private fun submitReview() {
        if (selectedImageUris.isNotEmpty()) {
            // 이미지가 있는 경우: 먼저 이미지 업로드 후 리뷰 등록
            showImageUploadProgress(0, selectedImageUris.size)
            viewModel.uploadImages(this, selectedImageUris)
        } else {
            // 이미지가 없는 경우: 바로 리뷰 등록
            showReviewSubmitProgress()
            viewModel.postStoreReview(
                storeId = storeId,
                contents = binding.etReview.text.toString().trim(),
                rating = currentRating,
                images = emptyList(),
                feedbacks = selectReviewSet.toList()
            )
        }
    }

    private fun showImageUploadProgress(current: Int, total: Int) {
        if (loadingDialog == null) {
            loadingDialog = LoadingProgressDialog(this)
        }
        loadingDialog?.setHorizontalMode(
            getString(CommonR.string.boss_review_uploading_images),
            current,
            total
        )
        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun showReviewSubmitProgress() {
        if (loadingDialog == null) {
            loadingDialog = LoadingProgressDialog(this)
        }
        loadingDialog?.setCircularMode(getString(CommonR.string.boss_review_submitting))
        if (loadingDialog?.isShowing != true) {
            loadingDialog?.show()
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
        ImageUtils.cleanupTempFiles(this)
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        const val MAX_IMAGE_COUNT = 10
        fun getIntent(context: Context, storeId: String) =
            Intent(context, BossReviewWriteActivity::class.java).apply {
                putExtra(KEY_STORE_ID, storeId)
            }
    }
}