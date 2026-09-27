package com.zion830.threedollars.ui.storeDetail.user.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.threedollar.common.base.BaseActivity
import com.threedollar.common.ext.showSnack
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityMoreImageBinding
import com.zion830.threedollars.ui.dialog.StorePhotoDialog
import com.zion830.threedollars.ui.storeDetail.user.adapter.MoreImageAdapter
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreDetailViewModel
import com.zion830.threedollars.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

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

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        uploadPickedImages(uris)
    }

    override fun initView() {
        setLightSystemBars()
        this.onBackPressedDispatcher.addCallback(this, backPressedCallback)
        initViewModel()
        initAdapter()
        initButton()
        initFlow()
    }

    private fun initViewModel() {
        viewModel.getImage(storeId)
    }

    private fun initAdapter() {
        binding.photoRecyclerView.adapter = adapter
    }

    private fun initButton() {
        binding.backButton.onSingleClick {
            setResult(RESULT_OK)
            finish()
        }
        binding.submitPhotoTextView.onSingleClick {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun uploadPickedImages(uris: List<Uri>) {
        if (uris.isEmpty()) return
        lifecycleScope.launch {
            val images = getImageFiles(uris)
            if (images != null) {
                viewModel.saveImages(images, storeId)
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

    private fun getImageFiles(data: List<Uri?>): List<MultipartBody.Part>? =
        FileUtils.toImageParts(data) ?: run {
            binding.root.showSnack(CommonR.string.error_file_size)
            null
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