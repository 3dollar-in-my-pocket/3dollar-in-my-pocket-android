package com.zion830.threedollars.ui.mypage.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.DialogAddReviewBinding
import com.zion830.threedollars.datasource.model.v2.request.NewReview
import com.zion830.threedollars.datasource.model.v2.response.my.ReviewDetail
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditReviewDialog(
    private val content: ReviewDetail?,
    private val onComplete: (NewReview) -> Unit
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DialogAddReviewBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.lifecycleOwner = this
        binding.ibClose.setOnClickListener {
            dismiss()
        }
        binding.etContent.setText(content?.contents)
        binding.rating.rating = content?.rating ?: 0f
        binding.btnFinish.setOnClickListener {
            when {
                binding.rating.rating == 0f -> {
                    showToast(R.string.over_rating_1)
                }
                binding.etContent.text.isBlank() -> {
                    showToast(R.string.input_content)
                }
                else -> {
                    val newReview =
                        NewReview(binding.etContent.text.toString(), binding.rating.rating)
                    onComplete(newReview)
                    dismiss()
                }
            }
        }
        binding.etContent.addTextChangedListener {
            binding.btnFinish.isEnabled = binding.etContent.text.isNotBlank()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}