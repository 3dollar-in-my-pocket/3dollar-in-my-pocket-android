package com.zion830.threedollars.core.ui.component

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.zion830.threedollars.core.ui.R

/**
 * 커스텀 토스트 컴포넌트
 * 블랙 배경의 커스텀 디자인 토스트를 제공합니다.
 */
object CustomToast {
    
    /**
     * 커스텀 디자인의 토스트를 표시합니다.
     * 
     * @param context 컨텍스트
     * @param message 표시할 메시지
     * @param duration 토스트 지속 시간 (Toast.LENGTH_SHORT 또는 Toast.LENGTH_LONG)
     */
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_toast_black, null)
        val toastTextView = view.findViewById<TextView>(R.id.toastTextView)
        toastTextView.text = message
        
        Toast(context).apply {
            setGravity(Gravity.CENTER, 0, 0)
            this.duration = duration
            this.view = view
            show()
        }
    }
    
    /**
     * 짧은 시간 동안 토스트를 표시합니다.
     */
    fun showShort(context: Context, message: String) {
        show(context, message, Toast.LENGTH_SHORT)
    }
    
    /**
     * 긴 시간 동안 토스트를 표시합니다.
     */
    fun showLong(context: Context, message: String) {
        show(context, message, Toast.LENGTH_LONG)
    }
    
    /**
     * 리소스 ID로 토스트를 표시합니다.
     */
    fun show(context: Context, messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        show(context, context.getString(messageRes), duration)
    }
    
    /**
     * 리소스 ID로 짧은 토스트를 표시합니다.
     */
    fun showShort(context: Context, messageRes: Int) {
        show(context, messageRes, Toast.LENGTH_SHORT)
    }
    
    /**
     * 리소스 ID로 긴 토스트를 표시합니다.
     */
    fun showLong(context: Context, messageRes: Int) {
        show(context, messageRes, Toast.LENGTH_LONG)
    }
}