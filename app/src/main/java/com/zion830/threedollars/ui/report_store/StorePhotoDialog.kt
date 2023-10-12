package com.zion830.threedollars.ui.report_store

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentStorePhotoBinding
import com.zion830.threedollars.ui.category.StoreDetailViewModel
import com.zion830.threedollars.ui.report_store.adapter.StoreImageSliderAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorePhotoDialog : DialogFragment() {
    private val viewModel: StoreDetailViewModel by activityViewModels()

    private lateinit var binding: FragmentStorePhotoBinding
    private var currentPosition = 0
    private val adapter: StoreImageSliderAdapter by lazy { StoreImageSliderAdapter() }
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
        savedInstanceState: Bundle?,
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = FragmentStorePhotoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPosition = arguments?.getInt(KEY_START_INDEX) ?: 0

        initAdapter()
        initButton()
    }

    private fun initAdapter() {
        binding.slider.adapter = adapter.apply {
            submitList(viewModel.imageContentModelList.value)
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.slider)
        binding.slider.addOnScrollListener(SnapOnScrollListener(snapHelper, onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                currentPosition = position
            }
        }))

        if (currentPosition > 0) {
            binding.slider.scrollToPosition(currentPosition)
        }
    }

    private fun initButton() {
        binding.leftButton.setOnClickListener {
            if (currentPosition > 0) {
                currentPosition -= 1
                binding.slider.smoothScrollToPosition(currentPosition)
            } else {
                currentPosition = adapter.itemCount - 1
                binding.slider.scrollToPosition(currentPosition)
            }
        }

        binding.rightButton.setOnClickListener {
            if (currentPosition < adapter.itemCount - 1) {
                currentPosition += 1
                binding.slider.smoothScrollToPosition(currentPosition)
            } else {
                currentPosition = 0
                binding.slider.scrollToPosition(currentPosition)
            }
        }
        binding.deleteButton.setOnClickListener {
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
        binding.backButton.setOnClickListener { dismiss() }
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