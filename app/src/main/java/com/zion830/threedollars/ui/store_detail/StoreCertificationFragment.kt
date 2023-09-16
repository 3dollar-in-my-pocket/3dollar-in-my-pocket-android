package com.zion830.threedollars.ui.store_detail

import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.naver.maps.geometry.LatLng
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.LayoutCertificationBinding
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.mypage.adapter.bindMenuIcons
import com.zion830.threedollars.ui.store_detail.StoreCertificationAvailableFragment.Companion.MIN_DISTANCE
import com.zion830.threedollars.ui.store_detail.map.StoreCertificationNaverMapFragment
import com.zion830.threedollars.ui.store_detail.vm.StoreCertificationViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.LegacySharedPrefUtils
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseFragment
import zion830.com.common.ext.addNewFragment
import zion830.com.common.ext.toFormattedNumber
import kotlin.math.abs
import kotlin.math.min

@AndroidEntryPoint
class StoreCertificationFragment : LegacyBaseFragment<LayoutCertificationBinding, StoreCertificationViewModel>(R.layout.layout_certification) {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private val storeDetailViewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var naverMapFragment: StoreCertificationNaverMapFragment

    private var progress: Int = 0

    private var minX: Float = 0f
    private var maxWidth: Int = 0

    override fun initView() {
        minX = binding.progressIndicator.x
        maxWidth = binding.viewProgressBackground.measuredWidth - SizeUtils.dpToPx(8f)
        binding.progressIndicator.layoutParams.width = 0
        binding.tvDistance.text = buildSpannedString {
            append("인증까지 ")
            bold { append("?m") }
        }

        binding.tvTitle.text = buildSpannedString {
            append("가게 근처에서\n")
            bold { append("방문을 인증") }
            append("할 수 있어요!")
        }

        naverMapFragment = StoreCertificationNaverMapFragment()
        binding.ibClose.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        observeUiData()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()

        naverMapFragment.updateMyLatestLocation {
            bindDistance(it)
        }

        viewModel.needUpdate.observe(viewLifecycleOwner) {
            naverMapFragment.updateMyLatestLocation {
                val distance = bindDistance(it)
                val temp = progress
                binding.ivProgress.x = min(minX, binding.ivProgress.x + (maxWidth * ((temp - progress) / 100.0)).toInt())
            }
        }
    }

    private fun bindDistance(it: LatLng?): Float {
        val distance = NaverMapUtils.calculateDistance(it, storeDetailViewModel.storeLocation.value)
        if (distance <= MIN_DISTANCE) {
            startCertification()
        }
        binding.tvDistance.text = buildSpannedString {
            append("인증까지 ")
            bold { append("${(distance - MIN_DISTANCE).toInt().toFormattedNumber()}m") }
        }
        progress = 100 - abs((distance - MIN_DISTANCE) / MIN_DISTANCE * 100).toInt()
        binding.progressIndicator.progress = progress
        return distance
    }

    private fun startCertification() {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.container,
            StoreCertificationAvailableFragment(),
            StoreCertificationAvailableFragment::class.java.name,
            false
        )
    }

    private fun observeUiData() {
        val categories = LegacySharedPrefUtils.getCategories()
        storeDetailViewModel.storeInfo.observe(viewLifecycleOwner) {
            binding.tvStoreName.text = it?.storeName
            binding.ivCategory.bindMenuIcons(it?.categories)
            binding.ivEnd.bindMenuIcons(it?.categories)
            binding.tvStoreCategory.text = it?.categories?.joinToString(" ") { category ->
                "#${categories.find { categoryInfo -> categoryInfo.category == category }?.name}"
            }
        }
    }
}