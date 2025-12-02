package com.zion830.threedollars.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.zion830.threedollars.databinding.DialogLoadingProgressBinding

class LoadingProgressDialog(context: Context) : Dialog(context) {
    
    private val binding: DialogLoadingProgressBinding
    
    init {
        binding = DialogLoadingProgressBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    
    /**
     * 원형 프로그래스 모드로 전환 (리뷰 등록 시)
     */
    fun setCircularMode(message: String) {
        binding.progressCircular.isVisible = true
        binding.progressHorizontal.isVisible = false
        binding.tvProgress.isVisible = false
        binding.tvMessage.text = message
    }
    
    /**
     * 수평 프로그래스 모드로 전환 (이미지 업로드 시)
     */
    fun setHorizontalMode(message: String, current: Int, total: Int) {
        binding.progressCircular.isVisible = false
        binding.progressHorizontal.isVisible = true
        binding.tvProgress.isVisible = true
        binding.tvMessage.text = message
        
        val progress = if (total > 0) (current * 100) / total else 0
        binding.progressHorizontal.progress = progress
        binding.tvProgress.text = "$current/$total"
    }
    
    /**
     * 메시지만 업데이트
     */
    fun updateMessage(message: String) {
        binding.tvMessage.text = message
    }
    
    /**
     * 수평 프로그래스만 업데이트
     */
    fun updateProgress(current: Int, total: Int) {
        val progress = if (total > 0) (current * 100) / total else 0
        binding.progressHorizontal.progress = progress
        binding.tvProgress.text = "$current/$total"
    }
}