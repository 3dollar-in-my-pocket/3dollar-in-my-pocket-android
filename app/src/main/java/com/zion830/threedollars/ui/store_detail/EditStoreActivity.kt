package com.zion830.threedollars.ui.store_detail

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityEditStoreBinding
import com.zion830.threedollars.repository.model.response.Menu
import com.zion830.threedollars.ui.addstore.StoreImage
import com.zion830.threedollars.ui.addstore.adapter.AddMenuRecyclerAdapter
import com.zion830.threedollars.ui.addstore.adapter.EditPhotoRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreEditViewModel
import com.zion830.threedollars.utils.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import zion830.com.common.base.BaseActivity
import zion830.com.common.listener.OnItemClickListener

class EditStoreActivity : BaseActivity<ActivityEditStoreBinding, StoreEditViewModel>(R.layout.activity_edit_store) {

    override val viewModel: StoreEditViewModel by viewModels()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mapFragment: SupportMapFragment

    private val menuAdapter = AddMenuRecyclerAdapter()

    private lateinit var photoAdapter: EditPhotoRecyclerAdapter

    private var googleMap: GoogleMap? = null

    private var currentPosition: LatLng = DEFAULT_LOCATION

    private var storeId = 0

    override fun initView() {
        storeId = intent.getIntExtra(KEY_STORE_ID, 0)
        photoAdapter = EditPhotoRecyclerAdapter(
            object : OnItemClickListener<StoreImage> {
                override fun onClick(item: StoreImage) {
                    if (item.url.isNullOrBlank() && item.uri == null) {
                        pickImage()
                    } else {
                        showRemoveImageDialog(item.index)
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
        }
        binding.btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        binding.btnAdd.setOnClickListener {
            menuAdapter.addNewRow()
        }
        binding.btnDelete.setOnClickListener {
            DeleteStoreDialog.getInstance().show(supportFragmentManager, DeleteStoreDialog::class.java.name)
        }
        binding.btnSubmit.setOnClickListener {
            viewModel.editStore(
                storeName = binding.etLocation.text.toString(),
                storeId = storeId,
                latitude = googleMap!!.cameraPosition.target.latitude,
                longitude = googleMap!!.cameraPosition.target.longitude,
                menus = getMenuList(),
                images = getImageFiles()
            )
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            googleMap = map
            initMap()
            binding.btnFindLocation.setOnClickListener {
                moveToCurrentPosition()
            }
            viewModel.storeLocation.observe(this) {
                map.setMarker(it)
                moveCameraTo(it, DEFAULT_ZOOM)
            }
            initSubmitButtonEvent(map)
        }
        binding.btnDelete.setOnClickListener {
            DeleteStoreDialog.getInstance().show(supportFragmentManager, DeleteStoreDialog::class.java.name)
        }
        viewModel.requestStoreInfo(storeId, currentPosition.latitude, currentPosition.longitude)

        viewModel.editStoreResult.observe(this) {
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun initMap() {
        val storeId = intent.getIntExtra(KEY_STORE_ID, 0)
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    if (it != null) {
                        moveCameraTo(it.toLatLng(), DEFAULT_ZOOM)
                        viewModel.requestStoreInfo(storeId, it.latitude, it.longitude)
                    }
                }
                googleMap?.isMyLocationEnabled = true
            }
        } catch (e: SecurityException) {
            Log.e(this::class.java.name, e.message ?: "")
        }
    }

    private fun moveToCurrentPosition() {
        try {
            if (isLocationAvailable() && isGpsAvailable()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnSuccessListener {
                    currentPosition = it.toLatLng()
                    moveCameraTo(it.toLatLng(), googleMap?.cameraPosition?.zoom)
                }
            } else {
                showToast(R.string.find_location_error)
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
        } else if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data?.data == null) {
                showToast(R.string.error_pick_image)
                return
            }

            photoAdapter.addPhoto(data.data)
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

    private fun moveCameraTo(position: LatLng?, zoomLevel: Float?) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel ?: DEFAULT_ZOOM))
    }

    private fun initSubmitButtonEvent(map: GoogleMap) {
        binding.btnSubmit.setOnClickListener {
            if (binding.etLocation.text.isNullOrBlank()) {
                showToast("가게 이름을 입력해주세요!")
            } else {
                viewModel.editStore(
                    storeName = binding.etLocation.text.toString(),
                    storeId = storeId,
                    latitude = map.cameraPosition.target.latitude,
                    longitude = map.cameraPosition.target.longitude,
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
                menuList.add(Menu(name.text.toString(), price.text.toString()))
            }
        }

        return menuList.filter { it.name.isNotBlank() && it.price.isNotBlank() }
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