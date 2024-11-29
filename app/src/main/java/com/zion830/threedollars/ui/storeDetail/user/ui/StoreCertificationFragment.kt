package com.zion830.threedollars.ui.storeDetail.user.ui

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.home.domain.data.store.UserStoreModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.addNewFragment
import com.threedollar.common.ext.loadImage
import com.threedollar.common.ext.textPartTypeface
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.LayoutCertificationBinding
import com.zion830.threedollars.ui.map.ui.StoreCertificationNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreCertificationAvailableFragment.Companion.MIN_DISTANCE
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreCertificationViewModel
import com.zion830.threedollars.utils.NaverMapUtils
import com.zion830.threedollars.utils.SizeUtils
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty
import kotlin.math.abs
import kotlin.math.min

@AndroidEntryPoint
class StoreCertificationFragment : BaseFragment<LayoutCertificationBinding, StoreCertificationViewModel>() {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private lateinit var naverMapFragment: StoreCertificationNaverMapFragment

    private var progress: Int = 0

    private var minX: Float = 0f
    private var maxWidth: Int = 0

    private val userStoreModel: UserStoreModel? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(USER_STORE_MODEL, UserStoreModel::class.java)
        } else {
            arguments?.getSerializable(USER_STORE_MODEL) as? UserStoreModel
        }
    }

    override fun initView() {
        minX = binding.progressIndicator.x
        maxWidth = binding.viewProgressBackground.measuredWidth - SizeUtils.dpToPx(8f)
        binding.progressIndicator.layoutParams.width = 0

        initMap()
        initTextView()
        initImageView()
        initButton()

        viewModel.needUpdate.observe(viewLifecycleOwner) {
            naverMapFragment.updateMyLatestLocation {
                bindDistance(it)
                val temp = progress
                binding.ivProgress.x = min(minX, binding.ivProgress.x + (maxWidth * ((temp - progress) / 100.0)).toInt())
            }
        }
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "StoreCertificationFragment", screenName = "visit_store")
    }

    private fun initMap() {
        val storeLatLng = LatLng(
            userStoreModel?.location?.latitude ?: 0.0,
            userStoreModel?.location?.longitude ?: 0.0
        )
        naverMapFragment = StoreCertificationNaverMapFragment.newInstance(latLng = storeLatLng)
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.map_container, naverMapFragment)?.commit()
    }

    private fun initButton() {
        binding.ibClose.onSingleClick {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun bindDistance(it: LatLng?): Float {
        val storeLatLng = LatLng(
            userStoreModel?.location?.latitude ?: 0.0,
            userStoreModel?.location?.longitude ?: 0.0
        )

        val distance = NaverMapUtils.calculateDistance(it, storeLatLng)

        if (distance <= MIN_DISTANCE) {
            startCertification()
        }
        binding.tvDistance.text = getString(R.string.certification_distance, (distance - MIN_DISTANCE).toInt())
        progress = 100 - abs((distance - MIN_DISTANCE) / MIN_DISTANCE * 100).toInt()
        binding.progressIndicator.progress = progress
        return distance
    }

    private fun startCertification() {
        requireActivity().supportFragmentManager.popBackStack()
        requireActivity().supportFragmentManager.addNewFragment(
            R.id.container,
            StoreCertificationAvailableFragment.getInstance(userStoreModel),
            StoreCertificationAvailableFragment::class.java.name,
            false
        )
    }

    private fun initTextView() {
        binding.tvTitle.textPartTypeface("가게 근처", Typeface.BOLD)
        binding.storeNameTextView.text = userStoreModel?.name
        binding.storeCategoryTextView.text = userStoreModel?.categories?.joinToString(" ") { "#${it.name}" }
    }

    private fun initImageView() {
        if (userStoreModel?.categories.isNotNullOrEmpty()) {
            binding.ivCategory.loadImage(userStoreModel?.categories?.first()?.imageUrl)
            binding.ivEnd.loadImage(userStoreModel?.categories?.first()?.imageUrl)
        }
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): LayoutCertificationBinding =
        LayoutCertificationBinding.inflate(inflater, container, false)

    companion object {
        private const val USER_STORE_MODEL = "userStoreModel"
        fun getInstance(userStoreModel: UserStoreModel?) = StoreCertificationFragment().apply {
            userStoreModel?.let {
                val bundle = Bundle()
                bundle.putSerializable(USER_STORE_MODEL, userStoreModel)
                arguments = bundle
            }
        }
    }
}