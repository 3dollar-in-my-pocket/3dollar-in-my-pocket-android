package com.zion830.threedollars.ui.like

import android.view.HapticFeedbackConstants
import androidx.core.content.ContextCompat
import com.threedollar.domain.home.data.store.StickerModel
import com.zion830.threedollars.databinding.ViewLikeButtonBinding
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

/**
 * 댓글·리뷰 본문 아래 좋아요 버튼을 그린다.
 * 안 누른 상태는 `heart_line`·gray60, 누른 상태는 `heart_fill`·red 이고 0개면 숫자 없이 "좋아요"만 쓴다.
 */
fun ViewLikeButtonBinding.bind(stickers: List<StickerModel>, onClick: () -> Unit) {
    val sticker = stickers.likeSticker
    val context = root.context
    likeIconImageView.setImageResource(if (sticker.reactedByMe) DesignSystemR.drawable.ic_heart_fill else DesignSystemR.drawable.ic_heart_line)
    likeTextView.text = if (sticker.count > 0) context.getString(CommonR.string.str_like, sticker.count) else context.getString(CommonR.string.str_like_empty)
    likeTextView.setTextColor(ContextCompat.getColor(context, if (sticker.reactedByMe) DesignSystemR.color.red else DesignSystemR.color.gray60))
    root.onSingleClick {
        root.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        onClick()
    }
}
