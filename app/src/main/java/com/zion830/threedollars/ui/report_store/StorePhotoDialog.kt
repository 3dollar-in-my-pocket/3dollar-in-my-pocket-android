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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStorePhotoBinding
import com.zion830.threedollars.ui.addstore.ui_model.StoreImage
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.report_store.adapter.StoreImageSliderAdapter
import com.zion830.threedollars.ui.report_store.adapter.StorePreviewImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener

@AndroidEntryPoint
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
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = FragmentStorePhotoBinding.inflate(inflater)
        binding.viewModel = viewModel

        val startIndex = arguments?.getInt(KEY_START_INDEX) ?: 0
        val adapter = StoreImageSliderAdapter()
        val indicatorAdapter = StorePreviewImageAdapter(object : OnItemClickListener<StoreImage> {
            override fun onClick(item: StoreImage) {
                binding.slider.smoothScrollToPosition(item.index)
            }
        })

        if (startIndex > 0) {
            indicatorAdapter.updateFocusedIndex(startIndex)
            binding.slider.scrollToPosition(startIndex)
        }

        binding.slider.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.slider)
        binding.rvIndicator.adapter = indicatorAdapter
        binding.slider.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        indicatorAdapter.updateFocusedIndex(position)
                    }
                })
        )

        viewModel.storeInfo.observe(this) {
            it?.let {
                adapter.submitList(it.images)
                indicatorAdapter.submitList(it.images.mapIndexed { index, value ->
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
                    val selectedPosition = (binding.slider.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0
                    viewModel.deletePhoto(adapter.getItems()[selectedPosition])
                    dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .setTitle(R.string.delete_photo_title)
                .setMessage(R.string.delete_photo_msg)
                .create()
                .show()
        }
        binding.btnBack.setOnClickListener { dismiss() }

        return binding.root
    }

    companion object {
        private const val KEY_START_INDEX = "start_index"

        fun getInstance(startIndex: Int) = StorePhotoDialog().apply {
            val bundle = Bundle()
            bundle.putInt(KEY_START_INDEX, startIndex)
            arguments = bundle
        }
    }
}