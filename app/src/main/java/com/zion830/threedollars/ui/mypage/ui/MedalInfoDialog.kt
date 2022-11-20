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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.zion830.threedollars.databinding.DialogMedalInfoBinding
import com.zion830.threedollars.datasource.model.v2.response.my.Medal
import com.zion830.threedollars.ui.mypage.adapter.MedalInfoRecyclerAdapter
import com.zion830.threedollars.ui.mypage.vm.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedalInfoDialog : DialogFragment() {

    private val viewModel: MyPageViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DialogMedalInfoBinding.inflate(inflater)
        val adapter = MedalInfoRecyclerAdapter()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.lifecycleOwner = this
        binding.ibClose.setOnClickListener {
            dismiss()
        }
        binding.rvInfo.adapter = adapter
        viewModel.allMedals.observe(viewLifecycleOwner) {
            adapter.submitList(it + listOf(Medal()))
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = (size.x * 1.0).toInt()
        params?.height = (size.y * 1.0).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}