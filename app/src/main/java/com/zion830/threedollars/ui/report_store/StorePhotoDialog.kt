package com.zion830.threedollars.ui.report_store

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStorePhotoBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import com.zion830.threedollars.ui.report_store.adapter.StoreImageSliderAdapter
import com.zion830.threedollars.ui.report_store.adapter.StorePreviewImageAdapter
import com.zion830.threedollars.ui.store_detail.vm.StoreDetailViewModel
import zion830.com.common.listener.OnItemClickListener

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = FragmentStorePhotoBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = StoreImageSliderAdapter()
        val indicatorAdapter = StorePreviewImageAdapter(object : OnItemClickListener<StoreImage> {
            override fun onClick(item: StoreImage) {
                binding.slider.currentPagePosition = item.index
            }
        })

        binding.slider.setSliderAdapter(adapter, false)
        binding.rvIndicator.adapter = indicatorAdapter

        viewModel.storeInfo.observe(this) {
            it?.let {
                adapter.submitItems(it.image ?: emptyList())
                indicatorAdapter.submitList(it.image?.mapIndexed { index, value ->
                    StoreImage(
                        index,
                        null,
                        value.url
                    )
                })
            }
        }
        binding.ibTrash.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setPositiveButton(R.string.ok) { _, _ ->
                    viewModel.deletePhoto(adapter.getItem(binding.slider.currentPagePosition))
                    dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    dismiss()
                }
                .setTitle(R.string.delete_photo_title)
                .setMessage(R.string.delete_photo_msg)
                .create()
                .show()
        }
        binding.btnBack.setOnClickListener { dismiss() }

        return binding.root
    }
}