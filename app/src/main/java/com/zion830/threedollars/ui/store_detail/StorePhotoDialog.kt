package com.zion830.threedollars.ui.store_detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.databinding.FragmentStorePhotoBinding
import com.zion830.threedollars.ui.store_detail.adapter.StoreImageSliderAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel

class StorePhotoDialog : DialogFragment() {
    private val viewModel: StoreDetailViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = FragmentStorePhotoBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = StoreImageSliderAdapter()
        binding.slider.setSliderAdapter(adapter, false)
        viewModel.storeInfo.observe(this) {
            it?.let {
                adapter.submitItems(it.image)
            }
        }
        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return binding.root
    }
}