package com.zion830.threedollars.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.threedollar.domain.home.data.store.ImageModel
import com.threedollar.common.listener.OnSnapPositionChangeListener
import com.threedollar.common.listener.SnapOnScrollListener
import com.zion830.threedollars.databinding.FragmentReviewPhotoBinding
import com.zion830.threedollars.ui.storeDetail.boss.adapter.ReviewImageSliderAdapter
import zion830.com.common.base.onSingleClick

class ReviewPhotoDialog : DialogFragment() {
    
    private lateinit var binding: FragmentReviewPhotoBinding
    private var currentPosition = 0
    private val images: List<ImageModel> by lazy { 
        arguments?.getParcelableArrayList<ImageModel>(KEY_IMAGES) ?: emptyList()
    }
    private val adapter: ReviewImageSliderAdapter by lazy { ReviewImageSliderAdapter() }
    private var isInitScroll = false

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
        binding = FragmentReviewPhotoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initButton()
        setupImages()
    }

    private fun initAdapter() {
        binding.slider.adapter = adapter
        currentPosition = arguments?.getInt(KEY_START_INDEX) ?: 0

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.slider)
        
        binding.slider.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper, 
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (isInitScroll) {
                            currentPosition = position
                        }
                    }
                }
            )
        )
    }

    private fun initButton() {
        binding.leftButton.onSingleClick {
            if (currentPosition > 0) {
                currentPosition -= 1
                binding.slider.smoothScrollToPosition(currentPosition)
            } else if (adapter.itemCount > 1) {
                currentPosition = adapter.itemCount - 1
                binding.slider.scrollToPosition(currentPosition)
            }
        }

        binding.rightButton.onSingleClick {
            if (currentPosition < adapter.itemCount - 1) {
                currentPosition += 1
                binding.slider.smoothScrollToPosition(currentPosition)
            } else if (adapter.itemCount > 1) {
                currentPosition = 0
                binding.slider.scrollToPosition(currentPosition)
            }
        }
        
        binding.backButton.onSingleClick { 
            dismiss() 
        }
    }

    private fun setupImages() {
        adapter.submitList(images) {
            // 이미지 로드 완료 후 시작 위치로 스크롤
            if (!isInitScroll && currentPosition > 0 && currentPosition < images.size) {
                binding.slider.postDelayed({
                    isInitScroll = true
                    binding.slider.scrollToPosition(currentPosition)
                }, 200)
            } else {
                isInitScroll = true
            }
        }
    }

    companion object {
        private const val KEY_IMAGES = "images"
        private const val KEY_START_INDEX = "start_index"
        
        fun getInstance(images: List<ImageModel>, startIndex: Int = 0): ReviewPhotoDialog {
            return ReviewPhotoDialog().apply {
                val bundle = Bundle().apply {
                    putParcelableArrayList(KEY_IMAGES, ArrayList(images))
                    putInt(KEY_START_INDEX, startIndex)
                }
                arguments = bundle
            }
        }
    }
}