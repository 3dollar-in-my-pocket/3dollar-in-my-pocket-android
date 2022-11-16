package com.zion830.threedollars.ui.store_detail

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.LayoutCertificationAvailableBinding
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.mypage.adapter.bindMenuIcons
import com.zion830.threedollars.ui.store_detail.map.StoreCertificationNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreCertificationViewModel
import com.zion830.threedollars.utils.SharedPrefUtils
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.BaseFragment

@AndroidEntryPoint
class StoreCertificationAvailableFragment :
    BaseFragment<LayoutCertificationAvailableBinding, StoreCertificationViewModel>(R.layout.layout_certification_available) {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private val storeDetailViewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreCertificationNaverMapFragment

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun initView() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        naverMapFragment = StoreCertificationNaverMapFragment()

        binding.ibClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.tvTitle.text = buildSpannedString {
            append("가게 도착!\n")
            bold { append("방문을 인증") }
            append("해보세요!")
        }
        binding.layoutSuccess.setOnClickListener {
            viewModel.addVisitHistory(storeDetailViewModel.storeInfo.value?.storeId ?: -1, true)
        }
        binding.layoutFail.setOnClickListener {
            viewModel.addVisitHistory(storeDetailViewModel.storeInfo.value?.storeId ?: -1, false)
        }

        observeUiData()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    private fun observeUiData() {
        val categories = SharedPrefUtils.getCategories()
        storeDetailViewModel.storeInfo.observe(viewLifecycleOwner) {
            binding.tvStoreName.text = it?.storeName
            binding.ivCategory.bindMenuIcons(it?.categories)
            binding.tvStoreCategory.text = it?.categories?.joinToString(" ") { category ->
                "#${categories.find { categoryInfo -> categoryInfo.category == category }?.name}"
            }
        }
        viewModel.addVisitHistoryResult.observe(viewLifecycleOwner) {
            when (it) {
                in 200..299 -> {
                    showToast(R.string.add_certification_success)
                    (requireActivity() as? StoreDetailActivity)?.refreshStoreInfo()
                }
                409 -> {
                    showToast(R.string.already_certification)
                }
                else -> {
                    showToast(R.string.failed_certification)
                }
            }
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    companion object {
        const val MIN_DISTANCE = 100
    }
}