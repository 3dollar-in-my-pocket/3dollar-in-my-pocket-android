package com.zion830.threedollars.ui.store_detail

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.home.domain.data.store.UserStoreModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.textPartTypeface
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.LayoutCertificationAvailableBinding
import com.zion830.threedollars.ui.store_detail.map.StoreCertificationNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreCertificationViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.ext.isNotNullOrEmpty

@AndroidEntryPoint
class StoreCertificationAvailableFragment : BaseFragment<LayoutCertificationAvailableBinding, StoreCertificationViewModel>() {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private lateinit var naverMapFragment: StoreCertificationNaverMapFragment

    private val userStoreModel: UserStoreModel? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(USER_STORE_MODEL, UserStoreModel::class.java)
        } else {
            arguments?.getSerializable(USER_STORE_MODEL) as? UserStoreModel
        }
    }

    override fun initView() {

        initTextView()
        initImageView()
        initButton()
        initFlow()
        initMap()
    }

    private fun initTextView() {
        binding.tvTitle.textPartTypeface("가게 도착!", Typeface.BOLD)
        binding.storeNameTextView.text = userStoreModel?.name
        binding.storeCategoryTextView.text = userStoreModel?.categories?.joinToString(" ") { "#${it.name}" }
    }

    private fun initImageView() {
        if (userStoreModel?.categories.isNotNullOrEmpty()) {
            Glide.with(binding.ivCategory)
                .load(userStoreModel?.categories?.first()?.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivCategory)
        }
    }

    private fun initMap() {
        val storeLatLng = LatLng(
            userStoreModel?.location?.latitude ?: 0.0,
            userStoreModel?.location?.longitude ?: 0.0
        )
        naverMapFragment = StoreCertificationNaverMapFragment(storeLatLng)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    private fun initButton() {
        binding.ibClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.layoutSuccess.setOnClickListener {
            viewModel.postStoreVisit(userStoreModel?.storeId ?: -1, true)
        }
        binding.layoutFailed.setOnClickListener {
            viewModel.postStoreVisit(userStoreModel?.storeId ?: -1, false)
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.storeVisitResult.collect {
                        activity?.supportFragmentManager?.popBackStack()
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): LayoutCertificationAvailableBinding =
        LayoutCertificationAvailableBinding.inflate(inflater, container, false)

    companion object {
        const val MIN_DISTANCE = 100
        private const val USER_STORE_MODEL = "userStoreModel"
        fun getInstance(userStoreModel: UserStoreModel?) = StoreCertificationAvailableFragment().apply {
            userStoreModel?.let {
                val bundle = Bundle()
                bundle.putSerializable(USER_STORE_MODEL, userStoreModel)
                arguments = bundle
            }
        }
    }
}