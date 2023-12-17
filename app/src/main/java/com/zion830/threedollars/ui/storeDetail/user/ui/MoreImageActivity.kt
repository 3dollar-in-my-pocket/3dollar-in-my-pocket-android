package com.zion830.threedollars.ui.storeDetail.user.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.showSnack
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.Constants
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityMoreImageBinding
import com.zion830.threedollars.ui.dialog.StorePhotoDialog
import com.zion830.threedollars.ui.storeDetail.user.adapter.MoreImageAdapter
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.FileUtils
import com.zion830.threedollars.utils.goToPermissionSetting
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class MoreImageActivity : BaseActivity<ActivityMoreImageBinding, StoreDetailViewModel>({ ActivityMoreImageBinding.inflate(it) }) {

    override val viewModel: StoreDetailViewModel by viewModels()

    private var progressDialog: AlertDialog? = null

    private val storeId: Int by lazy {
        intent.getIntExtra(STORE_ID, -1)
    }
    private val adapter: MoreImageAdapter by lazy {
        MoreImageAdapter(object : OnItemClickListener<Int> {
            override fun onClick(item: Int) {
                StorePhotoDialog.getInstance(item, storeId).show(supportFragmentManager, StorePhotoDialog::class.java.name)
            }
        })
    }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun initView() {
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        initViewModel()
        initAdapter()
        initButton()
        initFlow()
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "MoreImageActivity",screenName = null)
    }
    private fun initViewModel() {
        viewModel.getImage(storeId)
    }

    private fun initAdapter() {
        binding.photoRecyclerView.adapter = adapter
    }

    private fun initButton() {
        binding.backButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        binding.submitPhotoTextView.setOnClickListener {
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
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.imagePagingData.collectLatest {
                        it?.let { pagingData ->
                            adapter.submitData(PagingData.empty())
                            adapter.submitData(pagingData)
                        }
                    }
                }

                launch {
                    viewModel.uploadImageStatus.collect {
                        if (it) {
                            if (progressDialog == null) {
                                progressDialog = AlertDialog.Builder(this@MoreImageActivity)
                                    .setCancelable(false)
                                    .setView(R.layout.layout_image_upload_progress)
                                    .create()
                            }
                            progressDialog?.show()
                        } else {
                            progressDialog?.dismiss()
                        }
                    }
                }
            }
        }
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


    companion object {
        const val STORE_ID = "storeId"
        fun getIntent(context: Context, storeId: Int?) =
            Intent(context, MoreImageActivity::class.java).apply {
                storeId?.let {
                    putExtra(STORE_ID, storeId)
                }
            }
    }
}