package com.zion830.threedollars.ui.addstore
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Intent
//import android.graphics.Rect
//import android.os.Handler
//import android.view.View
//import android.widget.EditText
//import androidx.activity.viewModels
//import androidx.lifecycle.observe
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.zion830.threedollars.Constants
//import com.zion830.threedollars.R
//import com.zion830.threedollars.repository.model.MenuType
//import com.zion830.threedollars.repository.model.response.Menu
//import com.zion830.threedollars.ui.addstore.adapter.EditMenuRecyclerAdapter
//import com.zion830.threedollars.ui.addstore.adapter.PhotoRecyclerAdapter
//import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
//import com.zion830.threedollars.utils.*
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody.Companion.asRequestBody
//import zion830.com.common.base.BaseActivity
//import zion830.com.common.listener.OnItemClickListener
//
//
//class AddStoreActivity : BaseActivity<ActivityAddStoreBinding, AddStoreViewModel>(R.layout.fragment_add_store) {
//
//    override val viewModel: AddStoreViewModel by viewModels()
//
//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//
//    private lateinit var photoAdapter: PhotoRecyclerAdapter
//
//    private val menuAdapter: EditMenuRecyclerAdapter = EditMenuRecyclerAdapter()
//
//    private var isFirstOpen = true
//
//    override fun initView() {
//        initKeyboard()
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//
//        binding.btnBack.setOnClickListener {
//            setResult(Activity.RESULT_CANCELED)
//            finish()
//        }
//        binding.btnAdd.setOnClickListener {
//            menuAdapter.addNewRow()
//        }
//        photoAdapter = PhotoRecyclerAdapter(
//            object : OnItemClickListener<StoreImage> {
//                override fun onClick(item: StoreImage) {
//                    if (item.uri == null) {
//                        pickImage()
//                    } else {
//                        showRemoveImageDialog(item.index)
//                    }
//                }
//            }
//        )
//        binding.rad1.callOnClick()
//        binding.rvPhoto.adapter = photoAdapter
//        binding.rvEditMenu.adapter = menuAdapter
//
//        viewModel.newStoreId.observe(this) {
//            if (it >= 0) {
//                setResult(Activity.RESULT_OK)
//                startActivityForResult(StoreDetailActivity.getIntent(this@AddStoreActivity, it), Constants.SHOW_STORE_DETAIL)
//                finish()
//            } else {
//                showToast(R.string.failed_add_store)
//            }
//        }
//        getMenuList()
//        initSubmitButtonEvent()
//    }
//
//    private fun initKeyboard() {
//        var keypadBaseHeight = 0
//
//        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
//            val r = Rect(); // 키보드 위로 보여지는 공간
//            binding.root.getWindowVisibleDisplayFrame(r)
//            val screenHeight = binding.root.rootView.height
//
//            val keypadHeight = screenHeight - r.bottom
//
//            if (keypadBaseHeight == 0) {
//                keypadBaseHeight = keypadHeight
//            }
//
//            if (keypadHeight > screenHeight * 0.15) {
//                binding.btnSubmit.visibility = View.GONE
//                binding.viewSubmitBack.visibility = View.GONE
//            } else {
//                Handler().postDelayed({
//                    if (!isFirstOpen) {
//                        binding.btnSubmit.visibility = View.VISIBLE
//                        binding.viewSubmitBack.visibility = View.VISIBLE
//                    }
//                }, 50)
//            }
//        }
//
//        isFirstOpen = false
//    }
//
//    private fun getMenuList(): List<Menu> {
//        val menuList = arrayListOf<Menu>()
//        for (i in 0 until menuAdapter.itemCount) {
//            binding.rvEditMenu.getChildAt(i)?.let {
//                val name = it.findViewById(R.id.et_name) as EditText
//                val price = it.findViewById(R.id.et_price) as EditText
//                menuList.add(Menu(name = name.text.toString(), price = price.text.toString()))
//            }
//        }
//
//        return menuList.filter { it.name.isNotBlank() && it.price.isNotBlank() }
//    }
//
//    private fun initSubmitButtonEvent() {
//        binding.btnSubmit.setOnClickListener {
//            // 수정
//            viewModel.showLoading()
//            viewModel.addNewStore(
//                category = MenuType.getKey(binding.gridMenu.value),
//                latitude = NaverMapUtils.DEFAULT_LOCATION.latitude,
//                longitude = NaverMapUtils.DEFAULT_LOCATION.longitude,
//                menus = getMenuList(),
//                images = getImageFiles()
//            )
//        }
//    }
//
//    private fun getImageFiles(): List<MultipartBody.Part> {
//        val imageList = ArrayList<MultipartBody.Part>()
//        photoAdapter.currentList.forEach {
//            FileUtils.uriToFile(it.uri)?.run {
//                val requestFile = asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                imageList.add(MultipartBody.Part.createFormData("image", name, requestFile))
//            }
//        }
//        return imageList.toList()
//    }
//
//    private fun showRemoveImageDialog(position: Int) {
//        AlertDialog.Builder(this)
//            .setMessage(R.string.remove_image)
//            .setCancelable(true)
//            .setPositiveButton(R.string.ok) { _, _ -> photoAdapter.removePhoto(position) }
//            .create()
//            .show()
//    }
//
//    fun pickImage() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
//            if (data?.data == null) {
//                showToast(R.string.error_pick_image)
//                return
//            }
//
//            if (FileUtils.isAvailable(data.data)) {
//                showToast(R.string.error_file_size)
//            } else {
//                photoAdapter.addPhoto(data.data)
//            }
//        }
//    }
//
//    companion object {
//        private const val PICK_PHOTO_FOR_AVATAR = 123
//    }
//}