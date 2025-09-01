package com.zion830.threedollars.core.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zion830.threedollars.core.ui.R

/**
 * 빈 상태를 표시하는 공통 컴포넌트
 * 아이콘, 타이틀, 메시지를 설정할 수 있습니다.
 */
class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_no_data, this, true)
        orientation = VERTICAL
    }
    
    fun setEmptyState(
        @DrawableRes iconRes: Int,
        title: String,
        message: String? = null
    ) {
        val iconView = findViewById<ImageView>(R.id.iv_empty_icon)
        val titleView = findViewById<TextView>(R.id.tv_empty_title)
        val subtitleView = findViewById<TextView>(R.id.tv_empty_subtitle)
        
        iconView.setImageResource(iconRes)
        titleView.text = title
        
        if (message.isNullOrEmpty()) {
            subtitleView.visibility = View.GONE
        } else {
            subtitleView.visibility = View.VISIBLE
            subtitleView.text = message
        }
    }
    
    fun setEmptyState(
        @DrawableRes iconRes: Int,
        @StringRes titleRes: Int,
        @StringRes messageRes: Int? = null
    ) {
        setEmptyState(
            iconRes = iconRes,
            title = context.getString(titleRes),
            message = messageRes?.let { context.getString(it) }
        )
    }
    
    companion object {
        /**
         * 빈 상태 뷰를 생성합니다.
         */
        fun create(
            context: Context,
            @DrawableRes iconRes: Int,
            title: String,
            message: String? = null
        ): EmptyStateView {
            return EmptyStateView(context).apply {
                setEmptyState(iconRes, title, message)
            }
        }
    }
}