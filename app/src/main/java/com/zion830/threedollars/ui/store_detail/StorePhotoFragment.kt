package com.zion830.threedollars.ui.store_detail

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStorePhotoBinding
import com.zion830.threedollars.ui.store_detail.adapter.StoreImageSliderAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import zion830.com.common.base.BaseFragment

class StorePhotoFragment : BaseFragment<FragmentStorePhotoBinding, StoreDetailViewModel>(R.layout.fragment_store_photo) {

    override val viewModel: StoreDetailViewModel by activityViewModels()

    override fun initView() {
        val adapter = StoreImageSliderAdapter()
        binding.slider.setSliderAdapter(adapter)
        viewModel.storeInfo.observe(this) {
            it?.let {
                adapter.submitItems(it.image)
            }
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}