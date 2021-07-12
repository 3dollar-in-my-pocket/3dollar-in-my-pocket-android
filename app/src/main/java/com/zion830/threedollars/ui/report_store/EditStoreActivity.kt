package com.zion830.threedollars.ui.report_store

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityEditStoreBinding
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.ui.addstore.StoreImage
import com.zion830.threedollars.ui.addstore.adapter.EditMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditPhotoRecyclerAdapter
import com.zion830.threedollars.ui.report_store.map.StoreEditNaverMapFragment
import com.zion830.threedollars.ui.report_store.vm.StoreEditViewModel
import com.zion830.threedollars.utils.*
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_LOCATION
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener


class EditStoreActivity : BaseActivity<ActivityEditStoreBinding, StoreEditViewModel>(R.layout.activity_edit_store) {

    override val viewModel: StoreEditViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val menuAdapter = EditMenuRecyclerAdapter()

    private lateinit var photoAdapter: EditPhotoRecyclerAdapter

    private var currentPosition: LatLng = NaverMapUtils.DEFAULT_LOCATION

    private var storeId = 0

    private var isFirstOpen = true

    override fun initView() {
        initKeyboard()
        storeId = intent.getIntExtra(KEY_STORE_ID, 0)
        photoAdapter = EditPhotoRecyclerAdapter(
            object : OnItemClickListener<StoreImage> {
                override fun onClick(item: StoreImage) {
                    if (item.url.isNullOrBlank() && item.uri == null) {
                        pickImage()
                    } else if (item.uri != null) {
                        showRemoveImageDialog(item.index)
                    } else {
                        // showRemoveImageDialog(item.index)
                        // TODO : 추후 업데이트
                    }
                }
            }
        )
        binding.rvPhoto.adapter = photoAdapter
        binding.rvEditMenu.adapter = menuAdapter
        viewModel.storeInfo.observe(this) {
            val newList = it?.image?.mapIndexed { index, image -> StoreImage(index, null, image.url) }
                ?.plus(listOf(StoreImage(it.image.size ?: 0, null, null)))
            photoAdapter.submitList(newList?.toMutableList())
            menuAdapter.submitList(it?.menu?.toMutableList())
        }
        binding.btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.btnAdd.setOnClickListener {
            menuAdapter.addNewRow()
        }
        binding.btnSubmit.setOnClickListener {
            viewModel.editStore(
                storeName = binding.etLocation.text.toString(),
                storeId = storeId,
                latitude = viewModel.storeInfo.value?.latitude ?: DEFAULT_LOCATION.latitude,
                longitude = viewModel.storeInfo.value?.longitude ?: DEFAULT_LOCATION.longitude,
                menus = getMenuList(),
                images = getImageFiles()
            )
        }

        val naverMapFragment = StoreEditNaverMapFragment(
            object : OnMapTouchListener {
                override fun onTouch() {
                    binding.scroll.requestDisallowInterceptTouchEvent(true)
                }
            }
        )

        supportFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        observeUiData()
        initSubmitButtonEvent()
        viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
    }

    private fun observeUiData() {
        viewModel.editStoreResult.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                showToast(R.string.failed_edit_store)
            }
        }
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

    private fun initKeyboard() {
        var keypadBaseHeight = 0

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect(); // 키보드 위로 보여지는 공간
            binding.root.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.root.rootView.height

            val keypadHeight = screenHeight - r.bottom

            if (keypadBaseHeight == 0) {
                keypadBaseHeight = keypadHeight
            }

            if (keypadHeight > screenHeight * 0.15) {
                binding.btnSubmit.visibility = View.GONE
                binding.viewSubmitBack.visibility = View.GONE
            } else {
                Handler().postDelayed({
                    if (!isFirstOpen) {
                        binding.btnSubmit.visibility = View.VISIBLE
                        binding.viewSubmitBack.visibility = View.VISIBLE
                    }
                }, 50)
            }
        }

        isFirstOpen = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_STORE_INFO && resultCode == Activity.RESULT_OK) {
            viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)
            initMap()
        } else if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data?.data == null) {
                showToast(R.string.error_pick_image)
                return
            }

            if (FileUtils.isAvailable(data.data)) {
                showToast(R.string.error_file_size)
            } else {
                photoAdapter.addPhoto(data.data)
                binding.tvImageCount.text = getString(R.string.edit_photo).format(photoAdapter.itemCount)
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR)
    }

    private fun showRemoveImageDialog(position: Int) {
        AlertDialog.Builder(this)
            .setMessage(R.string.remove_image)
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ -> photoAdapter.removePhoto(position) }
            .create()
            .show()
    }

    private fun initSubmitButtonEvent() {
        binding.btnSubmit.setOnClickListener {
            if (binding.etLocation.text.isNullOrBlank()) {
                showToast("가게 이름을 입력해주세요!")
            } else {
                viewModel.editStore(
                    storeName = binding.etLocation.text.toString(),
                    storeId = storeId,
                    latitude = viewModel.storeInfo.value?.latitude ?: DEFAULT_LOCATION.latitude,
                    longitude = viewModel.storeInfo.value?.longitude ?: DEFAULT_LOCATION.longitude,
                    menus = getMenuList(),
                    images = getImageFiles()
                )
            }
        }
        viewModel.isLoading.observe(this) {
            if (!it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun getImageFiles(): List<MultipartBody.Part> {
        val imageList = ArrayList<MultipartBody.Part>()
        photoAdapter.currentList.forEach {
            FileUtils.uriToFile(it.uri)?.run {
                val requestFile = asRequestBody("multipart/form-data".toMediaTypeOrNull())
                imageList.add(MultipartBody.Part.createFormData("image", name, requestFile))
            }
        }
        return imageList.toList()
    }

    private fun getMenuList(): List<Menu> {
        val menuList = arrayListOf<Menu>()
        for (i in 0 until menuAdapter.itemCount) {
            binding.rvEditMenu.getChildAt(i)?.let {
                val name = it.findViewById(R.id.et_name) as EditText
                val price = it.findViewById(R.id.et_price) as EditText
                menuList.add(
                    Menu(
                        "", 0, name.text.toString(), price.text.toString()
                    )
                )
            }
        }

        return menuList.filter { it.name.isNotBlank() || it.price.isNotBlank() }
    }

    companion object {
        private const val KEY_STORE_ID = "KEY_STORE_ID"
        private const val EDIT_STORE_INFO = 234
        private const val PICK_PHOTO_FOR_AVATAR = 123

        fun getIntent(context: Context, storeId: Int) = Intent(context, EditStoreActivity::class.java).apply {
            putExtra(KEY_STORE_ID, storeId)
        }
    }
}