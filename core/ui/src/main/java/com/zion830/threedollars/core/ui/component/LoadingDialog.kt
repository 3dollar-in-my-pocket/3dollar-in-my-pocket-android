package com.zion830.threedollars.core.ui.component

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import com.zion830.threedollars.core.ui.R

/**
 * 공통 로딩 다이얼로그
 * 전체 화면에 투명한 배경과 로딩 인디케이터를 표시합니다.
 */
class LoadingDialog private constructor(private val context: Context) {
    
    private var dialog: Dialog? = null
    
    fun show(message: String = "로딩 중...") {
        if (dialog?.isShowing == true) return
        
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading_progress, null)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        tvMessage.text = message
        
        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(view)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(0.3f)
            }
        }
        
        try {
            dialog?.show()
        } catch (e: Exception) {
            // Activity가 종료된 상태에서 다이얼로그를 띄우려 할 때 예외 처리
            e.printStackTrace()
        }
    }
    
    fun dismiss() {
        try {
            dialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dialog = null
        }
    }
    
    val isShowing: Boolean
        get() = dialog?.isShowing == true
    
    companion object {
        @Volatile
        private var INSTANCE: LoadingDialog? = null
        
        fun getInstance(context: Context): LoadingDialog {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoadingDialog(context).also { INSTANCE = it }
            }
        }
        
        fun show(context: Context) {
            getInstance(context).show()
        }
        
        fun dismiss(context: Context) {
            getInstance(context).dismiss()
        }
    }
}