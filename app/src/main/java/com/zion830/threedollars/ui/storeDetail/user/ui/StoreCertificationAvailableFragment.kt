package com.zion830.threedollars.ui.storeDetail.user.ui

import android.app.Activity
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.threedollar.domain.home.data.store.UserStoreModel
import com.naver.maps.geometry.LatLng
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.ext.loadImage
import com.threedollar.common.ext.textPartTypeface
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.LayoutCertificationAvailableBinding
import com.zion830.threedollars.ui.map.ui.StoreCertificationNaverMapFragment
import com.zion830.threedollars.ui.storeDetail.user.viewModel.StoreCertificationViewModel
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import zion830.com.common.ext.isNotNullOrEmpty
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class StoreCertificationAvailableFragment : BaseFragment<LayoutCertificationAvailableBinding, StoreCertificationViewModel>() {

    override val viewModel: StoreCertificationViewModel by viewModels()

    private lateinit var naverMapFragment: StoreCertificationNaverMapFragment

    private var pendingVisitExists = false

    private val userStoreModel: UserStoreModel? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(USER_STORE_MODEL, UserStoreModel::class.java)
        } else {
            arguments?.getSerializable(USER_STORE_MODEL) as? UserStoreModel
        }
    }
    private val finishActivityOnClose: Boolean by lazy {
        arguments?.getBoolean(FINISH_ACTIVITY_ON_CLOSE, false) ?: false
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
            binding.ivCategory.loadImage(userStoreModel?.categories?.first()?.imageUrl)
        }
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
            closeCertificationFlow(Activity.RESULT_CANCELED)
        }
        binding.layoutSuccess.onSingleClick {
            pendingVisitExists = true
            viewModel.sendClickVisitSuccess()
            viewModel.postStoreVisit(userStoreModel?.storeId ?: -1, true)
        }
        binding.layoutFailed.onSingleClick {
            pendingVisitExists = false
            viewModel.sendClickVisitFail()
            viewModel.postStoreVisit(userStoreModel?.storeId ?: -1, false)
        }
    }

    private fun initFlow() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.storeVisitResult.collect { isSuccess ->
                        if (!isSuccess) return@collect
                        if (pendingVisitExists) {
                            showToast(CommonR.string.add_certification_success)
                        }
                        closeCertificationFlow(Activity.RESULT_OK)
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

    private fun closeCertificationFlow(resultCode: Int) {
        if (finishActivityOnClose) {
            requireActivity().setResult(resultCode)
            requireActivity().finish()
        } else {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        const val MIN_DISTANCE = 100
        private const val USER_STORE_MODEL = "userStoreModel"
        private const val FINISH_ACTIVITY_ON_CLOSE = "finishActivityOnClose"

        fun getInstance(
            userStoreModel: UserStoreModel?,
            finishActivityOnClose: Boolean = false,
        ) = StoreCertificationAvailableFragment().apply {
            val bundle = Bundle().apply {
                putBoolean(FINISH_ACTIVITY_ON_CLOSE, finishActivityOnClose)
                userStoreModel?.let {
                    putSerializable(USER_STORE_MODEL, userStoreModel)
                }
            }
            arguments = bundle
        }
    }
}
