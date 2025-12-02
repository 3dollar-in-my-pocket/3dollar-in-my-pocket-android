package com.zion830.threedollars.core.ui.util

import android.app.AlertDialog
import android.content.Context
import androidx.annotation.StringRes

/**
 * 다이얼로그 관련 유틸리티
 */
object DialogUtils {
    
    /**
     * 확인 다이얼로그를 생성합니다.
     */
    fun showConfirmDialog(
        context: Context,
        title: String? = null,
        message: String,
        positiveText: String = "확인",
        negativeText: String = "취소",
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .apply {
                if (title != null) setTitle(title)
                setMessage(message)
                setPositiveButton(positiveText) { dialog, _ ->
                    onPositiveClick?.invoke()
                    dialog.dismiss()
                }
                setNegativeButton(negativeText) { dialog, _ ->
                    onNegativeClick?.invoke()
                    dialog.dismiss()
                }
                setCancelable(false)
            }
            .show()
    }
    
    /**
     * 확인 다이얼로그 (리소스 ID 사용)
     */
    fun showConfirmDialog(
        context: Context,
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int,
        @StringRes positiveTextRes: Int = android.R.string.ok,
        @StringRes negativeTextRes: Int = android.R.string.cancel,
        onPositiveClick: (() -> Unit)? = null,
        onNegativeClick: (() -> Unit)? = null
    ): AlertDialog {
        return showConfirmDialog(
            context = context,
            title = titleRes?.let { context.getString(it) },
            message = context.getString(messageRes),
            positiveText = context.getString(positiveTextRes),
            negativeText = context.getString(negativeTextRes),
            onPositiveClick = onPositiveClick,
            onNegativeClick = onNegativeClick
        )
    }
    
    /**
     * 알림 다이얼로그를 생성합니다.
     */
    fun showAlertDialog(
        context: Context,
        title: String? = null,
        message: String,
        positiveText: String = "확인",
        onPositiveClick: (() -> Unit)? = null
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .apply {
                if (title != null) setTitle(title)
                setMessage(message)
                setPositiveButton(positiveText) { dialog, _ ->
                    onPositiveClick?.invoke()
                    dialog.dismiss()
                }
                setCancelable(false)
            }
            .show()
    }
    
    /**
     * 알림 다이얼로그 (리소스 ID 사용)
     */
    fun showAlertDialog(
        context: Context,
        @StringRes titleRes: Int? = null,
        @StringRes messageRes: Int,
        @StringRes positiveTextRes: Int = android.R.string.ok,
        onPositiveClick: (() -> Unit)? = null
    ): AlertDialog {
        return showAlertDialog(
            context = context,
            title = titleRes?.let { context.getString(it) },
            message = context.getString(messageRes),
            positiveText = context.getString(positiveTextRes),
            onPositiveClick = onPositiveClick
        )
    }
}