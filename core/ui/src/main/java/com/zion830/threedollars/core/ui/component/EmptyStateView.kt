package com.zion830.threedollars.core.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.zion830.threedollars.core.ui.R
import com.zion830.threedollars.core.ui.databinding.LayoutEmptyStateViewBinding

/**
 * 빈 상태를 표시하는 공통 컴포넌트
 * 아이콘, 타이틀, 메시지를 설정할 수 있습니다.
 */
class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Note: The layout file name was changed from layout_no_data.xml to layout_empty_state_view.xml
    // to avoid resource name collision with the app module.
    private val binding = LayoutEmptyStateViewBinding.inflate(LayoutInflater.from(context), this, true)
    
    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.EmptyStateView,
            defStyleAttr,
            0
        ) {
            getResourceId(R.styleable.EmptyStateView_emptyStateIcon, 0)
                .takeIf { it != 0 }
                ?.let {
                    binding.ivEmptyIcon.setImageResource(it)
                }

            getResourceId(R.styleable.EmptyStateView_emptyStateTitle, 0)
                .takeIf { it != 0 }
                ?.let {
                    binding.tvEmptyTitle.setText(it)
                }

            getResourceId(R.styleable.EmptyStateView_emptyStateSubTitle, 0).let {
                if (it == 0) {
                    binding.tvEmptySubtitle.isVisible = false
                } else {
                    binding.tvEmptySubtitle.isVisible = true
                    binding.tvEmptySubtitle.setText(it)
                }
            }
        }
    }

    fun setEmptyState(
        @DrawableRes iconRes: Int,
        title: String,
        message: String? = null
    ) {
        binding.ivEmptyIcon.setImageResource(iconRes)
        binding.tvEmptyTitle.text = title
        
        if (message.isNullOrEmpty()) {
            binding.tvEmptySubtitle.visibility = View.GONE
        } else {
            binding.tvEmptySubtitle.visibility = View.VISIBLE
            binding.tvEmptySubtitle.text = message
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