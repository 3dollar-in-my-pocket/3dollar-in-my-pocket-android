package com.zion830.threedollars.core.ui.extension

import android.view.View
import androidx.core.view.isVisible

/**
 * View 확장함수 모음
 */

/**
 * View의 visibility를 VISIBLE로 설정
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * View의 visibility를 GONE으로 설정
 */
fun View.hide() {
    visibility = View.GONE
}

/**
 * View의 visibility를 INVISIBLE로 설정
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 조건에 따라 View를 보이거나 숨김
 */
fun View.showIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * 조건에 따라 View를 보이거나 투명하게 함
 */
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.INVISIBLE
}

/**
 * 중복 클릭 방지를 위한 Safe Click Listener
 */
fun View.setSafeOnClickListener(
    debounceTime: Long = 500L,
    action: (v: View) -> Unit
) {
    setOnClickListener { v ->
        if (isClickable) {
            isClickable = false
            action(v)
            postDelayed({ isClickable = true }, debounceTime)
        }
    }
}

/**
 * View가 현재 보이는 상태인지 확인
 */
fun View.isShown(): Boolean = isVisible && alpha > 0f